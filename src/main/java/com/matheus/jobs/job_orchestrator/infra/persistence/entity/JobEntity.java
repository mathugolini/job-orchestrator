package com.matheus.jobs.job_orchestrator.infra.persistence.entity;

import com.matheus.jobs.job_orchestrator.domain.job.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jobs")
public class JobEntity {

    @Id
    private UUID id;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(unique = true)
    private String idempotencyKey;

    private int attempts;

    private Instant createdAt;

    private Instant updatedAt;

    @Version
    private Long version;
}