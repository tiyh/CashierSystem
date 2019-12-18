package com.kupanet.feign.controller;

import com.kupanet.feign.component.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IDController {
    @Autowired
    private FeignService feignService;

    @GetMapping("/id")
    public String getId(){
        return feignService.getId();
    }
}
