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
	@Query("SELECT SUM(p.payAmount * p.currencyRate)"
			+ " FROM PaymentsVO p"
			+ " WHERE p.currencyCode != 'KRW'"
			+ " AND YEAR(p.payDate) = YEAR(CURRENT_DATE)")
	Long selectTotalOverseasPayment();
	
	//전년 월 대비 올해 월 증감 (+/-)
	@Query(value = "WITH thisYear AS ("
			+ " SELECT SUM(p.pay_amount * p.currency_rate) AS currentSum"
				+ " FROM payments p"
				+ " WHERE p.currency_code != 'KRW'"
				+ " AND YEAR(p.pay_date) = YEAR(CURRENT_DATE)"
				+ " AND MONTH(p.pay_date) = :month"
			+ " ),"
			+ " lastYear AS ("
				+ " SELECT SUM(p.pay_amount * p.currency_rate) AS lastSum"
				+ " FROM payments p "
				+ " WHERE p.currency_code != 'KRW' "
				+ " AND YEAR(p.pay_date) = YEAR(CURRENT_DATE) - 1"
				+ " AND MONTH(p.pay_date) = :month"
			+ " )"
			+ " SELECT thisYear.currentSum, lastYear.lastSum"
			+ " FROM thisYear, lastYear", nativeQuery = true)
	Map<String, Object> selectDiffPaymentThisYearAndLastYear(@Param("month") int month);


	//올해 결제 건수가 제일 많은 나라 (순위 리스트업)
	@Query(value ="SELECT COUNT(p.CURRENCY_CODE) AS PAYMENT_CNT"
				+ " , SUM(p.PAY_AMOUNT * p.CURRENCY_RATE) AS TOTAL_PAYMENT"
				+ " , p.CURRENCY_CODE"
				+ " , c.CURRENCY_NATION"
				+ " , DATE_FORMAT(NOW(), '%Y') AS CURRENT_YEAR"
			+ " FROM payments p"
			+ " LEFT OUTER JOIN ("
				+ " SELECT DISTINCT CURRENCY_CODE, CURRENCY_NATION"
				+ " FROM currency"
			+ " ) c ON (p.CURRENCY_CODE = c.CURRENCY_CODE)"
			+ " WHERE p.CURRENCY_CODE != 'KRW'"
			+ " AND DATE_FORMAT(p.PAY_DATE, '%Y') = DATE_FORMAT(NOW(), '%Y')"
			+ " GROUP BY p.CURRENCY_CODE, c.CURRENCY_NATION"
			+ " ORDER BY PAYMENT_CNT DESC, TOTAL_PAYMENT DESC", nativeQuery = true)
	List<Map<String, Object>> selectHighestOrderPayment();
	

	
	
	////지현
	//고객 성별에 따른 평균 소비금액
	@Query("SELECT c.custGender, AVG(p.payAmount) " + "FROM PaymentsVO p JOIN p.regId r JOIN r.custId c "
			+ "GROUP BY c.custGender")
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

	//WorldMap
	// select 건수, 총금액, 연령대
	// 22,23,24년도 월별
	
	
	//용수
	//6개월 전부터 매달 총결제금액
	@Query(value="SELECT "
			+ "  DATE_FORMAT(pay_date, '%Y-%m') AS month, "
			+ "  SUM(pay_amount) AS total_amount "
			+ "FROM payments "
			+ "WHERE pay_date > DATE_SUB(CURRENT_DATE, INTERVAL 5 MONTH) "
			+ "GROUP BY month "
			+ "ORDER BY month asc ",nativeQuery = true)
	List<Map<String, Object>> selectPerMonthamount();
	
	@Query(value= "SELECT  "
			+ "  DATE_FORMAT(pay_date, '%Y-%m') AS month, "
			+ "  SUM(pay_amount) AS total_amount "
			+ "FROM payments\r\n"
			+ "WHERE pay_date > DATE_SUB(CURRENT_DATE,INTERVAL 17 MONTH) AND "
			+ "      pay_date < DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR) "
			+ "GROUP BY month "
			+ "ORDER BY month",nativeQuery = true)
	List<Map<String, Object>> selectLastYearPerMonthamount();
	
	@Query(value="SELECT \r\n"
			+ "  SUM(pay_amount) AS total_amount\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE , INTERVAL 5 MONTH)\r\n",nativeQuery = true)
	Long perMonthTotalAmount();
	
	@Query(value= "SELECT \r\n"
			+ "  SUM(pay_amount) AS total_amount\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE,INTERVAL 17 MONTH) AND\r\n"
			+ "      pay_date < DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR)",nativeQuery = true)
	Long lastYearPerMonthTotalAmount();
	
	
	@Query(value="SELECT\r\n"
			+ "  WEEK(pay_date) AS week,\r\n"
			+ "  SUM(pay_amount) AS total_amount\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE, INTERVAL 6 WEEK)\r\n"
			+ "GROUP BY week\r\n"
			+ "ORDER BY week", nativeQuery = true)
	List<Map<String, Object>> selectPerWeeklyamount();
	
	@Query(value="SELECT\r\n"
			+ "  WEEK(pay_date) AS week,\r\n"
			+ "  SUM(pay_amount) AS total_amount\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE, INTERVAL 52 WEEK)\r\n"
			+ "and	  pay_date < DATE_SUB(CURRENT_DATE, INTERVAL 47 WEEK)\r\n"
			+ "GROUP BY week\r\n"
			+ "ORDER BY week", nativeQuery = true)
	List<Map<String, Object>> selectLastYearPerWeeklyamount();
	
	
	@Query(value="SELECT\r\n"
			+ "  SUM(pay_amount) AS total_amount\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE, INTERVAL 6 WEEK)",nativeQuery = true)
	Long perWeekTotalAmount();
	
	@Query(value= "SELECT\r\n"
			+ "  SUM(pay_amount) AS total_amount\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE, INTERVAL 52 WEEK)\r\n"
			+ "and	  pay_date < DATE_SUB(CURRENT_DATE, INTERVAL 47 WEEK)",nativeQuery = true)
	Long lastYearPerWeekTotalAmount();
	
	//월거래건수
	@Query(value="SELECT \r\n"
			+ "  DATE_FORMAT(pay_date, '%Y-%m') AS month,\r\n"
			+ "  count(*) As transaction\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE , INTERVAL 5 MONTH)\r\n"
			+ "GROUP BY month\r\n"
			+ "ORDER BY month", nativeQuery = true)
	List<Map<String, Object>> selectPerMonthtransaction();
	
	@Query(value="SELECT \r\n"
			+ "  count(*) As transaction\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE , INTERVAL 5 MONTH)", nativeQuery = true)
	int selectMonthtransaction();
	//필요한가?..
	@Query(value="SELECT \r\n"
			+ "  DATE_FORMAT(pay_date, '%Y-%m') AS month,\r\n"
			+ "  count(*) As transaction\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE,INTERVAL 17 MONTH) AND\r\n"
			+ "      pay_date < DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR)\r\n"
			+ "GROUP BY month\r\n"
			+ "ORDER BY month", nativeQuery = true)
	List<Map<String, Object>> selectLastYearMonthtransaction();
	//주간
	@Query(value="SELECT\r\n"
			+ "  WEEK(pay_date) AS week,\r\n"
			+ "    count(*) As transaction\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE, INTERVAL 6 WEEK)\r\n"
			+ "GROUP BY week\r\n"
			+ "ORDER BY week",nativeQuery = true)
	List<Map<String, Object>> selectPerWeeklytransaction();
	
	@Query(value="SELECT \r\n"
			+ "  count(*) As transaction\r\n"
			+ "FROM payments\r\n"
			+ "WHERE pay_date >= DATE_SUB(CURRENT_DATE, INTERVAL 6 WEEK)", nativeQuery = true)
	int selectWeektransaction();

	
}
