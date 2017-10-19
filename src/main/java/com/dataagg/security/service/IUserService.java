package com.dataagg.security.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.dataagg.security.SecurityUtils;

public interface IUserService {
	public static final Logger log = LoggerFactory.getLogger(IUserService.class);

	/**
	 * 使用用户名密码登陆,返回认证信息Authentication
	 * @param username
	 * @param userpswd
	 * @return
	 */
	public Authentication login(String username, String userpswd, String remoteAddr) throws AuthenticationException;

	/**
	 * 根据认证信息Authentication生成token信息
	 * @param authentication
	 * @return
	 */
	public String createToken(Authentication authentication);

	/**
	 * 检查token有效性并返回认证信息Authentication
	 * @param token
	 * @return
	 */
	public Authentication checkToken(String token);

	/**
	 * 根据UserDetails获取认证信息Authentication
	 * @param ud
	 * @return
	 */
	public Authentication fetch(UserDetails ud);

	/**
	 * 查询角色们包含的所有权限标识符
	 * @param roles
	 * @return
	 */
	public Collection<String> queryAuthorities(List<String> roles);

	/**
	 * 检查认证信息Authentication是否有权限操作authority
	 * @param authentication
	 * @param authority
	 * @return
	 */
	public default boolean hasAuthority(Authentication authentication, String authority) {
		try {
			List<String> roles = authentication.getAuthorities().stream().map(a -> {
				return a.getAuthority();
			}).collect(Collectors.toList());
			Collection<String> allAuthorities = queryAuthorities(roles);
			log.debug("hasAuthority:" + authority + "->" + roles);
			return SecurityUtils.matchs(authority, allAuthorities);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 检查认证信息Authentication是否role角色权限
	 * @param authentication
	 * @param role
	 * @return
	 */
	public default boolean hasRole(Authentication authentication, String role) {
		try {
			List<String> roles = authentication.getAuthorities().stream().map(a -> {
				return a.getAuthority();
			}).collect(Collectors.toList());
			log.debug("hasRole:" + role + "->" + roles);
			return SecurityUtils.matchs(role, roles);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
}
