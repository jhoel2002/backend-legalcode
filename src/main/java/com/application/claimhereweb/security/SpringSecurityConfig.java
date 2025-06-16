package com.application.claimhereweb.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.application.claimhereweb.security.filter.JwtAuthenticationFilter;
import com.application.claimhereweb.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((authz) -> authz
                // ENDPOINTS DE BUFFET
                .requestMatchers(HttpMethod.POST, "/api/buffet/save").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/buffet/{code}/logo").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/buffet/enable").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/buffet/**").permitAll()
                // ENDPOINTS DE CUSTOMER
                .requestMatchers(HttpMethod.POST, "/api/customer/save").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/customer/updateCustomer/{code}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/customer/**").permitAll()
                // ENDPOINTS DE USER
                .requestMatchers(HttpMethod.PATCH, "/api/users/enable").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                // ENDPOINTS DE LAWYER
                .requestMatchers(HttpMethod.POST, "/api/lawyer/save").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/lawyer/updateLawyer/{code}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/lawyer/**").permitAll()
                // ENDPOINTS DE CASE REQUEST
                .requestMatchers(HttpMethod.POST, "/api/caseRequest/save/{codeCustomer}").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/casesRequest/updateStatus").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/casesRequest/assignLawyer").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/casesRequest/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/casesRequest/updateInfo/{code}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/caseRequest/saveEvidenceMassive/{codeCustomer}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/caseRequest/saveEvidenceMassiveQuotation/{codeCustomer}")
                .permitAll()

                .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/factures/export-pdf").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/factures/export-xls").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cases/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/casesRequest/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/s3/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/customer/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/cases/registerCase/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/casesRequest/registerCaseRequest/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/factures/registerFacture/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/s3/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/customer/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/buffet/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/document/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/casesRequest/statusCaseRequest/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/cases/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/webjars/**",
                        "/swagger-resources/**")
                .permitAll()
                // TIENE QUE SER POR ORDEN GET -> POST -> PUT -> DELETE
                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}