package com.finance.tracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Healthcheck {

    @RequestMapping(method = {RequestMethod. GET} , value = "/healthcheck")
    public String healthcheck(){
        return "ok";
    }
}
