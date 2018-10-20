package br.com.academiadev.thunderpets.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldEndpoint {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
