package com.project.cardvisor.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.cardvisor.vo.BenefitVO;

import jakarta.persistence.Tuple;

public interface BenefitRepository extends CrudRepository<BenefitVO, Integer>{
	
	@Query(value= "select b.* from card_benefit cb "
			+ "join card_reg_info cri on cb.card_type = cri.card_type "
			+ "join benefit b on cb.benefit_id = b.benefit_id "
			+ "where cri.reg_id = :reg_id "
			+ "and b.mcc_code = :mcc_code "
			+ "ORDER by b.benefit_pct desc "
			+ "limit 1", nativeQuery = true)
	BenefitVO selectBenefitPctAndId(@Param("reg_id")String reg_id, @Param("mcc_code")String mcc_code);
	
	@Query(value = "select b.mcc_code, m.ctg_name, b.benefit_detail, sum(p.benefit_amount) benefit_amount_sum, b.benefit_id,  b.benefit_pct, b.interest_id "
			+ "from payments p  "
			+ "join card_reg_info cri on p.reg_id = cri.reg_id "
			+ "join card_benefit cb on cb.card_type = cri.card_type "
			+ "join benefit b on cb.benefit_id = b.benefit_id "
			+ "JOIN mcc m ON b.mcc_code = m.mcc_code "
			+ "WHERE p.pay_date < CURRENT_TIMESTAMP() "
			+ "AND p.benefit_amount > 0 "
			+ "and b.benefit_id = p.applied_benefit_id "
			+ "group BY b.benefit_id "
			+ "ORDER BY b.mcc_code asc, sum(p.benefit_amount) desc, b.benefit_id asc", nativeQuery = true)
	List<Tuple> findByMCC();
	
	@Query(value = "SELECT DISTINCT(m.ctg_name) "
			+ "FROM payments p join mcc m "
			+ "on p.mcc_code = m.mcc_code "
			+ "WHERE p.benefit_amount > 0 "
			+ "AND p.pay_date < CURRENT_TIMESTAMP() "
			+ "ORDER BY p.mcc_code asc", nativeQuery = true)
	List<String> findDistinctMCC();
	
	@Query(value = "select b.benefit_id, count(cb.card_type) card_cnt "
			+ "from benefit b "
			+ "join card_benefit cb on b.benefit_id = cb.benefit_id "
			+ "WHERE b.mcc_code = :mcc_code "
			+ "GROUP BY b.benefit_id", nativeQuery = true)
	ArrayList<LinkedHashMap<String, Object>> cardCntBYMccCtg(@Param("mcc_code") String mcc_code);
	
	@Query(value = "select b.benefit_id, b.benefit_detail, b.benefit_pct, cb.card_type, cl.card_name "
			+ "from card_benefit cb "
			+ "join benefit b on cb.benefit_id = b.benefit_id "
			+ "join card_list cl on cl.card_type =cb.card_type "
			+ "where b.benefit_id in "
			+ "(select b.benefit_id "
			+ "from benefit b "
			+ "WHERE b.mcc_code = :mcc_code)"
			+ "order BY 1", nativeQuery = true)
	ArrayList<LinkedHashMap<String, Object>> benefitDetailByCategory(@Param("mcc_code") String mcc_code);
	
	@Query(value = "select sum(p.benefit_amount) benefit_sum, count(p.benefit_amount) benefit_amount_cnt, count(DISTINCT p.reg_id) reg_id_cnt "
			+ "from payments p "
			+ "join card_reg_info cri on p.reg_id = cri.reg_id "
			+ "WHERE cri.card_type = :card_type "
			+ "and p.applied_benefit_id = :benefit_id", nativeQuery = true)
	ArrayList<LinkedHashMap<String, Object>> accumulateDataByCard(@Param("card_type") String card_type, @Param("benefit_id") int benefit_id);
	
	
}
