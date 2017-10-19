package com.dataagg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
	private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

	@RequestMapping("/")
	String hello() {
		LOG.debug("DemoController:/");
		return "hello world";
	}

	@RequestMapping("/users")
	public @ResponseBody String getUsers() {
		LOG.debug("DemoController:/users");
		return "{\"users\":[{\"firstname\":\"super\", \"lastname\":\"watano\"}," + "{\"firstname\":\"super\",\"lastname\":\"guest\"}]}";
	}

	@RequestMapping("/users2")
	public @ResponseBody String getUsers2() {
		LOG.debug("DemoController:/users2");
		return "{\"users\":[{\"firstname\":\"super\", \"lastname\":\"watano\"}," + "{\"firstname\":\"super\",\"lastname\":\"admin\"}]}";
	}
}
