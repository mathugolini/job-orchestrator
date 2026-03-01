package com.matheus.jobs.job_orchestrator.infra.persistence.queue;

import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.partitions.model.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566")) // LocalStack default
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();
    }
}
