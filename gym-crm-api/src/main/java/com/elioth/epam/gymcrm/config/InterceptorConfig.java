package com.elioth.epam.gymcrm.config;

import com.elioth.epam.gymcrm.interceptor.AuthenticationInterceptor;
import com.elioth.epam.gymcrm.interceptor.RestCallLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private static final String[] ACTUATOR_PATHS = {
            "/actuator",
            "/actuator/**"
    };

    private final RestCallLoggingInterceptor restCallLoggingInterceptor;
    private final AuthenticationInterceptor authenticationInterceptor;

    public InterceptorConfig(
            RestCallLoggingInterceptor restCallLoggingInterceptor,
            AuthenticationInterceptor authenticationInterceptor
    ) {
        this.restCallLoggingInterceptor = restCallLoggingInterceptor;
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(restCallLoggingInterceptor)
                .addPathPatterns("/**");

        registry
                .addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/trainees/register",
                        "/trainers/register",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/error"
                );
    }

    @Bean
    public MappedInterceptor actuatorLoggingMappedInterceptor() {
        return new MappedInterceptor(
                ACTUATOR_PATHS,
                restCallLoggingInterceptor
        );
    }

    @Bean
    public MappedInterceptor actuatorAuthenticationMappedInterceptor() {
        return new MappedInterceptor(
                ACTUATOR_PATHS,
                authenticationInterceptor
        );
    }

}
