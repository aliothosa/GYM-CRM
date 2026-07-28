package com.elioth.epam.gymcrm.config;

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

    public InterceptorConfig(
            RestCallLoggingInterceptor restCallLoggingInterceptor
    ) {
        this.restCallLoggingInterceptor = restCallLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(restCallLoggingInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    public MappedInterceptor actuatorLoggingMappedInterceptor() {
        return new MappedInterceptor(
                ACTUATOR_PATHS,
                restCallLoggingInterceptor
        );
    }

}
