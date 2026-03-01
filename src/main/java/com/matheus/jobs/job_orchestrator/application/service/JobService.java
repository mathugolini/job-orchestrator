package com.matheus.jobs.job_orchestrator.application.service;

import com.matheus.jobs.job_orchestrator.domain.job.Job;
import com.matheus.jobs.job_orchestrator.infra.persistence.mapper.JobMapper;
import com.matheus.jobs.job_orchestrator.infra.persistence.repository.JobRepository;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

public class JobService {

    private final JobRepository jobRepository;
    private final JobPublisher jobPublisher;

    public JobService(JobRepository jobRepository, JobPublisher jobPublisher) {
        this.jobRepository = jobRepository;
        this.jobPublisher = jobPublisher;
    }

    @Transactional
    public Job createJob(String type, String payload, String idempotencyKey) {

        // Checa idempotência
        Optional<Job> existing = jobRepository.findByIdempotencyKey(idempotencyKey)
                .map(JobMapper::toDomain); // mapeia JobEntity → Job
        if (existing.isPresent()) {
            return existing.get(); // retorna sem criar duplicado
        }

        // Cria job
        Job job = Job.create(type, payload, idempotencyKey);

        // Salva no banco
        jobRepository.save(JobMapper.toEntity(job));

        // Publica na fila (simulada)
        jobPublisher.publish(job.getId().toString());

        return job;
    }

    public Optional<Job> getJobById(String id) {
        return jobRepository.findById(UUID.fromString(id))
                .map(JobMapper::toDomain);
    }

    @Transactional
    public void retryJob(Job job) {

        // Usa o metodo de dominio para resetar
        job.resetForRetry();

        // Reseta o job para ser reprocessado usando lógica de domínio
        job.markFailure();

        // Salva como entidade no banco
        jobRepository.save(JobMapper.toEntity(job));

        // Reenvia para fila
        jobPublisher.publish(job.getId().toString());
    }
}
