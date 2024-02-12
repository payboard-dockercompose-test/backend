package com.project.cardvisor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import com.project.cardvisor.dto.BenefitDTO;
import com.project.cardvisor.repo.BenefitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BenefitClusterService {

	private final BenefitRepository brep;

	public Map<String, Object> benefitTopAndBottomByMCC() {
		List<String> mcclist = brep.findDistinctMCC();

		List<BenefitDTO> benefitDtos = brep.findByMCC().stream().map(tuple -> {
			BenefitDTO dto = new BenefitDTO();
			dto.setCtg_name(tuple.get("ctg_name", String.class));
			dto.setMcc_code(tuple.get("mcc_code", String.class));
			dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
			dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
			dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
			dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
			dto.setCard_name(tuple.get("card_name", String.class));
			dto.setInterest_id(tuple.get("interest_id", Integer.class));
			return dto;
		}).collect(Collectors.toList());

		// top 5
		List<LinkedHashMap<String, Object>> benefitTop = new ArrayList<>();
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

			benefitTop.add(categoryMap);
		}

		Collections.sort(benefitTop, new Comparator<LinkedHashMap<String, Object>>() {

			@Override
			public int compare(LinkedHashMap<String, Object> o1, LinkedHashMap<String, Object> o2) {

				int compare = ((Integer) o2.get("value")).compareTo((Integer) o1.get("value"));
				if (compare != 0) {
					return compare;
				}

				return 0;
			}
		});

		// bottom 5
		List<LinkedHashMap<String, Object>> benefitBottom = new ArrayList<>();
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

//			if (benefitCount > 5) {
//				LinkedHashMap<String, Object> subDataMap = new LinkedHashMap<>();
//				subDataMap.put("category", "other");
//				subDataMap.put("value", otherValue);
//				((ArrayList<Object>) categoryMap.get("subData")).add(subDataMap);
//			}

			categoryMap.put("value", totalValue);
			((ArrayList<LinkedHashMap<String, Object>>) (categoryMap.get("subData"))).sort((d1, d2) -> {
				int val1 = (Integer) d1.get("value");
				int val2 = (Integer) d2.get("value");
				return val2 - val1;
			});

			benefitBottom.add(categoryMap);
		}
		
		Collections.sort(benefitBottom, new Comparator<LinkedHashMap<String, Object>>() {

			@Override
			public int compare(LinkedHashMap<String, Object> o1, LinkedHashMap<String, Object> o2) {

				int compare = ((Integer) o2.get("value")).compareTo((Integer) o1.get("value"));
				if (compare != 0) {
					return compare;
				}

				return 0;
			}
		});

		Map<String, Object> result = new HashMap<>();
		result.put("top", benefitTop);
		result.put("bottom", benefitBottom);
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
			dto.setCard_name(tuple.get("card_name", String.class));
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

	public LinkedList<Object> benefitTreeMapByMCC() {

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
		List<BenefitDTO> benefitDtos = brep.findByMCC().stream().map(tuple -> {
			BenefitDTO dto = new BenefitDTO();
			dto.setCtg_name(tuple.get("ctg_name", String.class));
			dto.setMcc_code(tuple.get("mcc_code", String.class));
			dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
			dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
			dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
			dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
			dto.setCard_name(tuple.get("card_name", String.class));
			dto.setInterest_id(tuple.get("interest_id", Integer.class));
			return dto;
		}).collect(Collectors.toList());
		
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

		return benfitList;
	}
}
