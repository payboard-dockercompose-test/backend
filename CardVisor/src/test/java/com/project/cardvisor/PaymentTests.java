package com.project.cardvisor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.cardvisor.repo.BenefitRepository;
import com.project.cardvisor.repo.CardBenefitRepository;
import com.project.cardvisor.repo.CardListRepository;
import com.project.cardvisor.repo.CardRegRepository;
import com.project.cardvisor.repo.CustomerRepository;
import com.project.cardvisor.repo.MccCodeRepository;
import com.project.cardvisor.repo.PaymentRepository;
import com.project.cardvisor.service.PaymentsService;
import com.project.cardvisor.vo.BenefitVO;
import com.project.cardvisor.vo.CardRegInfoVO;
import com.project.cardvisor.vo.MccVO;
import com.project.cardvisor.vo.PaymentsVO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class PaymentTests {
	
	@Autowired
	PaymentRepository prep;
	
	@Autowired
	CardRegRepository crep;
	
	@Autowired
	MccCodeRepository mrep;
	
	@Autowired
	CustomerRepository custrep;
	
	@Autowired
	CardBenefitRepository cbrep;
	
	@Autowired
	CardListRepository clrep;
	
	@Autowired
	BenefitRepository brep;
	
	@Autowired
	PaymentsService paymentservice;
	
	
	@Test
	public void f3() {
		  
		
	 	Long amount= paymentservice.AbroadTotalAmountPayments();
    	Long amount1 = paymentservice.AbroadLastMonthTotalAmountPayments(); 
    	Long totalamount= amount1- amount;
    	System.out.println("amount1:"+amount1);
    	System.out.println("amount:"+amount);
    	System.out.println("total:"+totalamount);
    
	}
	
	//@Test
	public void f1() {
		
		//고객 리스트
		LinkedList<CardRegInfoVO> clist = new LinkedList<>();
		crep.findAll().forEach(c -> {
			clist.add(c);
		});
		//mcc 코드 타입
		String[] mccCode = {
				"0001", "0002", "0003", "0004", "0005","0006", "0007", "0008", "0009", "0010", "0011",
				"0012", "0013", "0014", "0015","0016"
		};
		String[] 업종 = { "음식점", "편의점", "교통비", "쇼핑몰", "미용실", "병원", "숙박시설", "오락시설", "교육비", "카페", "주거/통신",
				"편의점", "레저/테마", "술/유흥", "국세납입","기타" };

		Random random = new Random();
		
		for(int i=0; i<40000; i++) { //34000
			UUID uuid = UUID.randomUUID();
			int mccidx = random.nextInt(16); // mcc 16개
			int regidx = random.nextInt(clist.size()); //999개
			int amountidx = random.nextInt(100); //곱할 숫자
			MccVO mvo = mrep.findById(mccCode[mccidx]).orElse(null);
			
			Date regDate = clist.get(regidx).getReg_date(); // 등록일
		    Date expDate = clist.get(regidx).getExpire_date(); // 만기일
		    java.util.Date randomDate = getRandomDate(regDate, expDate);
		    if (randomDate != null) { // randomDate가 null이 아닌 경우에만 save
		        Timestamp timestamp = new Timestamp(randomDate.getTime());
				
				PaymentsVO vo = PaymentsVO.builder()
						.pay_id("PA-"+uuid)
						.reg_id(clist.get(regidx))
						.nation("KOR")
						.currency_code("KRW")
						.currency_rate(1)
						.pay_amount(amountidx*1000)
						.pay_date(timestamp)
						.pay_store(업종[mccidx])
						.mcc_code(mvo)
						.build();
				prep.save(vo);
		    }
		}
	}
	
	//@Test
	public void f2() {
		prep.findAll().forEach(p -> {
			
			if(p.getBenefit_amount() > 0) {
				return;
			}
			MccVO curMcc = p.getMcc_code();
			
			List<BenefitVO> bvo = brep.findByPay_id(p.getPay_id());
			
			bvo.forEach(b -> {
				if(b.getMcc_code().equals(curMcc.getMcc_code())) {
					p.setBenefit_amount((int)Math.floor(p.getPay_amount()*b.getBenefit_pct()));
					prep.save(p);
				}
			});
		});
	}
	
	public static java.util.Date getRandomDate(java.util.Date start, java.util.Date end) {
	    java.util.Date now = new java.util.Date(); // 현재 시간

	    // end가 현재 시간보다 이후이면, end를 현재 시간으로 설정
	    if (end.after(now)) {
	        end = now;
	        if(end.after(start)) {
	        	
	        } else {
	        	return null;
	        }
	    }

	    // start와 end 사이에서 랜덤 날짜 생성
	    long startMillis = start.getTime();
	    long endMillis = end.getTime();
	    long randomMillisSinceEpoch = ThreadLocalRandom
	        .current()
	        .nextLong(startMillis, endMillis);

	    java.util.Date randomDate = new java.util.Date(randomMillisSinceEpoch);

	    // 시간, 분, 초 랜덤 설정
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(randomDate);
	    cal.set(Calendar.HOUR_OF_DAY, ThreadLocalRandom.current().nextInt(24)); // 시간
	    cal.set(Calendar.MINUTE, ThreadLocalRandom.current().nextInt(60)); // 분
	    cal.set(Calendar.SECOND, ThreadLocalRandom.current().nextInt(60)); // 초

	    // 생성된 랜덤 날짜가 현재로부터 4년 이내인지 확인
	    cal = Calendar.getInstance();
	    cal.setTime(now);
	    cal.add(Calendar.YEAR, -4); // 4년 전
	    java.util.Date fourYearsAgo = cal.getTime(); // 현재로부터 4년 전
	    if (randomDate.before(fourYearsAgo)) {
	        return null; // 생성된 랜덤 날짜가 현재로부터 4년 이전이면, null 반환
	    }

	    // 생성된 랜덤 날짜 반환
	    return randomDate;
	}
	
	
	
	


	
	//카드 분류 타입으로
//	모든 사람 랜덤 카드 : 35000건
//	 * 딥드림 카드 : 10000건 26, 27, 28
//	 * 
//	 * 여행 3000건 interestid => 6
//	 * 2000건

	
}
