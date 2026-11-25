package com.lookingprof.lookingProf.config;

import com.lookingprof.lookingProf.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    AuthenticationProvider authProvider;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${frontendUrl}")
    private String frontendUrl;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/professionals", "/professionals/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/all", "/user/allActive").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/province", "/user/city", "/user/profession").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/qualification/{qualification}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/{id}/contact").authenticated()
                        .requestMatchers(HttpMethod.GET, "/user/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/user/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/user/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/user/firstName", "/user/email").authenticated()
                        .requestMatchers(HttpMethod.GET, "/profession/**", "/provinces/**", "/city/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/profession/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/profession/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/profession/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/comment/getByUserDestination/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/comment/getByUserDestinationAndQualification/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/comment/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/comment").hasAuthority("ADMIN")
                        .requestMatchers("/comment/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/supportContact/send", "/supportContact/create").permitAll()
                        .requestMatchers("/supportContact/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/publication/**", "/advertisement/**").permitAll()
                        .requestMatchers("/publication/**", "/advertisement/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManager ->
                        sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of(frontendUrl.split(",")));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }
}
