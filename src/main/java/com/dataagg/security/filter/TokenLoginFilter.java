package com.dataagg.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.dataagg.security.SecurityUtils;
import com.dataagg.security.service.IUserService;

public class TokenLoginFilter extends AbstractAuthenticationProcessingFilter {
	private static final Logger LOG = LoggerFactory.getLogger(TokenLoginFilter.class);

	private IUserService userService;

	public TokenLoginFilter(String url, IUserService userService, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		this.userService = userService;
		setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException, ServletException {
		//用户登陆
		String username = req.getParameter("username");
		String userpswd = req.getParameter("userpswd");
		LOG.debug("TokenLoginFilter: attemptAuthentication->" + username + "@" + userpswd);
		Authentication a = userService.login(username, userpswd);
		return getAuthenticationManager().authenticate(a);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authentication) throws IOException, ServletException {
		if (authentication != null) {
			//认证成功后把token写入返回的header
			String token = userService.createToken(authentication);
			SecurityUtils.addAuthentication(res, token);
			LOG.debug("TokenLoginFilter: successfulAuthentication->" + token);
			//测试用的
			res.getWriter().append(token);
		}
	}
}
