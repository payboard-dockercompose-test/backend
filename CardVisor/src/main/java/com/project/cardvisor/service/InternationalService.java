package com.project.cardvisor.service;

import java.lang.ProcessHandle.Info;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cardvisor.controller.InternationalController;
import com.project.cardvisor.repo.PaymentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InternationalService {
	

	@Autowired
	PaymentRepository payrep;
	
	//해외 토탈 결제 금액
	public Long getTotalpayment() {
		return payrep.selectTotalOverseasPayment();
	}
	
	//전년 월 대비 올해 월 증감
    public Map<String, Object> getComparePaymentSamePeriod(int month) {
        return payrep.selectDiffPaymentThisYearAndLastYear(month);
    }

	//올해 결제 건수가 제일 많은 나라 (순위 리스트업)
	public List<Map<String, Object>> getHighestOrderPayment() {
		return payrep.selectHighestOrderPayment();
	}

	// (차트 데이터) 월별 데이터 추출
	public List<Map<String, Object>> getNationPaymentsDataList(String startMonth, String endMonth) {
	    LocalDate start = YearMonth.parse(startMonth).atDay(1);
	    LocalDate end = YearMonth.parse(endMonth).atDay(1);
	    
	    return payrep.selectNationPaymentsDataList(start, end);
	}

}
