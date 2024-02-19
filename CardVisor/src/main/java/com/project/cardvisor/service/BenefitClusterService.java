package com.project.cardvisor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.cardvisor.dto.BenefitDTO;
import com.project.cardvisor.repo.BenefitRepository;
import com.project.cardvisor.repo.MccCodeRepository;
import com.project.cardvisor.vo.MccVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BenefitClusterService {

	private final BenefitRepository brep;
	private final MccCodeRepository mrep;

	public List<Map<String, Object>> benefitDetailByCategory(String category, String date) {
		
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

		Collections.sort(benefitTotalInfo, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Long val1 = (Long) o1.get("total_sum");
				Long val2 = (Long) o2.get("total_sum");
				return val2.compareTo(val1);
			}

		});
		
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
		
		result.put("list",benefitList);

		return result;
	}

	// 전체 끌어오는 로직
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
		root.put("name", "Benfit By MCC");
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
