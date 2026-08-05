package com.elioth.epam.gymcrm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class WorkloadClientConfig {

    /**
     * Eureka must reach its configured server URL directly.  Keeping this
     * builder as the primary candidate prevents Eureka's own RestClient from
     * receiving the load-balancer interceptor intended for service calls.
     */
    @Bean
    @Primary
    public RestClient.Builder eurekaRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean("workloadRestClientBuilder")
    public RestClient.Builder workloadRestClientBuilder(
            @Value("${gymcrm.workload-service.connect-timeout:2s}") Duration connectTimeout,
            @Value("${gymcrm.workload-service.read-timeout:3s}") Duration readTimeout,
            LoadBalancerClient loadBalancerClient
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return RestClient.builder()
                .requestFactory(requestFactory)
                .requestInterceptor(new LoadBalancerInterceptor(loadBalancerClient));
    }
}
