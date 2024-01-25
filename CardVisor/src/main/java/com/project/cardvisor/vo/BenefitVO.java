package com.project.cardvisor.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Table(name="benefit")
public class BenefitVO {
	
	@Id
	private String benefit_id;
	
	private String benefit_detail;
	
	@ManyToOne
	@JoinColumn(name="interest_id")
	private InterestVO interest_id;
	

}
