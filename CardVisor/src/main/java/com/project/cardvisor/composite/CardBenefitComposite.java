package com.project.cardvisor.composite;

import java.io.Serializable;

import com.project.cardvisor.vo.BenefitVO;
import com.project.cardvisor.vo.CardListVO;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class CardBenefitComposite implements Serializable {

	@ManyToOne
	@JoinColumn(name="card_type")
	private CardListVO card_type;
	
	@ManyToOne
	@JoinColumn(name="benefit_id")
	private BenefitVO benefit_id;
	
	
}
