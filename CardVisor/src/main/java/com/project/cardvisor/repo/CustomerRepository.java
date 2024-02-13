package com.project.cardvisor.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.CustomerVO;

public interface CustomerRepository extends CrudRepository<CustomerVO, String> {
	
	//직업별 인원 조회
	@Query("SELECT c.jobId.jobType as jobType, COUNT(c) as count FROM CustomerVO c GROUP BY c.jobId.jobType")
	List<Map<String, Object>> findAllJobTypes();
	
	//연봉별 인원 조회
	@Query("SELECT custSalary as custsalary, COUNT(*) as count FROM CustomerVO GROUP BY custSalary")
	List<Object[]> findAllCustSalary();

	
}
