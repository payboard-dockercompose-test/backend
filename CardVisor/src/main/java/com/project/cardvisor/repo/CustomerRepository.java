package com.project.cardvisor.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.CustomerVO;

public interface CustomerRepository extends CrudRepository<CustomerVO, String> {
	
	//직업별 인원 조회
	@Query("SELECT c.job_id.job_type as jobType, COUNT(c) as count FROM CustomerVO c GROUP BY c.job_id.job_type")
	List<Map<String, Object>> findAllJobTypes();
	
	//연봉별 인원 조회
	@Query("SELECT cust_salary as custsalary, COUNT(*) as count FROM CustomerVO GROUP BY cust_salary")
	List<Object[]> findAllCustSalary();

}
