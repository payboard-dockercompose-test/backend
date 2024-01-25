package com.project.cardvisor.vo;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

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

@Table(name="payments")
public class PaymentsVO {
	
	@Id
	private String pay_id;
	
	@ManyToOne
	@JoinColumn(name="reg_id")
	private CardRegInfoVO reg_id;
	
	private String nation;
	private String currency_code;
	private double currency_rate;
	
	private String pay_store;
	private long pay_amount;
	
	@CreationTimestamp
	private Timestamp pay_date;
	
	@ManyToOne
	@JoinColumn(name="mcc_code")
	private MccVO mcc_code;


}
