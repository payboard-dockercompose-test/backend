package com.project.cardvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CustClusterFilterDTO {
	

	private String gender;
	private String ageRange;
	private String jobType;
	private String salaryRange;
   

}
