package com.project.cardvisor.repo;

import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.PaymentsVO;

public interface PaymentRepository extends CrudRepository<PaymentsVO, String>{

}
