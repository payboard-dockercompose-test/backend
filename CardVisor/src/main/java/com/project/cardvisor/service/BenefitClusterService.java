package com.project.cardvisor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
	
	public LinkedList<Object> benefitByMCC(){
		
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
	    System.out.println("시작전");
	    List<BenefitDTO> benefitDtos = brep.findByMCC().stream()
	    	    .map(tuple -> {
	    	        BenefitDTO dto = new BenefitDTO();
	    	        dto.setCtg_name(tuple.get("ctg_name",String.class));
	    	        dto.setMcc_code(tuple.get("mcc_code",String.class));
	    	        dto.setBenefit_amount_sum(tuple.get("benefit_amount_sum", BigDecimal.class).intValue());
	    	        dto.setBenefit_id(tuple.get("benefit_id", Integer.class));
	    	        dto.setBenefit_detail(tuple.get("benefit_detail", String.class));
	    	        dto.setBenefit_pct(tuple.get("benefit_pct", Double.class));
	    	        dto.setCard_name(tuple.get("card_name", String.class));
	    	        dto.setInterest_id(tuple.get("interest_id", Integer.class));
	    	        return dto;
	    	    })
	    	    .collect(Collectors.toList());
	    System.out.println("시작");
	    benefitDtos.forEach(b-> {
	    	String curCtg = (String)b.getCtg_name();
	    	System.out.println(curCtg);
	    	((ArrayList<Object>) root.get("children")).forEach(i -> {
	    		if(((LinkedHashMap<String, Object>)(i)).get("name").equals(curCtg)) {
	    			System.out.println(curCtg+"일치");
	    			LinkedHashMap<String, Object> cur_row = new LinkedHashMap<String, Object>();
	    			cur_row.put("name",(String)b.getBenefit_detail());
	    			cur_row.put("value",(Integer)b.getBenefit_amount_sum());
	    			System.out.println(cur_row.get("value"));
	    			((ArrayList<Object>)((LinkedHashMap<String, Object>)(i)).get("children")).add(cur_row);
	    		}
	    	});
	    });
	    System.out.println("종료");
	    
		return benfitList;
	}
}
