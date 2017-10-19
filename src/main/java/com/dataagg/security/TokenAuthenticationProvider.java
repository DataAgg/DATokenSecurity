package com.dataagg.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenAuthenticationProvider implements AuthenticationProvider {
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UserDetails ud = (UserDetails) authentication.getPrincipal();
		String username = ud.getUsername();
		if (username == null || username.trim().length() < 1) {
			//username错误!
			throw new BadCredentialsException(username);
		}
		if (!ud.isEnabled()) {
			//帐号已停用
			throw new DisabledException(ud.getUsername());
		}
		SecurityUtils.saveAuthentication(authentication);
		return authentication;
	}
}
