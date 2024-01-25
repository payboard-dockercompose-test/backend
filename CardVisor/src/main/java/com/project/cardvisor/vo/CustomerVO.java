package com.project.cardvisor.vo;

import java.sql.Date;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
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

@Table(name="Customer")
public class CustomerVO {
	
	
	@Id
	private String cust_id;
	
	private String cust_name;
	private char cust_gender;
	private Date cust_birth;
	private String cust_phone;
	private String cust_email;
	private int cust_salary;
	
	@ManyToOne
	@JoinColumn(name="job_id")
	private JobListVO job_id;
	
	

}
