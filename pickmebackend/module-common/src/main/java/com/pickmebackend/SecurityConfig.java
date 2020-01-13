package com.pickmebackend;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception    {

        http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/event")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                .and()
                    .csrf()
                    .disable();
//        http.authorizeRequests()
//                .antMatchers("/", "/oauth2/**", "/login/**",  "/css/**", "/images/**", "/js/**", "/console/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .headers().frameOptions().disable()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
//                .and()
//                .formLogin()
//                .successForwardUrl("/board/list")
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .deleteCookies("JSESSIONID")
//                .invalidateHttpSession(true)
//                .and()
//                .addFilterBefore(filter, CsrfFilter.class)
//                .csrf().disable();
    }

}