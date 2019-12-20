package com.kupanet.cashiersystem.service;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value ="feign-id-generator")
@Component
public interface IDGeneratorService {
    @RequestMapping(value="/api/leaf", method= RequestMethod.GET)
    String getId();

}
