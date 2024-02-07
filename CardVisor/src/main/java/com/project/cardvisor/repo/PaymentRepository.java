package com.project.cardvisor.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.PaymentsVO;

public interface PaymentRepository extends CrudRepository<PaymentsVO, String>{
	
	//해외 토탈 결제 금액
	@Query("select sum(p.pay_amount) from PaymentsVO p where p.currency_code != 'KRW'")
	Long selectTotalOverseasPayment();
}
