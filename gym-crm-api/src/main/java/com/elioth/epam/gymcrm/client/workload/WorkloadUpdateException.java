package com.elioth.epam.gymcrm.client.workload;

public abstract class WorkloadUpdateException extends RuntimeException {

    protected WorkloadUpdateException(Throwable cause) {
        super(cause);
    }
}
