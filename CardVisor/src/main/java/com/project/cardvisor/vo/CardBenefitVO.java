package com.project.cardvisor.vo;

import com.project.cardvisor.composite.CardBenefitComposite;
import com.project.cardvisor.composite.CustInterestComposite;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode

@Entity

@Table(name="card_benefit")
public class CardBenefitVO {
	
	@EmbeddedId
	private CardBenefitComposite id;
	
}
