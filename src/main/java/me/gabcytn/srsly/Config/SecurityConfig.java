package me.gabcytn.srsly.Config;

import me.gabcytn.srsly.Filter.JwtFilter;
import me.gabcytn.srsly.Service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final UserDetailsServiceImpl userDetailsServiceImpl;
  private final JwtFilter jwtFilter;

  public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, JwtFilter jwtFilter) {
    this.userDetailsServiceImpl = userDetailsServiceImpl;
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider =
        new DaoAuthenticationProvider(userDetailsServiceImpl);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

    return daoAuthenticationProvider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(
            httpSecuritySessionManagementConfigurer -> {
              httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                  SessionCreationPolicy.STATELESS);
            })
        .authorizeHttpRequests(
            authorizationManagerRequestMatcherRegistry -> {
              authorizationManagerRequestMatcherRegistry
                  .requestMatchers("/api/v1/auth/**")
                  .permitAll()
                  .anyRequest()
                  .authenticated();
            })
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
