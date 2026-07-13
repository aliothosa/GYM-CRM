package com.elioth.epam.gymcrm.config;

import com.elioth.epam.gymcrm.interceptor.AuthenticationInterceptor;
import com.elioth.epam.gymcrm.interceptor.RestCallLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

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
                        "/trainee/register",
                        "/trainer/register"
                );
    }

}
