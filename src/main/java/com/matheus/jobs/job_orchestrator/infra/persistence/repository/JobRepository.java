package com.matheus.jobs.job_orchestrator.infra.persistence.repository;

import com.matheus.jobs.job_orchestrator.domain.job.JobStatus;
import com.matheus.jobs.job_orchestrator.infra.persistence.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    Optional<JobEntity> findByIdempotencyKey(String idempotencyKey);

    List<JobEntity> findByStatus(JobStatus status);

}
