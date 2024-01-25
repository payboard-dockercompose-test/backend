package com.project.cardvisor.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

@Table(name="card_list")
public class CardListVO {
	
	@Id
	private String card_type;
	
	private String card_name;
	private String card_img_url;
	private int card_annual_fee;

}
