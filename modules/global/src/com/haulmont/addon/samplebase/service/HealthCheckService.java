package com.haulmont.addon.samplebase.service;


public interface HealthCheckService {
    String NAME = "cubasample_HealthCheckService";

    void healthCheck();
}