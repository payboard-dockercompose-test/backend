package com.project.cardvisor.repo;

import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.CardListVO;

public interface CardListRepository extends CrudRepository<CardListVO, Integer>{
	
}
