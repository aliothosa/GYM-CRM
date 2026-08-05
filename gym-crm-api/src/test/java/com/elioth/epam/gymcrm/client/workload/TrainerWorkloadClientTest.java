package com.elioth.epam.gymcrm.client.workload;

import com.elioth.epam.gymcrm.logging.TransactionIdFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.util.function.Supplier;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TrainerWorkloadClientTest {

    @AfterEach
    void clearTransactionId() {
        org.slf4j.MDC.remove(TransactionIdFilter.MDC_KEY);
    }

    @Test
    void postsWorkloadUpdateToTheServiceContractUri() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        CurrentBearerTokenProvider tokenProvider = mock(CurrentBearerTokenProvider.class);
        CircuitBreakerFactory<?, ?> circuitBreakerFactory = mock(CircuitBreakerFactory.class);
        CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        when(tokenProvider.requireToken()).thenReturn("signed-test-token");
        when(circuitBreakerFactory.create("trainerWorkload")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(
                ArgumentMatchers.<Supplier<Object>>any(),
                ArgumentMatchers.any()
        )).thenAnswer(invocation -> invocation.<Supplier<Object>>getArgument(0).get());

        TrainerWorkloadClient client = new TrainerWorkloadClient(
                builder,
                circuitBreakerFactory,
                tokenProvider,
                "http://trainer-workload-service"
        );
        org.slf4j.MDC.put(TransactionIdFilter.MDC_KEY, "tx-workload-uri");

        server.expect(once(), requestTo("http://trainer-workload-service/workloads"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer signed-test-token"))
                .andExpect(header(TransactionIdFilter.HEADER_NAME, "tx-workload-uri"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        client.updateWorkload(new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, "ADD"
        ));

        server.verify();
    }

    @Test
    void mapsConnectionFailureToAnObservableUnavailableError() {
        TrainerWorkloadClient client = clientWithFailingCircuitBreaker(new ResourceAccessException(
                "connection refused", new ConnectException("connection refused")
        ));

        assertThrows(WorkloadServiceUnavailableException.class,
                () -> client.updateWorkload(request()));
    }

    @Test
    void mapsReadTimeoutToAnObservableTimeoutError() {
        TrainerWorkloadClient client = clientWithFailingCircuitBreaker(new ResourceAccessException(
                "read timed out", new SocketTimeoutException("read timed out")
        ));

        assertThrows(WorkloadServiceTimeoutException.class,
                () -> client.updateWorkload(request()));
    }

    private TrainerWorkloadClient clientWithFailingCircuitBreaker(Throwable failure) {
        CurrentBearerTokenProvider tokenProvider = mock(CurrentBearerTokenProvider.class);
        CircuitBreakerFactory<?, ?> circuitBreakerFactory = mock(CircuitBreakerFactory.class);
        CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create("trainerWorkload")).thenReturn(circuitBreaker);
        when(circuitBreaker.run(ArgumentMatchers.<Supplier<Object>>any(), ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    Function<Throwable, Object> fallback = invocation.getArgument(1);
                    return fallback.apply(failure);
                });
        return new TrainerWorkloadClient(RestClient.builder(), circuitBreakerFactory, tokenProvider,
                "http://workload-service.test");
    }

    private TrainerWorkloadRequest request() {
        return new TrainerWorkloadRequest("john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 5), 60L, "ADD");
    }
}
