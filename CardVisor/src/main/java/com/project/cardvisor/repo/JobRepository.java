package com.project.cardvisor.repo;

import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.JobListVO;

public interface JobRepository extends CrudRepository<JobListVO, String>{
	
}
