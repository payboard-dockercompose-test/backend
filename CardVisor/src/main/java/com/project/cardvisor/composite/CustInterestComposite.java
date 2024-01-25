package com.project.cardvisor.composite;

import java.io.Serializable;

import com.project.cardvisor.vo.CustomerVO;
import com.project.cardvisor.vo.InterestVO;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class CustInterestComposite implements Serializable {
	
	@ManyToOne
	@JoinColumn(name="cust_id")
	private CustomerVO cust_id;
	
	@ManyToOne
	@JoinColumn(name="interest_id")
	private InterestVO interest_id;
	
	
}
