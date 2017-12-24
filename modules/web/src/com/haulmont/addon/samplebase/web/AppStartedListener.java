package com.haulmont.addon.samplebase.web;

import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.security.ExternalUserCredentials;
import com.haulmont.cuba.web.security.events.AppStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppStartedListener implements ApplicationListener<AppStartedEvent> {

    @Override
    public void onApplicationEvent(AppStartedEvent event) {
        ExternalUserCredentials credentials = new ExternalUserCredentials("user");
        try {
            event.getApp().getConnection().login(credentials);
        } catch (LoginException e) {
            throw new RuntimeException("Unable to login", e);
        }
    }
}
