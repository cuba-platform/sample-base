package com.haulmont.addon.samplebase.service;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(HealthCheckService.NAME)
public class HealthCheckServiceBean implements HealthCheckService {

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void healthCheck() {
        dataManager.reload(userSessionSource.getUserSession().getUser(), View.LOCAL);
    }
}