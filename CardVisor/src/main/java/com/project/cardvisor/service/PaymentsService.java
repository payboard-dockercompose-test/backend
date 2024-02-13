package com.project.cardvisor.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.PaymentRepository;
import com.project.cardvisor.vo.PaymentsVO;

@Service
public class PaymentsService {

	@Autowired
	PaymentRepository payrepo;
	
	public Long TotalAmountPayments() {
		
		Long amount = payrepo.TotalAmountPayments();
		return amount;
	}
public Long LastMonthTotalAmountPayments() {
		
	Long amount1 = payrepo.TotalAmountPayments();
	Long amount = payrepo.LastMonthTotalAmountPayments(); 
	Long totalamount= amount1- amount;
	System.out.println("total:"+totalamount);
		return totalamount;
	}
public Long AbroadTotalAmountPayments() {
	Long amount =payrepo.AbroadTotalAmountPayments();
	return amount;
}

public Long AbroadLastMonthTotalAmountPayments() {
	Long amount = payrepo.AbroadLastMonthTotalAmountPayments();
	if(amount==null) {
		amount=(long) 0;
	}
	Long amount1 = payrepo.AbroadTotalAmountPayments();
	if(amount1==null) {
		amount1=(long) 0;
	}
	Long totalamount= amount1- amount;
	return totalamount;
}
//용수
public List<Map<String, Object>> selectPerMonthamount() {
    List<Map<String, Object>> pList = payrepo.selectPerMonthamount();
    return pList;
}
public int perMonthTotalAmount() {
	Long totalamount = payrepo.perMonthTotalAmount();
	 String formattedAmount = String.valueOf(totalamount / 1000000);
	       int parsedValue = Integer.parseInt(formattedAmount);
	return parsedValue;
}
public Double TotalIncrese() {
	Long totalamount = payrepo.perMonthTotalAmount();
	Long Lastmonthtotalamount = payrepo.lastYearPerMonthTotalAmount();
	Double TotalIncrese = (((double)(totalamount -Lastmonthtotalamount)/Lastmonthtotalamount)*100);
	 DecimalFormat df = new DecimalFormat("0.0");

     // Format the double value using DecimalFormat
     String formattedValue = df.format(TotalIncrese);
     double parsedValue = Double.parseDouble(formattedValue);
	return parsedValue;
}
public List<Map<String, Object>> SelectLastYearPerMonthamount() {
	List<Map<String, Object>> pList = payrepo.selectLastYearPerMonthamount();
	return pList;
}

//주별..
public List<Map<String, Object>> selectPerWeeklyamount() {
    List<Map<String, Object>> pList = payrepo.selectPerWeeklyamount();
    return pList;
}
public List<Map<String, Object>> SelectLastYearPerWeeklyamount() {
    List<Map<String, Object>> pList = payrepo.selectLastYearPerWeeklyamount();
    return pList;
}
public int perWeekTotalAmount() {
	Long totalamount = payrepo.perWeekTotalAmount();
	 String formattedAmount = String.valueOf(totalamount / 1000000);
	       int parsedValue = Integer.parseInt(formattedAmount);
	return parsedValue;
}
public Double WeekTotalIncrese() {
	Long weektotalamount = payrepo.perWeekTotalAmount();
	Long Lastweektotalamount = payrepo.lastYearPerWeekTotalAmount();
	Double TotalIncrese = (((double)(weektotalamount -Lastweektotalamount)/Lastweektotalamount)*100);
	 DecimalFormat df = new DecimalFormat("0.0");

     // Format the double value using DecimalFormat
     String formattedValue = df.format(TotalIncrese);
     double parsedValue = Double.parseDouble(formattedValue);
	return parsedValue;
}

//월별 거래건수
public List<Map<String, Object>> selectPerMonthtransaction() {
    List<Map<String, Object>> pList = payrepo.selectPerMonthtransaction();
    return pList;
}
	
public int selectMonthtransaction() {
	int amount = payrepo.selectMonthtransaction();
	return amount;
}
//주간 거래건수


public int selectWeektransaction() {
	int amount =payrepo.selectWeektransaction();
	return amount;
}
public List<Map<String, Object>> selectPerWeeklytransaction() {
	  List<Map<String, Object>> pList = payrepo.selectPerWeeklytransaction();
	    return pList;
}
}
