package com.elioth.epam.gymcrm.client.workload;

import com.elioth.epam.gymcrm.logging.TransactionIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import org.springframework.web.client.RestClient;

@Component
public class TrainerWorkloadClient {

    private static final Logger LOG =
            LoggerFactory.getLogger(TrainerWorkloadClient.class);

    private static final String CIRCUIT_BREAKER_NAME = "trainerWorkload";

    private final RestClient restClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final CurrentBearerTokenProvider tokenProvider;

    public TrainerWorkloadClient(
            @Qualifier("workloadRestClientBuilder") RestClient.Builder restClientBuilder,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory,
            CurrentBearerTokenProvider tokenProvider,
            @Value("${gymcrm.workload-service.base-url}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.tokenProvider = tokenProvider;
    }

    public void updateWorkload(TrainerWorkloadRequest request) {
        CircuitBreaker circuitBreaker =
                circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME);
        String token = tokenProvider.requireToken();
        String transactionId = MDC.get(TransactionIdFilter.MDC_KEY);

        circuitBreaker.run(
                () -> {
                    callWorkloadService(request, token, transactionId);
                    return null;
                },
                throwable -> {
                    throw mapFailure(request, throwable);
                }
        );
    }

    private void callWorkloadService(
            TrainerWorkloadRequest request,
            String token,
            String transactionId
    ) {

        LOG.info(
                "operation=CALL_WORKLOAD_SERVICE action={} trainerUsername={}",
                request.actionType(),
                request.trainerUsername()
        );

        restClient.post()
                .uri("/workloads")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TransactionIdFilter.HEADER_NAME, transactionId)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        LOG.info(
                "operation=CALL_WORKLOAD_SERVICE result=SUCCESS action={} trainerUsername={}",
                request.actionType(),
                request.trainerUsername()
        );
    }

    private WorkloadUpdateException mapFailure(
            TrainerWorkloadRequest request,
            Throwable throwable
    ) {
        LOG.error(
                "operation=CALL_WORKLOAD_SERVICE result=FAILED action={} trainerUsername={} exceptionType={} rootCauseType={} message={}",
                request.actionType(),
                request.trainerUsername(),
                throwable.getClass().getSimpleName(),
                rootCause(throwable).getClass().getSimpleName(),
                throwable.getMessage()
        );

        if (isTimeout(throwable)) {
            return new WorkloadServiceTimeoutException(throwable);
        }
        if (throwable instanceof RestClientException) {
            return new WorkloadServiceUnavailableException(throwable);
        }
        return new WorkloadServiceUnavailableException(throwable);
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }
}
