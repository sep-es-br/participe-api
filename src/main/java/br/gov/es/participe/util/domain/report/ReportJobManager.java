/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain.report;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class ReportJobManager {
    
    
    private final Map<UUID, JobStatus> jobStatusMap = new ConcurrentHashMap<>();
    private final Map<UUID, Resource> reportContentMap = new ConcurrentHashMap<>();

    public UUID createJob() {
        UUID jobId = UUID.randomUUID();
        jobStatusMap.put(jobId, JobStatus.PROCESSING);
        return jobId;
    }

    public void completeJob(UUID jobId, Resource reportData) {
        jobStatusMap.put(jobId, JobStatus.DONE);
        reportContentMap.put(jobId, reportData);
        scheduleCleanup(jobId);
    }

    public void failJob(UUID jobId) {
        jobStatusMap.put(jobId, JobStatus.ERROR);
        scheduleCleanup(jobId);
    }

    public JobStatus getStatus(UUID jobId) {
        return jobStatusMap.getOrDefault(jobId, null);
    }

    public Resource getReport(UUID jobId) {
        return reportContentMap.get(jobId);
    }

    private void scheduleCleanup(UUID jobId) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            jobStatusMap.remove(jobId);
            reportContentMap.remove(jobId);
        }, 10, TimeUnit.MINUTES); // expira em 10 minutos
    }
    
}
