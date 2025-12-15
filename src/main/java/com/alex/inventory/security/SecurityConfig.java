package com.alex.inventory.security;

import com.alex.inventory.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers("/inventory").permitAll()
                        .requestMatchers("/inventory/terminals").hasRole("VIEW")
                        .requestMatchers("/inventory/terminals/add",
                                "/inventory/terminals/save",
                                "/inventory/terminals/edit/**").hasRole("MODIFY")
                        .requestMatchers("/inventory/terminals/delete/**").hasRole("ALL")

                        .requestMatchers("/inventory/users/**").hasRole("ALL")
                        .anyRequest().authenticated()
        ).formLogin(form ->
                form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticate")
                        .defaultSuccessUrl("/inventory", true)
                        .permitAll()
        ).logout(logout -> logout.permitAll()
        ).exceptionHandling(handling ->
                handling.accessDeniedPage("/access-denied")
        );

        http.httpBasic(Customizer.withDefaults());
        //http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
