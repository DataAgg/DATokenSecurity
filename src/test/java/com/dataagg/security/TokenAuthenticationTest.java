package com.dataagg.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

public class TokenAuthenticationTest {
	private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationTest.class);

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testCreate() {
		//admin
		HttpRequest request = HttpRequest.get("http://127.0.0.1:8080/login").query("username", "admin").query("userpswd", "admin");
		HttpResponse response = request.send();
		LOG.info(response.bodyText());
		assertEquals(200, response.statusCode());
		String token = response.bodyText();
		response = HttpRequest.get("http://127.0.0.1:8080/users").send();
		assertEquals(403, response.statusCode());
		request = HttpRequest.get("http://127.0.0.1:8080/users").header("Authorization", "Bearer 12" + token);
		response = request.send();
		assertEquals(403, response.statusCode());
		request = HttpRequest.get("http://127.0.0.1:8080/users").header("Authorization", "Bearer " + token);
		response = request.send();
		LOG.info(response.bodyText());
		assertEquals(200, response.statusCode());
		request = HttpRequest.get("http://127.0.0.1:8080/users2").header("Authorization", "Bearer " + token);
		response = request.send();
		LOG.info(response.bodyText());
		assertEquals(200, response.statusCode());

		//guest
		request = HttpRequest.get("http://127.0.0.1:8080/login").query("username", "guest").query("userpswd", "guest");
		response = request.send();
		LOG.info(response.bodyText());
		assertEquals(200, response.statusCode());
		token = response.bodyText();
		response = HttpRequest.get("http://127.0.0.1:8080/users").send();
		assertEquals(403, response.statusCode());
		request = HttpRequest.get("http://127.0.0.1:8080/users").header("Authorization", "Bearer 12" + token);
		response = request.send();
		assertEquals(403, response.statusCode());
		request = HttpRequest.get("http://127.0.0.1:8080/users").header("Authorization", "Bearer " + token);
		response = request.send();
		LOG.info(response.bodyText());
		assertEquals(200, response.statusCode());
		request = HttpRequest.get("http://127.0.0.1:8080/users2").header("Authorization", "Bearer " + token);
		response = request.send();
		assertEquals(403, response.statusCode());

	}
}
