package com.pickmebackend.config;

import com.pickmebackend.config.jwt.JwtAuthenticationEntryPoint;
import com.pickmebackend.config.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtFilter jwtFilter;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.addAllowedOrigin(CorsConfiguration.ALL);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        http
                .csrf()
                    .disable()
                .cors()
                    .configurationSource(source)
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api", "/index", "/api/accounts/**/favorite").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/accounts/", "/api/enterprises", "/api/accounts", "/api/enterprises/", "/api/login").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/experiences", "/api/experiences/", "/api/licenses", "/api/licenses/", "/api/prizes", "/api/prizes/", "/api/projects", "/api/projects/", "/api/selfInterviews", "/api/selfInterviews/").hasRole("USER")
                    .antMatchers(HttpMethod.PUT, "/api/accounts/**", "/api/experiences/**", "/api/licenses/**", "/api/prizes/**", "/api/projects/**", "/api/selfInterviews/**").hasRole("USER")
                    .antMatchers(HttpMethod.DELETE, "/api/accounts/**", "/api/experiences/**", "/api/licenses/**", "/api/prizes/**", "/api/projects/**", "/api/selfInterviews/**").hasRole("USER")
                    .antMatchers(HttpMethod.PUT, "/api/enterprises/**").hasRole("ENTERPRISE")
                    .antMatchers(HttpMethod.DELETE, "/api/enterprises/**").hasRole("ENTERPRISE")
                .anyRequest()
                    .authenticated()
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

}