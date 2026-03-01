package com.matheus.jobs.job_orchestrator.infra.persistence.mapper;

import com.matheus.jobs.job_orchestrator.domain.job.Job;
import com.matheus.jobs.job_orchestrator.infra.persistence.entity.JobEntity;

public class JobMapper {

    public static JobEntity toEntity(Job job) {

        JobEntity entity = new JobEntity();
        entity.setId(job.getId());
        entity.setType(job.getType());
        entity.setPayload(job.getPayload());
        entity.setStatus(job.getStatus());
        entity.setIdempotencyKey(job.getIdempotencyKey());
        entity.setAttempts(job.getAttempts());
        entity.setCreatedAt(job.getCreatedAt());
        entity.setUpdatedAt(job.getUpdatedAt());
        entity.setVersion(job.getVersion());

        return entity;
    }

    public static Job toDomain(JobEntity entity) {

        return Job.rehydrate(
                entity.getId(),
                entity.getType(),
                entity.getPayload(),
                entity.getStatus(),
                entity.getIdempotencyKey(),
                entity.getAttempts(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
}

}
