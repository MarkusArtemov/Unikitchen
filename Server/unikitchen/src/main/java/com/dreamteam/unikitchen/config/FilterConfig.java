package com.dreamteam.unikitchen.config;

import com.dreamteam.unikitchen.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    // Injects the custom JwtAuthenticationFilter
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor-based dependency injection for JwtAuthenticationFilter
    @Autowired
    public FilterConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Registers the JwtAuthenticationFilter as a bean and configures its settings
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();

        // Sets the custom JWT filter
        registrationBean.setFilter(jwtAuthenticationFilter);

        // Configures the URL patterns where the filter will be applied
        registrationBean.addUrlPatterns("/api/*");

        // Sets the order of this filter
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
