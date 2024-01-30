package com.project.cardvisor.vo;

import java.sql.Date;

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

@Table(name="cust_interest")
public class CustInterestVO {
	
	@EmbeddedId
	private CustInterestComposite id;

}
