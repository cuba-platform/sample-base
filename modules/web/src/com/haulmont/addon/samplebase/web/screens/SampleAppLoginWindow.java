package com.haulmont.addon.samplebase.web.screens;

import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.DefaultConnection;
import com.haulmont.cuba.web.app.loginwindow.AppLoginWindow;

import java.util.Locale;

public class SampleAppLoginWindow extends AppLoginWindow {

    @Override
    public void ready() {
        try {
            ((DefaultConnection) App.getInstance().getConnection()).loginAfterExternalAuthentication("user", Locale.ENGLISH);
        } catch (LoginException e) {
            throw new RuntimeException("Login error", e);
        }
    }
}