package com.kupanet.feign.component;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value ="feign-client")
@Component
public interface FeignService {
    //@RequestMapping(value="/api/leaf/{key}", method= RequestMethod.GET)
    //String getId(@PathVariable("key") Long key);
    @RequestMapping(value="/api/leaf", method= RequestMethod.GET)
    String getId();
}
