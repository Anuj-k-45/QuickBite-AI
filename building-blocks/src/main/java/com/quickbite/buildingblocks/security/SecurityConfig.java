package com.quickbite.buildingblocks.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.buildingblocks.exceptions.ErrorResponse;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ObjectMapper objectMapper) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {

        return authConfig.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnBean(UserDetailsService.class)
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // =====================================================
                        // 1. PUBLIC ENDPOINTS
                        // =====================================================

                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()

                        // Public restaurant APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/restaurants",
                                "/api/v1/restaurants/*",
                                "/api/v1/restaurants/*/catalog")
                        .permitAll()

                        // Public Search APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/search/restaurants",
                                "/api/v1/search/menu-items")
                        .permitAll()


                        // =====================================================
                        // 2. CUSTOMER ROUTES
                        // =====================================================

                        .requestMatchers(
                                "/api/v1/customers/**")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        // =====================================================
                        // 3. RESTAURANT OWNER ROUTES
                        // =====================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/restaurants")
                        .hasAnyRole("OWNER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/restaurants/**")
                        .hasAnyRole("OWNER", "ADMIN")

                        .requestMatchers(
                                "/api/v1/owner/restaurants/**")
                        .hasAnyRole("OWNER", "ADMIN")

                        // =====================================================
                        // 4. DRIVER ROUTES
                        // =====================================================

                        .requestMatchers(
                                "/api/v1/drivers/**")
                        .hasAnyRole("DRIVER", "ADMIN")

                        // =====================================================
                        // 5. ORDER ROUTES
                        // =====================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/orders")
                        .hasAnyRole("CUSTOMER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/orders/**")
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/orders/*/status")
                        .hasAnyRole("OWNER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/orders/*/assign-driver")
                        .hasAnyRole("DRIVER", "ADMIN")

                        // =====================================================
                        // 6. USER PROFILE / ADMIN ROUTES
                        // =====================================================

                        .requestMatchers(
                                "/api/v1/users/me")
                        .authenticated()

                        .requestMatchers(
                                "/api/v1/users/**")
                        .hasRole("ADMIN")

                        // =====================================================
                        // 7. EVERYTHING ELSE
                        // =====================================================

                        .anyRequest()
                        .authenticated())

                .exceptionHandling(exceptions -> exceptions

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    if (!response.isCommitted()) {

                                        response.setStatus(
                                                HttpServletResponse.SC_UNAUTHORIZED);

                                        response.setContentType(
                                                MediaType.APPLICATION_JSON_VALUE);

                                        ErrorResponse error = new ErrorResponse(
                                                401,
                                                "Unauthorized",
                                                "Full authentication is required to access this resource",
                                                request.getRequestURI());

                                        response.getWriter().write(
                                                objectMapper.writeValueAsString(
                                                        error));

                                        response.getWriter().flush();
                                        response.flushBuffer();
                                    }
                                })

                        .accessDeniedHandler(
                                (request, response,
                                        accessDeniedException) -> {

                                    if (!response.isCommitted()) {

                                        response.setStatus(
                                                HttpServletResponse.SC_FORBIDDEN);

                                        response.setContentType(
                                                MediaType.APPLICATION_JSON_VALUE);

                                        ErrorResponse error = new ErrorResponse(
                                                403,
                                                "Forbidden",
                                                "You do not have sufficient permissions to access this resource",
                                                request.getRequestURI());

                                        response.getWriter().write(
                                                objectMapper.writeValueAsString(
                                                        error));

                                        response.getWriter().flush();
                                        response.flushBuffer();
                                    }
                                }))

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}