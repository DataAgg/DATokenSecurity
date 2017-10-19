package com.dataagg;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dataagg.security.service.IUserService;

@RestController
public class DemoController {
	private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);
	@Autowired
	private IUserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;

	@RequestMapping("/")
	String hello() {
		LOG.debug("DemoController:/");
		return "hello world";
	}

	@RequestMapping("/login")
	public @ResponseBody String login(@RequestParam("username") String username, @RequestParam("userpswd") String userpswd, HttpServletRequest req) {
		LOG.debug("TokenLoginFilter: attemptAuthentication->" + username + "@" + userpswd);
		try {
			Authentication a = userService.login(username, userpswd, req.getRemoteAddr());
			a = authenticationManager.authenticate(a);
			return userService.createToken(a);
		} catch (AuthenticationException e) {
			LOG.error(e.getMessage(), e);
			return e.getMessage();
		}
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
