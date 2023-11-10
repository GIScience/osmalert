package org.heigit.osmalert.webapp;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.*;
import org.springframework.security.web.*;

import static org.springframework.security.config.Customizer.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public UserDetailsService userDetailsService(@Qualifier("osmalert") OsmalertConfiguration config) {
		UserDetails user =
			User.withDefaultPasswordEncoder()
				.username(config.getWebUsername())
				.password(config.getWebPassword())
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
													  .anyRequest().authenticated()
			)
			.httpBasic(withDefaults())
			.formLogin(withDefaults())
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}
}