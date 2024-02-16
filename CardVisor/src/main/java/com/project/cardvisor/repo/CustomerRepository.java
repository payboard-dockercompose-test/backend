package com.project.cardvisor.repo;

import java.math.BigDecimal;
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

	
	//전체 고객 수
	@Query("SELECT COUNT(c) FROM CustomerVO c")
    long getTotalCustomers();
	//남성 고객 수
    @Query("SELECT COUNT(c) FROM CustomerVO c WHERE c.custGender = '남'")
    long getMaleCustomers();
    //여성 고객 수
    @Query("SELECT COUNT(c) FROM CustomerVO c WHERE c.custGender = '여'")
    long getFemaleCustomers();
    
	//전체(남,여)평균 연령
	@Query("SELECT CAST(c.custGender AS string), AVG(TIMESTAMPDIFF(YEAR, c.custBirth, CURDATE())) FROM CustomerVO c GROUP BY c.custGender "
			+ "UNION ALL " + "SELECT 'all', AVG(TIMESTAMPDIFF(YEAR, c.custBirth, CURDATE())) FROM CustomerVO c")
	List<Object[]> getAverageAge();
	
	//가장 많은 수의 고객을 가진 연봉 카테고리
	@Query(value = "(SELECT 'all', cust_salary FROM customer GROUP BY cust_salary ORDER BY COUNT(*) DESC LIMIT 1) "
			+ "UNION ALL "
			+ "(SELECT 'male', cust_salary FROM customer WHERE cust_gender = '남' GROUP BY cust_salary ORDER BY COUNT(*) DESC LIMIT 1) "
			+ "UNION ALL "
			+ "(SELECT 'female', cust_salary FROM customer WHERE cust_gender = '여' GROUP BY cust_salary ORDER BY COUNT(*) DESC LIMIT 1)", nativeQuery = true)
	List<Object[]> getMostCommonSalary();
	
	//연령대별 고객 수 조회
	@Query(value = "SELECT " + "SUM(CASE WHEN age >= 20 AND age < 30 THEN 1 ELSE 0 END) AS '20대', "
			+ "SUM(CASE WHEN age >= 30 AND age < 40 THEN 1 ELSE 0 END) AS '30대', "
			+ "SUM(CASE WHEN age >= 40 AND age < 50 THEN 1 ELSE 0 END) AS '40대', "
			+ "SUM(CASE WHEN age >= 50 AND age < 60 THEN 1 ELSE 0 END) AS '50대', "
			+ "SUM(CASE WHEN age >= 60 AND age < 70 THEN 1 ELSE 0 END) AS '60대', "
			+ "SUM(CASE WHEN age >= 70 THEN 1 ELSE 0 END) AS '70대 이상' " + "FROM ( "
			+ "SELECT (YEAR(CURRENT_DATE) - YEAR(c.cust_birth)) - "
			+ "(CASE WHEN (MONTH(CURRENT_DATE) < MONTH(c.cust_birth)) OR "
			+ "(MONTH(CURRENT_DATE) = MONTH(c.cust_birth) AND DAY(CURRENT_DATE) < DAY(c.cust_birth)) THEN 1 ELSE 0 END) AS age "
			+ "FROM customer c " + ") c;", nativeQuery = true)
	Map<String, BigDecimal> findAgeGroups();
	
	//연령대별 고객 연령 총합
	@Query(value = "SELECT " + 
		    "SUM(CASE WHEN age >= 20 AND age < 30 THEN age ELSE 0 END) AS '20대', " +
		    "SUM(CASE WHEN age >= 30 AND age < 40 THEN age ELSE 0 END) AS '30대', " +
		    "SUM(CASE WHEN age >= 40 AND age < 50 THEN age ELSE 0 END) AS '40대', " +
		    "SUM(CASE WHEN age >= 50 AND age < 60 THEN age ELSE 0 END) AS '50대', " +
		    "SUM(CASE WHEN age >= 60 AND age < 70 THEN age ELSE 0 END) AS '60대', " +
		    "SUM(CASE WHEN age >= 70 THEN age ELSE 0 END) AS '70대 이상' " +
		    "FROM ( " +
		    "SELECT (YEAR(CURRENT_DATE) - YEAR(c.cust_birth)) - " +
		    "(CASE WHEN (MONTH(CURRENT_DATE) < MONTH(c.cust_birth)) OR " +
		    "(MONTH(CURRENT_DATE) = MONTH(c.cust_birth) AND DAY(CURRENT_DATE) < DAY(c.cust_birth)) THEN 1 ELSE 0 END) AS age " +
		    "FROM customer c " +
		    ") c;", nativeQuery = true)
		Map<String, BigDecimal> findAgeSums();
	
		// 연령대별 가장 많은 수의 고객을 가진 연봉 카테고리
		@Query(value = "SELECT age_range, salary, count " + "FROM (" + "  SELECT age_range, salary, count, "
				+ "ROW_NUMBER() OVER (PARTITION BY age_range ORDER BY count DESC) as rn " + " FROM ("
				+ "SELECT CASE " + " WHEN age BETWEEN 20 AND 29 THEN '20대' "
				+ "WHEN age >= 30 AND age < 40 THEN '30대' " + " WHEN age >= 40 AND age < 50 THEN '40대' "
				+ "WHEN age >= 50 AND age < 60 THEN '50대' " + " WHEN age >= 60 AND age < 70 THEN '60대' "
				+ "ELSE '70대 이상' END as age_range, " + "    cust_salary as salary, COUNT(*) as count "
				+ "FROM (SELECT cust_salary, YEAR(CURRENT_DATE) - YEAR(cust_birth) as age FROM customer) as subquery "
				+ "GROUP BY age_range, salary " + "  ) as subquery2 " + " UNION ALL "
				+ "SELECT 'all' as age_range, salary, count, " + " ROW_NUMBER() OVER (ORDER BY count DESC) as rn "
				+ "FROM (" + " SELECT cust_salary as salary, COUNT(*) as count " + " FROM customer "
				+ "GROUP BY salary " + "  ) as subquery3 " + ") as subquery4 " + "WHERE rn = 1", nativeQuery = true)
		List<Object[]> getTopSalaryByAgeRange();
	}
