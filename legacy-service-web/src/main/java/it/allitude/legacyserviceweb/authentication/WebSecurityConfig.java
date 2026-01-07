package it.allitude.legacyserviceweb.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;




@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig  {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;		
	
	@Autowired
    private CustomAuthenticationProvider authProvider;
	
	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
    }	
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity
			// 	We don't need CSRF for this example
			.csrf().disable()
			// don't authenticate this particular request
			.authorizeHttpRequests()
			// .requestMatchers("/authenticate", "/greeting").permitAll()
			.antMatchers("/api/authenticate", "/api/echo", "/api/echodb").permitAll()
			// Abilito tutte le pagine statiche presenti in /src/main/resources/static
			.antMatchers("/", "/index.html", "/*.js", "/*.css", "/*.ico").permitAll()
			// all other requests need to be authenticated
			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().authenticated()
			.and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.and().sessionManagement()
			// make sure we use stateless session; session won't be used to store user's state.
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpSecurity.authenticationProvider(authProvider);
		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}
	
}
