package com.project.cardvisor.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.project.cardvisor.vo.PaymentsVO;

public interface PaymentRepository extends CrudRepository<PaymentsVO, String>{

	@Query(value="SELECT sum(pay_amount) "
			+ "FROM payments "
			+ "WHERE currency_code = 'KRW' and pay_date >= DATE_FORMAT(NOW() - INTERVAL 0 MONTH + INTERVAL 1 DAY, '%Y-%m-01 00:00:00')  "
			+ "AND pay_date <= DATE_FORMAT(NOW() - INTERVAL 0 MONTH , '%Y-%m-%d %H:%i:%s')",nativeQuery = true)
	 Long TotalAmountPayments();
	
	
	@Query(value="SELECT sum(pay_amount) "
			+ "FROM payments "
			+ "WHERE currency_code = 'KRW' and pay_date >= DATE_FORMAT(NOW() - INTERVAL 1 MONTH + INTERVAL 1 DAY, '%Y-%m-01 00:00:00') "
			+ "AND pay_date <= DATE_FORMAT(NOW() - INTERVAL 1 MONTH , '%Y-%m-%d %H:%i:%s')",nativeQuery = true)
	 Long LastMonthTotalAmountPayments();
	
	@Query(value="SELECT sum(pay_amount) "
			+ "FROM payments "
			+ "WHERE currency_code != 'KRW' and pay_date >= DATE_FORMAT(NOW() - INTERVAL 0 MONTH + INTERVAL 1 DAY, '%Y-%m-01 00:00:00')  "
			+ "AND pay_date <= DATE_FORMAT(NOW() - INTERVAL 0 MONTH , '%Y-%m-%d %H:%i:%s')",nativeQuery = true)
	 Long AbroadTotalAmountPayments();
	
	
	@Query(value="SELECT sum(pay_amount) "
			+ "FROM payments "
			+ "WHERE currency_code != 'KRW' and pay_date >= DATE_FORMAT(NOW() - INTERVAL 1 MONTH + INTERVAL 1 DAY, '%Y-%m-01 00:00:00') "
			+ "AND pay_date <= DATE_FORMAT(NOW() - INTERVAL 1 MONTH , '%Y-%m-%d %H:%i:%s')",nativeQuery = true)
	 Long AbroadLastMonthTotalAmountPayments();
	
	
	/////// 은경 ///////
	//올해 해외 토탈 결제 금액
	@Query("SELECT SUM(p.pay_amount)"
			+ " FROM PaymentsVO p"
			+ " WHERE p.currency_code != 'KRW'"
			+ " AND YEAR(p.pay_date) = YEAR(CURRENT_DATE)")
	Long selectTotalOverseasPayment();
	
	//전년 월 대비 올해 월 증감 (+/-)
	@Query(value = "select thisYear.currentSum - lastYear.lastSum"
				+ " from"
				+ " ("
					+ " SELECT SUM(p.pay_amount) currentSum"
					+ " FROM payments p"
					+ " WHERE p.CURRENCY_CODE != 'KRW'"
					+ " AND YEAR(p.pay_date) = YEAR(CURRENT_DATE)"
					+ " AND MONTH(p.pay_date) = :month"
				+ " ) thisYear,"
				+ " ("
					+ " SELECT SUM(p.pay_amount) lastSum"
					+ " FROM payments p"
					+ " WHERE p.CURRENCY_CODE != 'KRW'"
					+ " AND YEAR(p.pay_date) = YEAR(CURRENT_DATE) - 1"
					+ " AND MONTH(p.pay_date) = :month"
				+ " ) lastYear", nativeQuery = true)
	Long selectDiffPaymentThisYearAndLastYear(@Param("month") int month);

	//올해 건수가 제일 많은 나라 (순위 리스트업)
	@Query(value ="SELECT COUNT(P.CURRENCY_CODE), P.CURRENCY_CODE, C.CURRENCY_NATION"
			+ " FROM PAYMENTS P"
			+ " JOIN CURRENCY C ON (P.CURRENCY_CODE = C.CURRENCY_CODE)"
			+ " WHERE P.CURRENCY_CODE != 'KRW'"
			+ " AND YEAR(P.PAY_DATE) = YEAR(CURRENT_DATE)"
			+ " GROUP BY P.CURRENCY_CODE, C.CURRENCY_NATION"
			+ " ORDER BY COUNT(P.CURRENCY_CODE) DESC", nativeQuery = true)
	List<Map<String, Object>> selectHighestOrderPayment();
	
	
	
	////지현
	//고객 성별에 따른 평균 소비금액
	@Query("SELECT c.cust_gender, AVG(p.pay_amount) " + "FROM PaymentsVO p JOIN p.reg_id r JOIN r.cust_id c "
			+ "GROUP BY c.cust_gender")
	List<Object[]> getAveragePaymentAmount();
	
