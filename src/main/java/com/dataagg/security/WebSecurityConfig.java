package com.dataagg.security;

import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dataagg.security.filter.TokenAuthenticationFilter;
import com.dataagg.security.filter.TokenLoginFilter;
import com.dataagg.security.service.IUserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private IUserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	//根据实际情况配置Spring Security
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//		ProviderManager pm = (ProviderManager) authenticationManager;
		//		pm.getProviders().forEach(t -> LOG.debug(t.getClass().getName()));
		//@formatter:off
		http.csrf().disable().authorizeRequests()
			.accessDecisionManager(accessDecisionManager())
			.antMatchers("/").permitAll()
			.antMatchers(HttpMethod.POST, "/login").permitAll()
			//FIXME 配置url对应的权限, 目前只支持anonymous,authenticated,hasAuthority和hasRole
			.antMatchers("/users").hasAuthority("user_view")
			.antMatchers("/users2").hasAuthority("user_edit")
			//url权限配置结束
			.anyRequest().authenticated().and()
			// We filter the api/login requests
			.addFilterBefore(tokenLoginFilter(), UsernamePasswordAuthenticationFilter.class)
			// And filter other requests to check the presence of token in header
			.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
		;
		//@formatter:on
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new TokenAuthenticationProvider();
	}

	@Bean
	public AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = Lists.newArrayList(tokenAccessDecisionVoter());
		AccessDecisionManager accessDecisionManager = new AffirmativeBased(decisionVoters);
		return accessDecisionManager;
	}

	@Bean
	public TokenAccessDecisionVoter tokenAccessDecisionVoter() {
		return new TokenAccessDecisionVoter(userService);
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(userService);
	}

	@Bean
	public TokenLoginFilter tokenLoginFilter() {
		return new TokenLoginFilter("/login", userService, authenticationManager);
	}

	//用户登陆密码的加密器
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//FIXME 直接使用内存UserDetailsManager简化代码
	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		createUser(manager, "admin", "ADMIN");
		createUser(manager, "user", "USER");
		createUser(manager, "guest", "GUEST");
		return manager;
	}

	//FIXME 使用InMemoryUserDetailsManager创建用户的便捷方法,可移除
	private void createUser(InMemoryUserDetailsManager manager, String userName, String... roles) {
		if (passwordEncoder == null) {
			passwordEncoder = passwordEncoder();
		}
		manager.createUser(User.withUsername(userName).password(passwordEncoder.encode(userName)).roles(roles).build());
	}
}
