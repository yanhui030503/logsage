package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserService userService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			return userService.findByEmail(username)
					.map(user -> org.springframework.security.core.userdetails.User
							.withUsername(user.getEmail())
							.password(user.getPassword())
							.roles("USER")
							.build())
					.orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
		};
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/register", "/register/submit", "/css/**", "/h2-console/**")
						.permitAll()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/analyze", true)
						.failureUrl("/login?error=true")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout=true")
						.permitAll())
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/h2-console/**"))
				.headers(headers -> headers
						.frameOptions(frame -> frame.sameOrigin()));

		return http.build();
	}
}
