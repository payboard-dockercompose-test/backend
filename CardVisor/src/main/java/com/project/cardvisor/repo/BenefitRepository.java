package com.project.cardvisor.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.cardvisor.vo.BenefitVO;

import jakarta.persistence.Tuple;

public interface BenefitRepository extends CrudRepository<BenefitVO, Integer>{
	
	@Query(value = "select * from benefit b "
			+ "where b.benefit_id in "
			+ "(select cb.benefit_id from card_benefit cb "
			+ "where cb.card_type = "
			+ "(select cl.card_type  from card_list cl "
			+ "where cl.card_type = "
			+ "(select cri.card_type from card_reg_info cri "
			+ "where cri.reg_id = ("
			+ "select p.reg_id "
			+ "from payments p "
			+ "where p.pay_id = :pay_id "
			+ ")))) "
			+ "group by mcc_code", nativeQuery = true)
	List<BenefitVO> findByPay_id(@Param("pay_id") String pay_id);
	
	@Query(value = "SELECT m.ctg_name, SUM(p.benefit_amount) benefit_amount_sum, b.benefit_id, b.benefit_detail, b.benefit_pct, cl.card_name, b.interest_id, b.mcc_code "
			+ "FROM payments p "
			+ "JOIN card_reg_info cri ON p.reg_id = cri.reg_id "
			+ "JOIN card_list cl ON cri.card_type = cl.card_type "
			+ "JOIN card_benefit cb ON cl.card_type = cb.card_type "
			+ "JOIN benefit b ON cb.benefit_id = b.benefit_id "
			+ "JOIN mcc m ON p.mcc_code = m.mcc_code "
			+ "WHERE p.pay_date < CURRENT_TIMESTAMP() "
			+ "AND p.benefit_amount > 0 "
			+ "GROUP BY b.benefit_id "
			+ "ORDER BY b.mcc_code asc,SUM(p.benefit_amount) desc, b.benefit_id asc", nativeQuery = true)
	List<Tuple> findByMCC();
	
	@Query(value = "SELECT DISTINCT(m.ctg_name) "
			+ "FROM payments p join mcc m "
			+ "on p.mcc_code = m.mcc_code "
			+ "WHERE p.benefit_amount > 0 "
			+ "AND p.pay_date < CURRENT_TIMESTAMP() "
			+ "ORDER BY p.mcc_code asc;", nativeQuery = true)
	List<String> findDistinctMCC();
}
