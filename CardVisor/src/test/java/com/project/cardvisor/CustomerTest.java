package com.project.cardvisor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.cardvisor.repo.CustomerRepository;
import com.project.cardvisor.repo.JobRepository;
import com.project.cardvisor.vo.CustomerVO;
import com.project.cardvisor.vo.JobListVO;

@SpringBootTest
public class CustomerTest {

	@Autowired
	CustomerRepository crepo;

	@Autowired
	JobRepository jrepo;

	List<String> nameList = new ArrayList<String>();
	List<String> phoneList = new ArrayList<String>();
	List<String> emailList = new ArrayList<String>();


	// 성별 정보 배열
	char[] gender = { '남', '여' };

	// 이메일 도메인 정보 배열
	String[] emailDomain = { "@naver.com", "@gmail.com", "@daum.net", "@hanmail.net" };

	UUID uuid;

	// 급여 정보 배열
	String[] salaryList = { "3000만원 미만", "3000만원 이상 5000만원 미만", "5000만원 이상 7000만원 미만", "7000만원 이상 1억 미만", "1억 이상" };

	// 랜덤 생년월일 생성 조정 함수
	static Date getRandomDate(Date start, Date end) {
		return new Date(getRandomTime(start.getTime(), end.getTime()));
	}

	static long getRandomTime(long startMillis, long endMillis) {
		return ThreadLocalRandom.current().nextLong(startMillis, endMillis);
	}

	//@Test
	void f1() throws IOException {

		// 1. 이름 리스트업
		BufferedReader reader = new BufferedReader(new FileReader("c:\\nameList.txt"));
		String name;
		while ((name = reader.readLine()) != null) {
			nameList.add(name);
		}

		// 2. 번호 리스트업
		BufferedReader reader2 = new BufferedReader(new FileReader("c:\\phoneList.txt"));
		String phone;
		while ((phone = reader2.readLine()) != null) {
			phoneList.add(phone);
		}

		// 3. 이메일 리스트업
		BufferedReader reader3 = new BufferedReader(new FileReader("c:\\emailList.txt"));
		String email;
		while ((email = reader3.readLine()) != null) {
			emailList.add(email);
		}

		// 2. 나이대 설정
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		// 20대 ~ 50대 설정 (1965년생 ~ 2004년생)
		Calendar calendar = Calendar.getInstance();
		calendar.set(1965, Calendar.JANUARY, 1);
		Date start = calendar.getTime();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(2004, Calendar.DECEMBER, 31);
		Date end = calendar2.getTime();

		JobListVO j;

		for (int i = 0; i < 800; i++) {

			uuid = UUID.randomUUID();
			char gen;
			if (i % 2 == 0) {
				gen = gender[0];
			} else {
				gen = gender[1];
			}

			Date randomDate = getRandomDate(start, end);
			java.sql.Date sqlDate = java.sql.Date.valueOf(dateFormat.format(randomDate));

			Random random = new Random();
			int randomNumber = random.nextInt(9) + 1;
			int randomEmailDomain = random.nextInt(4);
			int randomSalary = random.nextInt(5);

			j = jrepo.findById(randomNumber).orElse(null);

			CustomerVO c1 = CustomerVO.builder().cust_id("US-" + uuid).cust_name(nameList.get(i)).cust_gender(gen)
					.cust_birth(sqlDate).cust_email(emailList.get(i) + emailDomain[randomEmailDomain])
					.cust_phone("010" + phoneList.get(i)).cust_salary(salaryList[randomSalary]).job_id(j).build();

			crepo.save(c1);
		}

		//(1940 ~ 1964년생 설정)
		calendar.set(1940, Calendar.JANUARY, 1);
		start = calendar.getTime();

		calendar2.set(1964, Calendar.DECEMBER, 31);
		end = calendar2.getTime();
		
		for (int i = 801; i < 1000; i++) {
			uuid = UUID.randomUUID();
			char gen;
			if (i % 2 == 0) {
				gen = gender[0];
			} else {
				gen = gender[1];
			}

			Date randomDate = getRandomDate(start, end);
			java.sql.Date sqlDate = java.sql.Date.valueOf(dateFormat.format(randomDate));

			Random random = new Random();
			int randomNumber = random.nextInt(9) + 1;
			int randomEmailDomain = random.nextInt(4);
			int randomSalary = random.nextInt(5);

			j = jrepo.findById(randomNumber).orElse(null);

			CustomerVO c1 = CustomerVO.builder().cust_id("US-" + uuid).cust_name(nameList.get(i)).cust_gender(gen)
					.cust_birth(sqlDate).cust_email(emailList.get(i) + emailDomain[randomEmailDomain])
					.cust_phone("010" + phoneList.get(i)).cust_salary(salaryList[randomSalary]).job_id(j).build();

			crepo.save(c1);
		}

	}

}
