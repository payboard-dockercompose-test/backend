package com.project.cardvisor.repo;

import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.MccVO;

public interface MccCodeRepository extends CrudRepository<MccVO, String> {

}
