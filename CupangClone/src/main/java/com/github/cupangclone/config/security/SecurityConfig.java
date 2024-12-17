package com.github.cupangclone.config.security;

import com.github.cupangclone.service.security.CustomLoginFailureHandler;
import com.github.cupangclone.service.security.LogoutHandlerImpl;
import com.github.cupangclone.service.security.LogoutSuccessHandlerImpl;
import com.github.cupangclone.web.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LogoutHandlerImpl logoutHandler;

    private final JwtTokenProvider jwtTokenProvider;

    private static final String[] AUTH_WHITELIST = {
            "/api/signup/register", "/api/signup/login", "/api/signup/seller/register",
            "/api-docs", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui-custom.html"
    };

    private static final String[] AUTH_SELLER_WHITELIST = {
            "/api/sell_items/**"
    };

    private static final String[] AUTH_USER_WHITELIST = {
            "/api/signup/resign/*", "/api/items/**", "/api/items/search/*",
            "/api/users/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutSuccessHandlerImpl logoutSuccessHandlerImpl) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests( auth
                    -> auth.requestMatchers(AUTH_USER_WHITELIST)
                    .hasAnyRole("USER", "SELLER", "ADMIN")
                    .requestMatchers(AUTH_SELLER_WHITELIST)
                    .hasAnyRole("SELLER", "ADMIN")
                    .requestMatchers(AUTH_WHITELIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .addFilterBefore(new JwtAuthFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults())
            .sessionManagement( sessionManagement ->
                    sessionManagement
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .cors( cors -> corsConfigurationSource())
            .headers(httpSecurityHeadersConfigurer ->
                    httpSecurityHeadersConfigurer
                            .frameOptions(HeadersConfigurer
                                    .FrameOptionsConfig::sameOrigin))
            .logout( logout -> logout.logoutUrl("/api/signup/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler(logoutSuccessHandlerImpl)
            );

        return http.build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public CustomLoginFailureHandler customLoginFailureHandler() {
        return new CustomLoginFailureHandler();
    }

}
