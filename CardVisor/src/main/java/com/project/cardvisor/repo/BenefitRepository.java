package com.project.cardvisor.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.cardvisor.vo.BenefitVO;

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
	
}
