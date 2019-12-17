package com.kupanet.leaf.controler;

import com.kupanet.leaf.service.LeafSnowFlakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IDController {
    private Logger logger = LoggerFactory.getLogger(IDController.class);

    @Autowired
    private LeafSnowFlakeService leafSnowFlakeService;

    @GetMapping(value = "/api/leaf")
    public String getId() {

        Long id = leafSnowFlakeService.getId();
        return String.valueOf(id);
    }

}
