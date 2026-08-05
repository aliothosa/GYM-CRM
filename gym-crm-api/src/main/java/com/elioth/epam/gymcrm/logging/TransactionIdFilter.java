package com.elioth.epam.gymcrm.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Transaction-Id";
    public static final String MDC_KEY = "transactionId";

    private static final Logger LOG =
            LoggerFactory.getLogger(TransactionIdFilter.class);

    private static final Pattern SAFE_TRANSACTION_ID =
            Pattern.compile("[A-Za-z0-9._-]{1,100}");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startNanos = System.nanoTime();
        String transactionId = resolveTransactionId(request);

        MDC.put(MDC_KEY, transactionId);
        response.setHeader(HEADER_NAME, transactionId);

        try {
            LOG.info(
                    "transaction=START method={} endpoint={}",
                    request.getMethod(),
                    request.getRequestURI()
            );

            filterChain.doFilter(request, response);
        } finally {
            long durationMillis =
                    (System.nanoTime() - startNanos) / 1_000_000;

            LOG.info(
                    "transaction=END method={} endpoint={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMillis
            );

            MDC.remove(MDC_KEY);
        }
    }

    private String resolveTransactionId(HttpServletRequest request) {
        String supplied = request.getHeader(HEADER_NAME);

        if (supplied != null
                && SAFE_TRANSACTION_ID.matcher(supplied).matches()) {
            return supplied;
        }

        return UUID.randomUUID().toString();
    }
}