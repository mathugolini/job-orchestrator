package com.matheus.jobs.job_orchestrator.domain.job;

import java.time.Instant;
import java.util.UUID;

public class Job {

    private UUID id;
    private String type;
    private String payload;
    private JobStatus status;
    private String idempotencyKey;
    private int attempts;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private static final int MAX_RETRIES = 3;

    private Job() {}

    public static Job create(String type, String payload, String idempotencyKey) {

        Job job = new Job();
        job.id = UUID.randomUUID();
        job.type = type;
        job.payload = payload;
        job.status = JobStatus.CREATED;
        job.idempotencyKey = idempotencyKey;
        job.attempts = 0;
        job.createdAt = Instant.now();
        job.updatedAt = Instant.now();

        return job;
    }

    public void markProcessing() {
        if (status != JobStatus.CREATED) {
            throw new IllegalStateException("Job cannot be processed from status: " + status);
        }
        this.status = JobStatus.PROCESSING;
        touch();
    }

    public void markSuccess() {
        if (status != JobStatus.PROCESSING) {
            throw new IllegalStateException("Job cannot succeed from status: " + status);
        }
        this.status = JobStatus.SUCCESS;
        touch();
    }

    public void markFailure() {
        if (status != JobStatus.PROCESSING) {
            throw new IllegalStateException("Job cannot fail from status: " + status);
        }

        attempts++;

        if (attempts >= MAX_RETRIES) {
            this.status = JobStatus.FAILED;
        } else {
            this.status = JobStatus.CREATED;
        }

        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public boolean canRetry() {
        return attempts < MAX_RETRIES;
    }

}
