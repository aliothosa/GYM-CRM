package com.elioth.epam.gymcrm.config;

import com.elioth.epam.gymcrm.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;

    public InterceptorConfig(
            AuthenticationInterceptor authenticationInterceptor
    ) {
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/trainee/register",
                        "/trainer/register"
                );
    }

}
