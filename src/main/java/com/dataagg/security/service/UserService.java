package com.dataagg.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dataagg.security.SecurityUtils;

@Service
public class UserService implements IUserService {
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	//FIXME 临时使用两个map存储token-username和username-authorities
	private static final Map<String, String> tokens = new Hashtable<>();
	private static final Map<String, List<String>> roleAuthorities = new Hashtable<>();
	static {
		roleAuthorities.put("GUEST", Lists.newArrayList("home", "user_view"));
		roleAuthorities.put("ADMIN", Lists.newArrayList("home", "user_view", "user_edit"));
	}

	/* (non-Javadoc)
	 * @see com.dataagg.security.IUserService#login(java.lang.String, java.lang.String)
	 */
	@Override
	public Authentication login(String username, String userpswd, String remoteAddr) throws AuthenticationException {
		if (username == null || username.trim().length() < 1) {
			//username错误!
			throw new BadCredentialsException(username);
		}
		username = username.trim();
		UserDetails ud = userDetailsService.loadUserByUsername(username);
		if (!passwordEncoder.matches(userpswd, ud.getPassword())) {
			//密码错误
			throw new BadCredentialsException(username);
		}
		Authentication a = fetch(ud);
		return a;
	}

	/* (non-Javadoc)
	 * @see com.dataagg.security.IUserService#createToken(org.springframework.security.core.Authentication)
	 */
	@Override
	public String createToken(Authentication authentication) {
		String token = null;
		UserDetails ud = SecurityUtils.getUserDetails(authentication);
		if (ud != null) {
			String username = ud.getUsername();
			//FIXME 根据UserDetails创建token
			if (!tokens.containsValue(username)) {
				token = username + "" + System.currentTimeMillis();
				tokens.put(token, username);
			} else {
				for (String t : tokens.keySet()) {
					String un = tokens.get(t);
					if (un.equals(username)) {
						token = t;
						break;
					}
				}
			}
		}
		return token;
	}

	/* (non-Javadoc)
	 * @see com.dataagg.security.IUserService#checkToken(java.lang.String)
	 */
	@Override
	public Authentication checkToken(String token) {
		//FIXME 直接从map中获取token对应的username
		if (token != null) {
			String username = tokens.get(token);
			if (username != null) {
				UserDetails ud = userDetailsService.loadUserByUsername(username);
				return fetch(ud);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dataagg.security.IUserService#fetch(org.springframework.security.core.userdetails.UserDetails)
	 */
	@Override
	public Authentication fetch(UserDetails ud) {
		String username = ud.getUsername();
		//使用principal保存UserDetails, authorities保存所有role
		//FIXME username大写对应角色role
		return SecurityUtils.build(ud, null, Lists.newArrayList(username.toUpperCase()));
	}

	/* (non-Javadoc)
	 * @see com.dataagg.security.IUserService#queryAuthorities(java.util.List)
	 */
	@Override
	public Collection<String> queryAuthorities(List<String> roles) {
		Set<String> authorities = new HashSet<>();
		if (roles != null) {
			for (String role : roles) {
				if (roleAuthorities.get(role) != null) {
					authorities.addAll(roleAuthorities.get(role));
				}
			}
		}
		return authorities;
	}
}
