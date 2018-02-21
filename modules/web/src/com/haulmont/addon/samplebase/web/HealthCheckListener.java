package com.haulmont.addon.samplebase.web;

import com.haulmont.addon.samplebase.service.HealthCheckService;
import com.haulmont.cuba.core.global.HealthCheckEvent;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.auth.AuthenticationDetails;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.security.AnonymousUserCredentials;
import com.haulmont.cuba.web.security.providers.AnonymousLoginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class HealthCheckListener implements ApplicationListener<HealthCheckEvent> {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckListener.class);

    @Inject
    private AnonymousLoginProvider loginProvider;

    @Inject
    private HealthCheckService healthCheckService;

    @Override
    public void onApplicationEvent(HealthCheckEvent event) {
        log.info("Health check");

        AnonymousUserCredentials credentials = new AnonymousUserCredentials();
        try {
            AuthenticationDetails authenticationDetails = loginProvider.login(credentials);
            if (authenticationDetails == null) {
                error(event, null);
            } else {
                UserSession userSession = authenticationDetails.getSession();
                AppContext.withSecurityContext(new SecurityContext(userSession), () -> {
                    healthCheckService.healthCheck();
                });
            }
        } catch (Exception e) {
            error(event, e);
        }
    }

    private void error(HealthCheckEvent event, Exception e) {
        log.error("Health check error", e != null ? e.getMessage() : "");
        event.setResponse("error");
    }
}
