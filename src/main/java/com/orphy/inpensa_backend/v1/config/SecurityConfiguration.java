package com.orphy.inpensa_backend.v1.config;

import com.orphy.inpensa_backend.v1.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!dev")
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize

                        //Actuator
                        .requestMatchers(HttpMethod.GET, "/actuator/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope())
                        .requestMatchers(HttpMethod.POST, "/actuator/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope(), Role.ADMIN_WRITE.getScope())
                        .requestMatchers(HttpMethod.PUT, "/actuator/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope(), Role.ADMIN_WRITE.getScope())
                        .requestMatchers(HttpMethod.DELETE, "/actuator/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope(), Role.ADMIN_WRITE.getScope())

                        //Transactions
                        .requestMatchers(HttpMethod.GET, "/transactions/admin/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope())
                        .requestMatchers(HttpMethod.POST, "/transactions/admin/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope())
                        .requestMatchers(HttpMethod.PUT, "/transactions/admin/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope())
                        .requestMatchers(HttpMethod.DELETE, "/transactions/admin/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope())

                        .requestMatchers(HttpMethod.GET, "/transactions/**")
                            .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope(), Role.USER_READ.getScope())
                        .requestMatchers(HttpMethod.POST, "/transactions/**")
                            .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope(), Role.USER_WRITE.getScope())
                        .requestMatchers(HttpMethod.PUT, "/transactions/**")
                            .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope(), Role.USER_WRITE.getScope())
                        .requestMatchers(HttpMethod.DELETE, "/transactions/**")
                            .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope(), Role.USER_WRITE.getScope())


                        //Users
                        .requestMatchers(HttpMethod.GET, "/users/admin/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope())

                        .requestMatchers(HttpMethod.GET, "/users/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_READ.getScope(), Role.USER_READ.getScope())
                        .requestMatchers(HttpMethod.PUT, "/users/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope(), Role.USER_WRITE.getScope())
                        .requestMatchers(HttpMethod.DELETE, "/users/**")
                        .hasAnyAuthority(Role.SUPER_ADMIN.getScope(), Role.ADMIN_WRITE.getScope(), Role.USER_WRITE.getScope())

                        .anyRequest()
                            .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(CsrfConfigurer::disable)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}