package com.tkmi.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tkmi.serialization.RequestProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired DemoService demoService;

    @RequestMapping("/agg")
    public ResponseEntity<ObjectNode> agg(@RequestBody RequestProperties properties) {
        return demoService.agg(properties);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "Hello Kanstantsin";
    }
}
