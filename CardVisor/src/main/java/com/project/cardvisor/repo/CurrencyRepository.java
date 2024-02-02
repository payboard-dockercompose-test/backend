package com.project.cardvisor.repo;

import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.CurrencyVO;



public interface CurrencyRepository extends CrudRepository<CurrencyVO, String>{

}
