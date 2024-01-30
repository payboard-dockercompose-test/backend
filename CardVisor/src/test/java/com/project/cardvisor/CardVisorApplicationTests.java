package com.project.cardvisor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.cardvisor.repo.JobRepository;
import com.project.cardvisor.vo.JobListVO;

@SpringBootTest
class CardVisorApplicationTests {
	
	@Autowired
	JobRepository jrepo;
	

	@Test
	void f1() {
		
		JobListVO j1 = JobListVO.builder().job_id("1").job_type("공무원").build();
		jrepo.save(j1);
		
		
		
	}
	
	//@Test
	void contextLoads() {
		
	}

}
