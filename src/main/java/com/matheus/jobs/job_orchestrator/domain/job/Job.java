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

    // =========================
    // FACTORY
    // =========================
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

    // =========================
    // BEHAVIOUR
    // =========================

    public void startProcessing() {
        if (status != JobStatus.CREATED) {
            throw new IllegalStateException("Only CREATED jobs can be processed");
        }
        status = JobStatus.PROCESSING;
        touch();
    }

    public void markSuccess() {
        if (status != JobStatus.PROCESSING) {
            throw new IllegalStateException("Only PROCESSING jobs can succeed");
        }
        status = JobStatus.SUCCESS;
        touch();
    }

    public void markFailure() {
        if (status != JobStatus.PROCESSING) {
            throw new IllegalStateException("Only PROCESSING jobs can fail");
        }

        attempts++;

        if (attempts >= MAX_RETRIES) {
            status = JobStatus.FAILED;
        } else {
            status = JobStatus.CREATED;
        }

        touch();
    }

    public void resetForRetry() {
        if (!canRetry()) {
            throw new IllegalStateException(
                    "Job não pode ser reprocessado: attempts = " + attempts
            );
        }
        attempts++;             // incrementa tentativa
        status = JobStatus.CREATED; // reseta para CREATED
        touch();
    }

    public boolean canRetry() {
        return attempts < MAX_RETRIES;
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    // =========================
    // REHYDRATION (Persistence)
    // =========================
    public static Job rehydrate(
            UUID id,
            String type,
            String payload,
            JobStatus status,
            String idempotencyKey,
            int attempts,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        Job job = new Job();
        job.id = id;
        job.type = type;
        job.payload = payload;
        job.status = status;
        job.idempotencyKey = idempotencyKey;
        job.attempts = attempts;
        job.createdAt = createdAt;
        job.updatedAt = updatedAt;
        job.version = version;
        return job;
    }

    // =========================
    // GETTERS ONLY
    // =========================

    public UUID getId() { return id; }
    public String getType() { return type; }
    public String getPayload() { return payload; }
    public JobStatus getStatus() { return status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public int getAttempts() { return attempts; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

}