package com.dataagg.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jodd.util.Wildcard;

public class SecurityUtils {
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";

	public static String addAuthentication(HttpServletResponse res, String token) {
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
		return token;
	}

	public static String parseToken(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			token = token.replace(TOKEN_PREFIX, "").trim();
			return token;
		}
		return null;
	}

	public static void saveAuthentication(Authentication authentication) {
		if (authentication != null) {
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}

	public static Authentication getAuthentication() {
		if (SecurityContextHolder.getContext() != null) { return SecurityContextHolder.getContext().getAuthentication(); }
		return null;
	}

	public static Authentication build(Object principal, Object credentials, List<String> authorities) {
		List<GrantedAuthority> authorities2 = authorities.stream().map(a -> (GrantedAuthority) () -> a).collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(principal, credentials, authorities2);
	}

	public static UserDetails getUserDetails(Authentication a) {
		if (a != null && a instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken upaToken = (UsernamePasswordAuthenticationToken) a;
			return (UserDetails) upaToken.getPrincipal();
		}
		return null;
	}

	//FIXME 比较字符串string是否匹配这些模式patterns中的任意一个
	public static boolean matchs(String string, Collection<String> patterns) {
		if (patterns != null) {
			for (String pattern : patterns) {
				if (Wildcard.match(string.toLowerCase(), pattern.toLowerCase())) { return true; }
			}
		}
		return false;
	}
}
