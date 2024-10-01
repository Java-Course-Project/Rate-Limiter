package org.example.ratelimiter.controller;

import org.example.ratelimiter.annotation.TokenSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HelloController {
	@GetMapping
	@TokenSize(token = 3)
	public String hello() {
		return "Hello World";
	}
}
