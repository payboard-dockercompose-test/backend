package com.project.cardvisor.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.project.cardvisor.vo.CardListVO;

import lombok.Value;

public interface CardListRepository extends CrudRepository<CardListVO, Integer>{
	
	
	
	@Query(value="select * from card_list cl,\r\n"
			+ "(\r\n"
			+ "select  card_type,\r\n"
			+ "count(*) as col\r\n"
			+ "from card_reg_info \r\n"
			+ "group by card_type\r\n"
			+ "order by col desc \r\n"
			+ "limit 5\r\n"
			+ ") ci \r\n"
			+ "where cl.card_type = ci.card_type\r\n"
			+ "order by ci.col desc", nativeQuery = true)
	List<Map<String, Object>> selectTop5CardList();
}
