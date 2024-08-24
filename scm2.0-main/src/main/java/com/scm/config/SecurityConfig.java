package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.scm.services.impl.SecurityCustomUserDetailService;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityCustomUserDetailService userDetailService;

    @Autowired
    private OAuthAuthenicationSuccessHandler handler;

    @Autowired
    private AuthFailtureHandler authFailtureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers("/user/**").authenticated()
                    .anyRequest().permitAll();
            })
            .formLogin(formLogin -> {
                formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticate")
                    .successForwardUrl("/user/profile")
                    .failureHandler(authFailtureHandler)
                    .usernameParameter("email")
                    .passwordParameter("password");
            })
            .oauth2Login(oauth2Login -> {
                oauth2Login
                    .loginPage("/login")
                    .successHandler(handler);
            })
            .logout(logout -> {
                logout
                    .logoutUrl("/do-logout")
                    .logoutSuccessUrl("/login?logout=true");
            })
            .csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

