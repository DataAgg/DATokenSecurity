package com.dataagg.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;

import com.dataagg.security.SecurityUtils;
import com.dataagg.security.service.IUserService;

public class TokenAuthenticationFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

	private IUserService userService;

	public TokenAuthenticationFilter(IUserService userService) {
		super();
		this.userService = userService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		LOG.debug("TokenAuthenticationFilter start:" + request.toString());
		String token = SecurityUtils.parseToken((HttpServletRequest) request);
		LOG.debug("TokenAuthenticationFilter token:" + token);
		Authentication authentication = userService.checkToken(token);
		if (authentication != null) {
			LOG.debug("TokenAuthenticationFilter saveAuthentication!");
			SecurityUtils.saveAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
		LOG.debug("TokenAuthenticationFilter end:" + request.toString());
	}
}
