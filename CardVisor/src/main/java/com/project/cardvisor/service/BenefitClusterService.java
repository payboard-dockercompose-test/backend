package com.project.cardvisor.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.cardvisor.dto.BenefitDTO;
import com.project.cardvisor.repo.BenefitRepository;
import com.project.cardvisor.repo.MccCodeRepository;
import com.project.cardvisor.vo.BenefitVO;
import com.project.cardvisor.vo.QCardBenefitVO;
import com.project.cardvisor.vo.QCardRegInfoVO;
import com.project.cardvisor.vo.QCustomerVO;
import com.project.cardvisor.vo.QMccVO;
import com.project.cardvisor.vo.QPaymentsVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BenefitClusterService {

	private final BenefitRepository brep;
	private final MccCodeRepository mrep;

	@PersistenceContext
	private EntityManager entityManager;

	public Map<String, List<Map<String, Object>>> benefitRecommendForValueEstimation(Map<String, Object> data) {

		// db에서 넘어온 컬럼 정리
		boolean[] genArr = null;
		boolean[] jobArr = null;
		boolean[] payArr = null;
		boolean[] ageArr = null;

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			List<Boolean> boolList = (List<Boolean>) entry.getValue();
			boolean[] arr = new boolean[boolList.size()];
			for (int i = 0; i < boolList.size(); i++) {
				arr[i] = boolList.get(i);
			}

			if (entry.getKey().equals("성별")) {
				genArr = arr;
			} else if (entry.getKey().equals("직업")) {
				jobArr = arr;
			} else if (entry.getKey().equals("연소득")) {
				payArr = arr;
			} else if (entry.getKey().equals("연령")) {
				ageArr = arr;
			}
		}

		// 성별, 직업, 연소득, 연령
		// db에서 넘어오는 배열의 index로 값 매핑
		char[] genValList = new char[] { '남', '여' };
		int[] jobValList = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		String[] payValList = new String[] { "3000만원 미만", "3000만원 이상 5000만원 미만", "5000만원 이상 7000만원 미만",
				"7000만원 이상 1억 미만", "1억 이상" };

		List<Character> genList = new LinkedList<>();
		List<Integer> jobList = new LinkedList<>();
		List<String> payList = new LinkedList<>();

		for (int i = 0; i < genArr.length; i++) {
			if (genArr[i])
				genList.add(genValList[i]);
		}

		for (int i = 0; i < jobArr.length; i++) {
			if (jobArr[i])
				jobList.add(jobValList[i]);
		}

		for (int i = 0; i < payArr.length; i++) {
			if (payArr[i])
				payList.add(payValList[i]);
		}

		// 나이 변환
		LocalDate now = LocalDate.now();
		String[] ageValList = new String[] { "20", "30", "40", "50", "60", "70대 이상" };
		List<Map<String, LocalDate>> ageRangeList = new LinkedList<>();
		for (int i = 0; i < ageArr.length; i++) {
			if (ageArr[i]) {
				String curAge = ageValList[i];
				Map<String, LocalDate> ageRange = new HashMap<>();
				switch (curAge) {
				case "20":
					ageRange.put("start", now.minusYears(28).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(19).withMonth(12).withDayOfMonth(31));
					break;
				case "30":
					ageRange.put("start", now.minusYears(38).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(29).withMonth(12).withDayOfMonth(31));
					break;
				case "40":
					ageRange.put("start", now.minusYears(48).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(39).withMonth(12).withDayOfMonth(31));
					break;
				case "50":
					ageRange.put("start", now.minusYears(58).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(49).withMonth(12).withDayOfMonth(31));
					break;
				case "60":
					ageRange.put("start", now.minusYears(68).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(59).withMonth(12).withDayOfMonth(31));
					break;
				case "70대 이상":
					ageRange.put("start", now.minusYears(69).withMonth(12).withDayOfMonth(31));
					break;
				}
				ageRangeList.add(ageRange);
			}
		}

		QPaymentsVO pvo = QPaymentsVO.paymentsVO;
		QMccVO mccvo = QMccVO.mccVO;
		QCardRegInfoVO cardvo = QCardRegInfoVO.cardRegInfoVO;
		QCustomerVO custvo = QCustomerVO.customerVO;
		QCardBenefitVO cbvo = QCardBenefitVO.cardBenefitVO;

		// booleanBuidler age 조건 생성
		BooleanBuilder ageCondition = new BooleanBuilder();
		for (Map<String, LocalDate> ageRange : ageRangeList) {
			Date start = java.sql.Date.valueOf(ageRange.get("start"));
			if (ageRange.containsKey("end")) {
				Date end = java.sql.Date.valueOf(ageRange.get("end"));
				ageCondition.or(custvo.custBirth.between(start, end));
			} else {
				ageCondition.or(custvo.custBirth.lt(start));
			}
		}

		JPAQuery<?> query = new JPAQuery<Void>(entityManager);
		List<Tuple> queryResult = query
				.select(pvo.appliedBenefitId, pvo.benefitAmount.sum(), pvo.benefitAmount.count(), mccvo.ctgName)
				.from(pvo).join(mccvo).on(mccvo.mccCode.eq(pvo.mccCode.mccCode))
				.where(pvo.regId.regId
						.in(JPAExpressions.select(cardvo.regId).from(cardvo)
								.where(cardvo.custId.custId.in(JPAExpressions.select(custvo.custId).from(custvo).where(
										custvo.jobId.jobId.in(jobList), ageCondition, custvo.custGender.in(genList),
										custvo.custSalary.in(payList))))),
						pvo.benefitAmount.gt(0))
				.groupBy(pvo.appliedBenefitId).orderBy(mccvo.mccCode.asc(), pvo.benefitAmount.sum().desc()).fetch();

		List<Map<String, Object>> result = new ArrayList<>();
		
		for (Tuple tuple : queryResult) {
			Map<String, Object> map = new HashMap<>();

			Integer benefit_id = tuple.get(pvo.appliedBenefitId);
			map.put("benefit_id", benefit_id);
			map.put("sum_benefit", tuple.get(pvo.benefitAmount.sum()));
			map.put("cnt_benefit", tuple.get(pvo.benefitAmount.count()));
			String ctg_name = tuple.get(mccvo.ctgName);
			map.put("ctg_name", ctg_name);
			
			BenefitVO vo = brep.findById(benefit_id).orElse(null);
			Double cur_pct = vo.getBenefitPct();
			String cur_detail = vo.getBenefitDetail();
			map.put("benefit_pct", cur_pct);
			map.put("benefit_detail", cur_detail);
			
			result.add(map);
		}

		Collections.sort(result, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Long val1 = (Long) o1.get("cnt_benefit");
				Long val2 = (Long) o2.get("cnt_benefit");
				int compareCnt = val2.compareTo(val1);
				if (compareCnt != 0) {
					return val2.compareTo(val1);
				} else {
					Integer val_sum1 = (Integer) o1.get("sum_benefit");
					Integer val_sum2 = (Integer) o2.get("sum_benefit");
					return val_sum2.compareTo(val_sum1);
				}
			}
		});

		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> item = result.get(i);
			item.put("rank", i + 1); // 순위 추가
		}
		
		// 각 혜택의 예상 가치를 계산
		for (Map<String, Object> benefit : result) {
		  // 각 카드별로 각 혜택의 사용 건수, 적립 금액, 카드 자체의 총 결제 금액을 계산
		  List<Map<String, Object>> cardBenefitForValueEstimation = query
		    .select(cardvo.cardType, pvo.appliedBenefitId, pvo.benefitAmount.sum(),
		      pvo.benefitAmount.count(),
		      pvo.payAmount.multiply(pvo.currencyRate).sum())
		    .from(pvo).join(cardvo).on(cardvo.regId.eq(pvo.regId.regId))
		    .where(cardvo.cardType.cardType.in(JPAExpressions.select(cbvo.id.cardType.cardType).from(cbvo).where(cbvo.id.benefitId.benefitId.eq((Integer)benefit.get("benefit_id")))),
		      pvo.payDate.year().eq(2023)) // pay_date는 2023년만 포함
		    .groupBy(cardvo.cardType)
		    .fetch()
		    .stream()
		    .map(innertuple -> {
		      Map<String, Object> innermap = new HashMap<>();
		      innermap.put("card_type", innertuple.get(cardvo.cardType));
		      innermap.put("benefit_id", innertuple.get(pvo.appliedBenefitId));
		      innermap.put("sum_benefit", innertuple.get(pvo.benefitAmount.sum()));
		      innermap.put("cnt_benefit", innertuple.get(pvo.benefitAmount.count()));
		      innermap.put("total_pay_amount", innertuple.get(pvo.payAmount.multiply(pvo.currencyRate).sum()));
		      return innermap;
		    }).collect(Collectors.toList());

		  // 각 혜택의 예상 가치를 계산 및 카드 연회비 추가
		  for (Map<String, Object> cardBenefit : cardBenefitForValueEstimation) {
		    double benefitValue = ((Integer) cardBenefit.get("sum_benefit")).doubleValue() / ((Integer) cardBenefit.get("total_pay_amount")).doubleValue();
		    double expectedBenefitValue = benefitValue * (Long) cardBenefit.get("cnt_benefit");
		    cardBenefit.put("expected_benefit_value", expectedBenefitValue);
		    benefit.put("card_benefit_info", cardBenefit);
		  }
 
		}

		// map에 mcc 별로 구분 지어 넣기.
		// max_cnt 관련 쿼리 수행
		Map<String, List<Map<String, Object>>> groupedResult = new HashMap<>();
		for (Map<String, Object> map : result) {
			String ctg_name = (String) map.get("ctg_name");
			if (!groupedResult.containsKey(ctg_name)) {
				groupedResult.put(ctg_name, new ArrayList<>());
			}
			// 현재 그룹에 아이템이 이미 3개 이상이라면 아이템을 추가하지 않음.
			if (groupedResult.get(ctg_name).size() >= 3) {
				continue;
			} else {
				String max_cnt_card_type = (String) map.get("max_cnt_card_type");
			}
			groupedResult.get(ctg_name).add(map);
		}
		return groupedResult;
	}
	
	//미사용 예정
	public Map<String, List<Map<String, Object>>> benefitRecommendByFilter(Map<String, Object> data) {

		// db에서 넘어온 컬럼 정리
		boolean[] genArr = null;
		boolean[] jobArr = null;
		boolean[] payArr = null;
		boolean[] ageArr = null;

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			List<Boolean> boolList = (List<Boolean>) entry.getValue();
			boolean[] arr = new boolean[boolList.size()];
			for (int i = 0; i < boolList.size(); i++) {
				arr[i] = boolList.get(i);
			}

			if (entry.getKey().equals("성별")) {
				genArr = arr;
			} else if (entry.getKey().equals("직업")) {
				jobArr = arr;
			} else if (entry.getKey().equals("연소득")) {
				payArr = arr;
			} else if (entry.getKey().equals("연령")) {
				ageArr = arr;
			}
		}

		// 성별, 직업, 연소득, 연령
		// db에서 넘어오는 배열의 index로 값 매핑
		char[] genValList = new char[] { '남', '여' };
		int[] jobValList = new int[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		String[] payValList = new String[] { "3000만원 미만", "3000만원 이상 5000만원 미만", "5000만원 이상 7000만원 미만",
				"7000만원 이상 1억 미만", "1억 이상" };

		List<Character> genList = new LinkedList<>();
		List<Integer> jobList = new LinkedList<>();
		List<String> payList = new LinkedList<>();

		for (int i = 0; i < genArr.length; i++) {
			if (genArr[i])
				genList.add(genValList[i]);
		}

		for (int i = 0; i < jobArr.length; i++) {
			if (jobArr[i])
				jobList.add(jobValList[i]);
		}

		for (int i = 0; i < payArr.length; i++) {
			if (payArr[i])
				payList.add(payValList[i]);
		}

		// 나이 변환
		LocalDate now = LocalDate.now();
		String[] ageValList = new String[] { "20", "30", "40", "50", "60", "70대 이상" };
		List<Map<String, LocalDate>> ageRangeList = new LinkedList<>();
		for (int i = 0; i < ageArr.length; i++) {
			if (ageArr[i]) {
				String curAge = ageValList[i];
				Map<String, LocalDate> ageRange = new HashMap<>();
				switch (curAge) {
				case "20":
					ageRange.put("start", now.minusYears(28).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(19).withMonth(12).withDayOfMonth(31));
					break;
				case "30":
					ageRange.put("start", now.minusYears(38).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(29).withMonth(12).withDayOfMonth(31));
					break;
				case "40":
					ageRange.put("start", now.minusYears(48).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(39).withMonth(12).withDayOfMonth(31));
					break;
				case "50":
					ageRange.put("start", now.minusYears(58).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(49).withMonth(12).withDayOfMonth(31));
					break;
				case "60":
					ageRange.put("start", now.minusYears(68).withMonth(1).withDayOfMonth(1));
					ageRange.put("end", now.minusYears(59).withMonth(12).withDayOfMonth(31));
					break;
				case "70대 이상":
					ageRange.put("start", now.minusYears(69).withMonth(12).withDayOfMonth(31));
					break;
				}
				ageRangeList.add(ageRange);
			}
		}

		QPaymentsVO pvo = QPaymentsVO.paymentsVO;
		QMccVO mccvo = QMccVO.mccVO;
		QCardRegInfoVO cardvo = QCardRegInfoVO.cardRegInfoVO;
		QCustomerVO custvo = QCustomerVO.customerVO;

		// booleanBuidler age 조건 생성
		BooleanBuilder ageCondition = new BooleanBuilder();
		for (Map<String, LocalDate> ageRange : ageRangeList) {
			Date start = java.sql.Date.valueOf(ageRange.get("start"));
			if (ageRange.containsKey("end")) {
				Date end = java.sql.Date.valueOf(ageRange.get("end"));
				ageCondition.or(custvo.custBirth.between(start, end));
			} else {
				ageCondition.or(custvo.custBirth.lt(start));
			}
		}

		JPAQuery<?> query = new JPAQuery<Void>(entityManager);
		List<Tuple> queryResult = query
				.select(pvo.appliedBenefitId, pvo.benefitAmount.sum(), pvo.benefitAmount.count(), mccvo.ctgName)
				.from(pvo).join(mccvo).on(mccvo.mccCode.eq(pvo.mccCode.mccCode))
				.where(pvo.regId.regId
						.in(JPAExpressions.select(cardvo.regId).from(cardvo)
								.where(cardvo.custId.custId.in(JPAExpressions.select(custvo.custId).from(custvo).where(
										custvo.jobId.jobId.in(jobList), ageCondition, custvo.custGender.in(genList),
										custvo.custSalary.in(payList))))),
						pvo.benefitAmount.gt(0))
				.groupBy(pvo.appliedBenefitId).orderBy(mccvo.mccCode.asc(), pvo.benefitAmount.sum().desc()).fetch();

		List<Map<String, Object>> result = new ArrayList<>();
		List<Integer> beneList = new ArrayList<>();
		Set<String> ctgList = new HashSet<>();
		for (Tuple tuple : queryResult) {
			Map<String, Object> map = new HashMap<>();

			Integer benefit_id = tuple.get(pvo.appliedBenefitId);
			beneList.add(benefit_id);
			map.put("benefit_id", benefit_id);
			map.put("sum_benefit", tuple.get(pvo.benefitAmount.sum()));
			map.put("cnt_benefit", tuple.get(pvo.benefitAmount.count()));
			String ctg_name = tuple.get(mccvo.ctgName);
			map.put("ctg_name", ctg_name);
			ctgList.add(ctg_name);
			BenefitVO vo = brep.findById(benefit_id).orElse(null);
			Double cur_pct = vo.getBenefitPct();
			String cur_detail = vo.getBenefitDetail();
			map.put("benefit_pct", cur_pct);
			map.put("benefit_detail", cur_detail);
			result.add(map);
		}

		Collections.sort(result, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Long val1 = (Long) o1.get("cnt_benefit");
				Long val2 = (Long) o2.get("cnt_benefit");
				int compareCnt = val2.compareTo(val1);
				if (compareCnt != 0) {
					return val2.compareTo(val1);
				} else {
					Integer val_sum1 = (Integer) o1.get("sum_benefit");
					Integer val_sum2 = (Integer) o2.get("sum_benefit");
					return val_sum2.compareTo(val_sum1);
				}
			}
		});

		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> item = result.get(i);
			item.put("rank", i + 1); // 순위 추가
		}

		// 혜택 연관된 카드의 상세 정보
		// max_cnt 관련 항목 외, max_sum, max_use는 기능에서 미사용
		brep.cardDetailRelatedBenefitForFilteredAction(beneList).forEach(b -> {
			Integer cur_id = (Integer) b.get("benefit_id");
			Integer cur_card_type = (Integer) b.get("card_type");
			String cur_card_name = (String) b.get("card_name");
			Integer cur_card_annual_fee = (Integer) b.get("card_annual_fee");

			// 혜택 별로 카드의 혜택 정보를 조회
			String date = null;
			Map<String, Object> comparison = brep.cardComparison(cur_card_type, cur_id, date);
			Long cur_sum = 0L;
			Long cur_cnt = 0L;
			Long cur_use = 0L;
			if (comparison != null) {
				if (comparison.get("cur_sum") != null) {
					cur_sum = (Long) ((BigDecimal) comparison.get("cur_sum")).longValue();
				}
				if (comparison.get("cur_cnt") != null) {
					cur_cnt = (Long) comparison.get("cur_cnt");
				}
				if (comparison.get("cur_use") != null) {
					cur_use = (Long) comparison.get("cur_use");
				}
			}

			// benefitTotalInfo에서 현재 benefit_id와 일치하는 항목을 찾아서 업데이트
			for (Map<String, Object> benefit : result) {
				if (((Integer) benefit.get("benefit_id")).equals(cur_id)) {
					// max value 체크
					Long max_sum = benefit.containsKey("max_sum") ? (Long) benefit.get("max_sum") : 0L;
					Long max_cnt = benefit.containsKey("max_cnt") ? (Long) benefit.get("max_cnt") : 0L;
					Long max_use = benefit.containsKey("max_use") ? (Long) benefit.get("max_use") : 0L;

					if (cur_sum > max_sum) {
						benefit.put("max_sum_card_type", cur_card_type);
						benefit.put("max_sum_card_name", cur_card_name);
						benefit.put("max_sum", cur_sum);
						benefit.put("max_sum_fee", cur_card_annual_fee);
					}

					if (cur_cnt > max_cnt) {
						benefit.put("max_cnt_card_type", cur_card_type);
						benefit.put("max_cnt_card_name", cur_card_name);
						benefit.put("max_cnt", cur_cnt);
						benefit.put("max_cnt_fee", cur_card_annual_fee);
					}

					if (cur_use > max_use) {
						benefit.put("max_use_card_type", cur_card_type);
						benefit.put("max_use_card_name", cur_card_name);
						benefit.put("max_use", cur_use);
						benefit.put("max_use_fee", cur_card_annual_fee);
					}
				}
			}
		});

		// map에 mcc 별로 구분 지어 넣기.
		// max_cnt 관련 쿼리 수행
		Map<String, List<Map<String, Object>>> groupedResult = new HashMap<>();
		for (Map<String, Object> map : result) {
			String ctg_name = (String) map.get("ctg_name");
			if (!groupedResult.containsKey(ctg_name)) {
				groupedResult.put(ctg_name, new ArrayList<>());
			}
			// 현재 그룹에 아이템이 이미 3개 이상이라면 아이템을 추가하지 않음.
			if (groupedResult.get(ctg_name).size() >= 3) {
				continue;
			} else {
				String max_cnt_card_type = (String) map.get("max_cnt_card_type");
			}
			groupedResult.get(ctg_name).add(map);
		}
		return groupedResult;
	}

	public List<Map<String, Object>> benefitDetailByCategory(String category, String date, String selectOption) {

		String mccCtg = mrep.findByCtgName(category).getMccCode();
		// (기간동안)혜택이 이용된 건수, (기간동안) 혜택으로 총 할인 금액
		List<Map<String, Object>> benefitTotalInfo = new ArrayList<>();
		brep.benefitInfoAndCalData(mccCtg, date).forEach(b -> {
			Map<String, Object> inData = new HashMap<>();
			inData.put("benefit_id", (Integer) b.get("benefit_id"));
			inData.put("benefit_detail", (String) b.get("benefit_detail"));
			inData.put("benefit_pct", (Double) b.get("benefit_pct"));
			inData.put("total_count", (Long) b.get("count_benefit_used"));
			BigDecimal bd = (BigDecimal) b.get("sum_benefit_amount");
			Long total_sum = bd.longValue();
			Long total_use = (Long) b.get("count_using_people");
			inData.put("total_sum", total_sum);
			if (total_sum == 0 || total_use == 0) {
				inData.put("amount_per_person", 0);
			} else {
				inData.put("amount_per_person", total_sum / total_use);
			}
			benefitTotalInfo.add(inData);
		});

		// 특정 혜택의 카드 갯수, 카드 연회비 평균
		brep.cardCalData(mccCtg).forEach(b -> {
			Integer benefit_id = (Integer) b.get("benefit_id");
			Long related_card_cnt = (Long) b.get("related_card_cnt");
			BigDecimal bd = (BigDecimal) b.get("avg_annual_fee");
			Long avg_annual_fee = bd.longValue();
			for (Map<String, Object> data : benefitTotalInfo) {
				if (((Integer) data.get("benefit_id")).equals(benefit_id)) {
					data.put("related_card_cnt", related_card_cnt);
					data.put("avg_annual_fee", avg_annual_fee);
				}
			}
		});

		// 혜택 연관된 카드의 상세 정보
		brep.cardDetailRelatedBenefit(mccCtg).forEach(b -> {
			Integer cur_id = (Integer) b.get("benefit_id");
			Integer cur_card_type = (Integer) b.get("card_type");
			String cur_card_name = (String) b.get("card_name");

			// 혜택 별로 카드의 혜택 정보를 조회
			Map<String, Object> comparison = brep.cardComparison(cur_card_type, cur_id, date);
			Long cur_sum = 0L;
			Long cur_cnt = 0L;
			Long cur_use = 0L;
			if (comparison != null) {
				if (comparison.get("cur_sum") != null) {
					cur_sum = (Long) ((BigDecimal) comparison.get("cur_sum")).longValue();
				}
				if (comparison.get("cur_cnt") != null) {
					cur_cnt = (Long) comparison.get("cur_cnt");
				}
				if (comparison.get("cur_use") != null) {
					cur_use = (Long) comparison.get("cur_use");
				}
			}

			// benefitTotalInfo에서 현재 benefit_id와 일치하는 항목을 찾아서 업데이트
			for (Map<String, Object> benefit : benefitTotalInfo) {
				if (((Integer) benefit.get("benefit_id")).equals(cur_id)) {
					// max value 체크
					Long max_sum = benefit.containsKey("max_sum") ? (Long) benefit.get("max_sum") : 0L;
					Long max_cnt = benefit.containsKey("max_cnt") ? (Long) benefit.get("max_cnt") : 0L;
					Long max_use = benefit.containsKey("max_use") ? (Long) benefit.get("max_use") : 0L;

					if (cur_sum > max_sum) {
						benefit.put("max_sum_card_type", cur_card_type);
						benefit.put("max_sum_card_name", cur_card_name);
						benefit.put("max_sum", cur_sum);
					}

					if (cur_cnt > max_cnt) {
						benefit.put("max_cnt_card_type", cur_card_type);
						benefit.put("max_cnt_card_name", cur_card_name);
						benefit.put("max_cnt", cur_cnt);
					}

					if (cur_use > max_use) {
						benefit.put("max_use_card_type", cur_card_type);
						benefit.put("max_use_card_name", cur_card_name);
						benefit.put("max_use", cur_use);
					}
				}
			}
		});
		if (selectOption.equals("high")) {
			Collections.sort(benefitTotalInfo, new Comparator<Map<String, Object>>() {

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Long val1 = (Long) o1.get("total_sum");
					Long val2 = (Long) o2.get("total_sum");
					return val2.compareTo(val1);
				}

			});
		} else {
			Collections.sort(benefitTotalInfo, new Comparator<Map<String, Object>>() {

				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Long val1 = (Long) o1.get("total_sum");
					Long val2 = (Long) o2.get("total_sum");
					return val1.compareTo(val2);
				}

			});
		}

		return benefitTotalInfo;
	}

	public Map<String, Object> benefitTopAndBottomByMCC(String selectOption, String date) {
		List<String> mcclist = brep.findDistinctMCC();
		Map<String, Object> result = new HashMap<>();
		List<BenefitDTO> benefitDtos = null;
		if (date == null) {
			benefitDtos = brep.findByMCC().stream().map(tuple -> {
				BenefitDTO dto = new BenefitDTO();
				dto.setCtg_name(tuple.get("ctg_name", String.class));
				dto.setMcc_code(tuple.get("mcc_code", String.class));
				dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
				dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
				dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
				dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
				dto.setInterest_id(tuple.get("interest_id", Integer.class));
				return dto;
			}).collect(Collectors.toList());
		} else {
			benefitDtos = brep.findByMCCWithDate(date).stream().map(tuple -> {
				BenefitDTO dto = new BenefitDTO();
				dto.setCtg_name(tuple.get("ctg_name", String.class));
				dto.setMcc_code(tuple.get("mcc_code", String.class));
				dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
				dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
				dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
				dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
				dto.setInterest_id(tuple.get("interest_id", Integer.class));
				return dto;
			}).collect(Collectors.toList());
		}

		List<LinkedHashMap<String, Object>> benefitList = new ArrayList<>();
		// top or bottom 분기
		if (selectOption.equals("high")) {
			for (String name : mcclist) {
				LinkedHashMap<String, Object> categoryMap = new LinkedHashMap<>();
				categoryMap.put("category", name);
				categoryMap.put("subData", new ArrayList<>());

				int totalValue = 0;
				int otherValue = 0;
				int benefitCount = 0;
				for (BenefitDTO dto : benefitDtos) {
					if (dto.getCtg_name().equals(name)) {
						benefitCount++;
						if (benefitCount <= 5) {
							LinkedHashMap<String, Object> subDataMap = new LinkedHashMap<>();
							subDataMap.put("category", dto.getBenefit_detail());
							subDataMap.put("value", dto.getBenefit_amount_sum());
							((ArrayList<Object>) categoryMap.get("subData")).add(subDataMap);
						} else {
							otherValue += dto.getBenefit_amount_sum();
						}
						totalValue += dto.getBenefit_amount_sum();
					}
				}

				if (benefitCount > 5) {
					LinkedHashMap<String, Object> subDataMap = new LinkedHashMap<>();
					subDataMap.put("category", "기타");
					subDataMap.put("value", otherValue);
					((ArrayList<Object>) categoryMap.get("subData")).add(subDataMap);
				}

				categoryMap.put("value", totalValue);
				((ArrayList<LinkedHashMap<String, Object>>) (categoryMap.get("subData"))).sort((d1, d2) -> {
					int val1 = (Integer) d1.get("value");
					int val2 = (Integer) d2.get("value");
					return val2 - val1;
				});

				benefitList.add(categoryMap);
			}
			Collections.sort(benefitList, new Comparator<LinkedHashMap<String, Object>>() {

				@Override
				public int compare(LinkedHashMap<String, Object> o1, LinkedHashMap<String, Object> o2) {

					int compare = ((Integer) o2.get("value")).compareTo((Integer) o1.get("value"));
					if (compare != 0) {
						return compare;
					}

					return 0;
				}
			});
			result.put("title", "Top 5");
		} else {
			// bottom 5
			Collections.sort(benefitDtos, new Comparator<BenefitDTO>() {

				@Override
				public int compare(BenefitDTO o1, BenefitDTO o2) {
					int mccComparison = o1.getMcc_code().compareTo(o2.getMcc_code());
					if (mccComparison != 0) {
						return mccComparison;
					} else {
						return Integer.compare(o1.getBenefit_amount_sum(), o2.getBenefit_amount_sum());
					}

				}

			});
			for (String name : mcclist) {
				LinkedHashMap<String, Object> categoryMap = new LinkedHashMap<>();
				categoryMap.put("category", name);
				categoryMap.put("subData", new ArrayList<>());

				int totalValue = 0;
				int otherValue = 0;
				int benefitCount = 0;
				for (BenefitDTO dto : benefitDtos) {
					if (dto.getCtg_name().equals(name)) {
						benefitCount++;
						if (benefitCount <= 5) {
							LinkedHashMap<String, Object> subDataMap = new LinkedHashMap<>();
							subDataMap.put("category", dto.getBenefit_detail());
							subDataMap.put("value", dto.getBenefit_amount_sum());
							((ArrayList<Object>) categoryMap.get("subData")).add(subDataMap);
						} else {
							otherValue += dto.getBenefit_amount_sum();
						}
						totalValue += dto.getBenefit_amount_sum();
					}
				}

				categoryMap.put("value", totalValue);
				((ArrayList<LinkedHashMap<String, Object>>) (categoryMap.get("subData"))).sort((d1, d2) -> {
					int val1 = (Integer) d1.get("value");
					int val2 = (Integer) d2.get("value");
					return val2 - val1;
				});

				benefitList.add(categoryMap);
			}
			Collections.sort(benefitList, new Comparator<LinkedHashMap<String, Object>>() {

				@Override
				public int compare(LinkedHashMap<String, Object> o1, LinkedHashMap<String, Object> o2) {

					int compare = ((Integer) o2.get("value")).compareTo((Integer) o1.get("value"));
					if (compare != 0) {
						return compare;
					}

					return 0;
				}
			});
			result.put("title", "Bottom 5");
		}

		result.put("list", benefitList);

		return result;
	}

	// 전체 끌어오는 로직 : 현재 미사용
	public List<LinkedHashMap<String, Object>> benefitAllByMCC() {
		List<String> mcclist = brep.findDistinctMCC();
		List<LinkedHashMap<String, Object>> benefitList = new ArrayList<>();

		List<BenefitDTO> benefitDtos = brep.findByMCC().stream().map(tuple -> {
			BenefitDTO dto = new BenefitDTO();
			dto.setCtg_name(tuple.get("ctg_name", String.class));
			dto.setMcc_code(tuple.get("mcc_code", String.class));
			dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
			dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
			dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
			dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
			dto.setInterest_id(tuple.get("interest_id", Integer.class));
			return dto;
		}).collect(Collectors.toList());

		for (String name : mcclist) {
			LinkedHashMap<String, Object> categoryMap = new LinkedHashMap<>();
			categoryMap.put("category", name);
			categoryMap.put("subData", new ArrayList<>());

			int totalValue = 0;
			for (BenefitDTO dto : benefitDtos) {
				if (dto.getCtg_name().equals(name)) {
					LinkedHashMap<String, Object> subDataMap = new LinkedHashMap<>();
					subDataMap.put("category", dto.getBenefit_detail());
					subDataMap.put("value", dto.getBenefit_amount_sum());

					((ArrayList<Object>) categoryMap.get("subData")).add(subDataMap);
					totalValue += dto.getBenefit_amount_sum();
				}
			}

			categoryMap.put("value", totalValue);
			((ArrayList<LinkedHashMap<String, Object>>) (categoryMap.get("subData"))).sort((d1, d2) -> {
				int val1 = (Integer) d1.get("value");
				int val2 = (Integer) d2.get("value");
				return val2 - val1;
			});

			benefitList.add(categoryMap);
		}

		return benefitList;
	}

	// treemap 가져오기
	public LinkedList<Object> benefitTreeMapByMCC(String date) {

		List<String> mcclist = brep.findDistinctMCC();

		LinkedList<Object> benfitList = new LinkedList<>();
		Map<String, Object> root = new LinkedHashMap<>();
		root.put("name", "Benefit TreeMap");
		root.put("children", new ArrayList<Object>());
		benfitList.add(root);
		for (String name : mcclist) {
			LinkedHashMap<String, Object> cur = new LinkedHashMap<String, Object>();
			cur.put("name", name);
			cur.put("children", new ArrayList<Object>());
			((ArrayList<Object>) root.get("children")).add(cur);
		}
		List<BenefitDTO> benefitDtos = null;
		if (date == null) {
			benefitDtos = brep.findByMCC().stream().map(tuple -> {
				BenefitDTO dto = new BenefitDTO();
				dto.setCtg_name(tuple.get("ctg_name", String.class));
				dto.setMcc_code(tuple.get("mcc_code", String.class));
				dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
				dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
				dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
				dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
				dto.setInterest_id(tuple.get("interest_id", Integer.class));
				return dto;
			}).collect(Collectors.toList());
		} else {
			benefitDtos = brep.findByMCCWithDate(date).stream().map(tuple -> {
				BenefitDTO dto = new BenefitDTO();
				dto.setCtg_name(tuple.get("ctg_name", String.class));
				dto.setMcc_code(tuple.get("mcc_code", String.class));
				dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
				dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
				dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
				dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
				dto.setInterest_id(tuple.get("interest_id", Integer.class));
				return dto;
			}).collect(Collectors.toList());
		}

		benefitDtos.forEach(b -> {
			String curCtg = (String) b.getCtg_name();
			((ArrayList<Object>) root.get("children")).forEach(i -> {
				if (((LinkedHashMap<String, Object>) (i)).get("name").equals(curCtg)) {
					LinkedHashMap<String, Object> cur_row = new LinkedHashMap<String, Object>();
					cur_row.put("name", (String) b.getBenefit_detail());
					cur_row.put("value", (Integer) b.getBenefit_amount_sum());
					((ArrayList<Object>) ((LinkedHashMap<String, Object>) (i)).get("children")).add(cur_row);
				}
			});
		});
		Collections.sort(benefitDtos, new Comparator<BenefitDTO>() {

			public int compare(BenefitDTO o1, BenefitDTO o2) {
				return o2.getBenefit_amount_sum() - o1.getBenefit_amount_sum();
			}
		});

		return benfitList;
	}
}
