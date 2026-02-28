package com.matheus.jobs.job_orchestrator.domain.job;

public enum JobStatus {

    CREATED,
    PROCESSING,
    SUCCESS,
    FAILED;

    public boolean isFinal() {
        return this == SUCCESS || this == FAILED;
    }
}
