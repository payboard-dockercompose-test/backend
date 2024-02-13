
package com.project.cardvisor.service;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.cardvisor.repo.CustomerRepository;
import com.project.cardvisor.repo.PaymentRepository;
import com.project.cardvisor.vo.CustomerVO;
import com.project.cardvisor.vo.PaymentsVO;
@Service
public class CustClusterService {
    @Autowired
    CustomerRepository crepo;
    
    @Autowired
    PaymentRepository prepo;
    //성별 조회
    public Map<String, Long> getGenderRatio() {
        List<CustomerVO> customers = (List<CustomerVO>) crepo.findAll();
        long maleCount = customers.stream().filter(customer -> customer.getCustGender() == '남').count();
        long femaleCount = customers.stream().filter(customer -> customer.getCustGender() == '여').count();
        Map<String, Long> genderRatio = new HashMap<>();
        genderRatio.put("남성", maleCount);
        
        genderRatio.put("여성", femaleCount);
        return genderRatio;
    }
    
    //나이별 조회
    public Map<String, Integer> getAgeGroupCount() {
        Iterable<CustomerVO> customers = crepo.findAll();
        Map<String, Integer> ageGroupCount = new HashMap<>();
        for (CustomerVO customer : customers) {
          String ageGroup = calculateAgeGroup(customer.getCustBirth());
          if (ageGroupCount.containsKey(ageGroup)) {
            ageGroupCount.put(ageGroup, ageGroupCount.get(ageGroup) + 1);
          } else {
            ageGroupCount.put(ageGroup, 1);
          }
        }
        return ageGroupCount;
    }
      private String calculateAgeGroup(Date birthDate) {
        //java.sql.Date 인스턴스를 java.util.Date 인스턴스로 변환한 후 toInstant() 메소드를 호출
        LocalDate localBirthDate = new java.util.Date(birthDate.getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Period.between(localBirthDate, LocalDate.now()).getYears();
        // 연령대 계산 로직
        if (age >= 70) {
          return "70대 이상";
        } else if (age >= 60) {
          return "60대";
        } else if (age >= 50) {
          return "50대";
        } else if (age >= 40) {
          return "40대";
        } else if (age >= 30) {
          return "30대";
        } else {
          return "20대";
        }
      }
      //직업별 조회
      public List<Map<String, Object>> findAllJobTypes() {
          return crepo.findAllJobTypes();
      }
      
      //연봉별 조회
      public List<Object[]> findAllCustSalary() {
          return crepo.findAllCustSalary();
      }
      
      //payment에서 custSalary,payAmount만 출력
      public List<Object[]> getAveragePayAmountBySalary() {
            List<PaymentsVO> payments = (List<PaymentsVO>) prepo.findAll();
            Map<String, List<Long>> salaryToPayAmounts = new HashMap<>();
            for (PaymentsVO payment : payments) {
                String custSalary = payment.getRegId().getCustId().getCustSalary();
                long payAmount = payment.getPayAmount();
                if (!salaryToPayAmounts.containsKey(custSalary)) {
                    salaryToPayAmounts.put(custSalary, new ArrayList<>());
                }
                salaryToPayAmounts.get(custSalary).add(payAmount);
            }
            List<Object[]> salaryToAveragePayAmount = new ArrayList<>();
            for (Map.Entry<String, List<Long>> entry : salaryToPayAmounts.entrySet()) {
                String salary = entry.getKey();
                List<Long> payAmounts = entry.getValue();
                double totalPayAmount = 0;
                for (Long payAmount : payAmounts) {
                    totalPayAmount += payAmount;
                }
                double averagePayAmount = totalPayAmount / payAmounts.size();
                //평균 금액를 천 단위로 구분하는 문자열로 변환
                String averagePayAmountInKRW = String.format("%,.0f", averagePayAmount);
                salaryToAveragePayAmount.add(new Object[]{salary, averagePayAmountInKRW});
            }
            return salaryToAveragePayAmount;
        }
    public int CustomerTotalCount() {
        List<CustomerVO> customers = (List<CustomerVO>) crepo.findAll();
        int CustomerCount = customers.size();
        return CustomerCount;
    }
}