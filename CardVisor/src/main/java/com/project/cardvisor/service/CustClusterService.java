package com.project.cardvisor.service;

import org.springframework.stereotype.Service;

import com.project.cardvisor.repo.CustomerRepository;
import com.project.cardvisor.vo.CustomerVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustClusterService {

    private final CustomerRepository crepo;

    public CustClusterService(CustomerRepository customerRepository) {
        this.crepo = customerRepository;
    }

    public Map<String, Long> getGenderRatio() {
        List<CustomerVO> customers = (List<CustomerVO>) crepo.findAll();
        long maleCount = customers.stream().filter(customer -> customer.getCust_gender() == '남').count();
        long femaleCount = customers.stream().filter(customer -> customer.getCust_gender() == '여').count();

        Map<String, Long> genderRatio = new HashMap<>();
        genderRatio.put("남성", maleCount);
        genderRatio.put("여성", femaleCount);

        return genderRatio;
    }
}
