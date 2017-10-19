package com.dataagg.security;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import com.dataagg.security.service.IUserService;

public class TokenAccessDecisionVoter implements AccessDecisionVoter<Object> {
	private static final Logger LOG = LoggerFactory.getLogger(TokenAccessDecisionVoter.class);

	private IUserService userService;

	public TokenAccessDecisionVoter(IUserService userService) {
		super();
		this.userService = userService;
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		if (!authentication.isAuthenticated() || authentication.getAuthorities() == null) {
			//没有认证通过或者没有授权的都被拒绝
			return AccessDecisionVoter.ACCESS_DENIED;
		}
		for (ConfigAttribute attr : attributes) {
			String exp = attr.toString();
			LOG.debug(attr.toString());
			if ("authenticated".equals(exp) || "anonymous".equals(exp)) {
				return AccessDecisionVoter.ACCESS_GRANTED;
			} else {
				if (exp.startsWith("hasAuthority('") && exp.endsWith("')")) {
					String authority = exp.substring("hasAuthority('".length(), exp.length() - 2);
					return userService.hasAuthority(authentication, authority) ? AccessDecisionVoter.ACCESS_GRANTED : AccessDecisionVoter.ACCESS_DENIED;
				} else if (exp.startsWith("hasRole('") && exp.endsWith("')")) {
					String role = exp.substring("hasRole('".length(), exp.length() - 2);
					return userService.hasRole(authentication, role) ? AccessDecisionVoter.ACCESS_GRANTED : AccessDecisionVoter.ACCESS_DENIED;
				} else {
					LOG.warn("不支持的表达式:" + exp);
				}
			}
		}
		return AccessDecisionVoter.ACCESS_DENIED;
	}
}
