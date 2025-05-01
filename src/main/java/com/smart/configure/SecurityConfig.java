package com.smart.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.smart.repo.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	  @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	        provider.setUserDetailsService(customUserDetailsService);
	        provider.setPasswordEncoder(passwordEncoder());
	        return provider;
	    
	
//	@Bean
//	public UserDetailsService detailsService() {
//		UserDetails normalUser = User
//				.withUsername("asjad01122@gmail.com")
//				.password(passwordEncoder().encode("22222"))
//				.roles("NORMAL")
//				.build();
//		UserDetails normalUser1 = User
//				.withUsername("ayankhan01122@gmail.com")
//				.password(passwordEncoder().encode("ayan"))
//				.roles("NORMAL")
//				.build();
//		UserDetails adminlUser = User
//				.withUsername("admin@gmail.com")
//				.password(passwordEncoder().encode("admin"))
//				.roles("ADMIN")
//				.build();
//		UserDetails adminlUser1 = User
//				.withUsername("admin1@gmail.com")
//				.password(passwordEncoder().encode("admin1"))
//				.roles("ADMIN")
//				.build();
//		InMemoryUserDetailsManager imud = new InMemoryUserDetailsManager(normalUser,adminlUser,normalUser1,adminlUser1);
//		
//		return imud;		
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable()
		.authorizeRequests()
		.requestMatchers("/user/**").hasRole("NORMAL")
		.requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/**","/home/**","/css/**", "/js/**", "/image/**").permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.formLogin();
		
		return httpSecurity.build();
	}
}