	//고객 성별에 따른 상위 3개의 MCC
	@Query(value = "SELECT subquery2.cust_gender, m.ctg_name " + "FROM ("
			+ "    SELECT c.cust_gender, p.mcc_code, ROW_NUMBER() OVER(PARTITION BY c.cust_gender ORDER BY COUNT(*) DESC) as rn "
			+ "    FROM customer c " + "    JOIN card_reg_info r ON c.cust_id = r.cust_id "
			+ "    JOIN payments p ON r.reg_id = p.reg_id " + "    GROUP BY c.cust_gender, p.mcc_code "
			+ ") as subquery " + "JOIN mcc m ON m.mcc_code = subquery.mcc_code " + "JOIN ("
			+ "    SELECT c.cust_gender, ROW_NUMBER() OVER(PARTITION BY c.cust_gender ORDER BY COUNT(*) DESC) as rn "
			+ "    FROM customer c " + "    JOIN card_reg_info r ON c.cust_id = r.cust_id "
			+ "    JOIN payments p ON r.reg_id = p.reg_id " + "    GROUP BY c.cust_gender "
			+ ") as subquery2 ON subquery.cust_gender = subquery2.cust_gender AND subquery.rn <= 3 " + "UNION "
			+ "SELECT 'all' as cust_gender, m.ctg_name " + "FROM ("
			+ "    SELECT p.mcc_code, ROW_NUMBER() OVER(ORDER BY COUNT(*) DESC) as rn " + "    FROM card_reg_info r "
			+ "    JOIN payments p ON r.reg_id = p.reg_id " + "    GROUP BY p.mcc_code " + ") as subquery "
			+ "JOIN mcc m ON m.mcc_code = subquery.mcc_code " + "WHERE subquery.rn <= 3", nativeQuery = true)
	List<Object[]> getTop3MccCodeByGender();
	
	// 연령대별의 평균 소비금액
	@Query(value = "SELECT age_range, AVG(pay_amount) FROM ( " + "SELECT CASE  "
			+ "WHEN age >= 20 AND age < 30 THEN '20대' " + "WHEN age >= 30 AND age < 40 THEN '30대' "
			+ "WHEN age >= 40 AND age < 50 THEN '40대' " + "WHEN age >= 50 AND age < 60 THEN '50대' "
			+ "WHEN age >= 60 AND age < 70 THEN '60대' " + "ELSE '70대 이상' END as age_range, "
			+ "AVG(pay_amount) as pay_amount " + "FROM ( SELECT p.*, YEAR(CURRENT_DATE) - YEAR(c.cust_birth) as age "
			+ "FROM payments p " + "JOIN card_reg_info cri ON p.reg_id = cri.reg_id "
			+ "JOIN customer c ON cri.cust_id = c.cust_id ) as subquery " + "GROUP BY age_range " + "UNION "
			+ "SELECT 'all' as age_range, AVG(pay_amount) as pay_amount " + "FROM payments p ) as all_data "
			+ "GROUP BY age_range "
			+ "ORDER BY CASE WHEN age_range = 'all' THEN 0 ELSE 1 END, age_range", nativeQuery = true)
	List<Map<String, Object>> findAveragePaymentByAgeRange();
	
	//연령대별에 따른 상위 3개의 MCC
	@Query(value = "" + "(SELECT subquery.age_range, m.ctg_name " + "FROM ("
			+ "SELECT subquery1.age_range, subquery1.mcc_code, ROW_NUMBER() OVER(PARTITION BY subquery1.age_range ORDER BY subquery1.count DESC) as rn "
			+ "FROM (" + "SELECT CASE "
			+ "WHEN YEAR(CURRENT_DATE) - YEAR(c.cust_birth) >= 20 AND YEAR(CURRENT_DATE) - YEAR(c.cust_birth) < 30 THEN '20대' "
			+ "WHEN YEAR(CURRENT_DATE) - YEAR(c.cust_birth) >= 30 AND YEAR(CURRENT_DATE) - YEAR(c.cust_birth) < 40 THEN '30대' "
			+ "WHEN YEAR(CURRENT_DATE) - YEAR(c.cust_birth) >= 40 AND YEAR(CURRENT_DATE) - YEAR(c.cust_birth) < 50 THEN '40대' "
			+ "WHEN YEAR(CURRENT_DATE) - YEAR(c.cust_birth) >= 50 AND YEAR(CURRENT_DATE) - YEAR(c.cust_birth) < 60 THEN '50대' "
			+ "WHEN YEAR(CURRENT_DATE) - YEAR(c.cust_birth) >= 60 AND YEAR(CURRENT_DATE) - YEAR(c.cust_birth) < 70 THEN '60대' "
			+ "ELSE '70대 이상' END as age_range, p.mcc_code, COUNT(*) as count " + "FROM customer c "
			+ "JOIN card_reg_info r ON c.cust_id = r.cust_id "
			+ "JOIN payments p ON r.reg_id = p.reg_id " + "GROUP BY age_range, p.mcc_code "
			+ ") as subquery1 " + ") as subquery " + "JOIN mcc m ON m.mcc_code = subquery.mcc_code "
			+ "WHERE subquery.rn <= 3 " + "UNION " + "SELECT 'all' as age_range, m.ctg_name " + "FROM ("
			+ "SELECT p.mcc_code, ROW_NUMBER() OVER(ORDER BY COUNT(*) DESC) as rn " + "FROM card_reg_info r "
			+ "JOIN payments p ON r.reg_id = p.reg_id " + "GROUP BY p.mcc_code " + ") as subquery_all "
			+ "JOIN mcc m ON m.mcc_code = subquery_all.mcc_code " + "WHERE subquery_all.rn <= 3)", nativeQuery = true)
	List<Object[]> findTopMccCodes();
	
}
