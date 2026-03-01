package com.matheus.jobs.job_orchestrator.application.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class JobPublisher {

    private SqsClient sqsClient;
    private String queueUrl;

    private static final String QUEUE_NAME = "job-execution";

    @PostConstruct
    public void init() {

        // Conecta no LocalStack (endpoint 4566)
        sqsClient = SqsClient.builder()
                .endpointOverride(java.net.URI.create("http://localhost:4566"))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();

        // Cria a fila (se já existir, retorna URL existente)
        queueUrl = sqsClient.createQueue(CreateQueueRequest.builder()
                        .queueName(QUEUE_NAME)
                        .build())
                .queueUrl();
    }

    public void publish(String jobId) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(jobId)
                .build();

        sqsClient.sendMessage(request);

        System.out.println("Published jobId to LocalStack SQS: " + jobId);
    }
}
