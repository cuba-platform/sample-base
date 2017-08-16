package com.haulmont.addon.samplebase.web.screens;

import com.google.common.base.Strings;
import com.haulmont.addon.samplebase.BaseConfig;
import com.haulmont.addon.samplebase.web.BaseWindowManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManagerProvider;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.sys.LinkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class SampleLinkHandler extends LinkHandler {

    private Logger log = LoggerFactory.getLogger(SampleLinkHandler.class);

    @Inject
    private BaseConfig config;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private WindowManagerProvider windowManagerProvider;

    public SampleLinkHandler(App app, String action, Map<String, String> requestParams) {
        super(app, action, requestParams);
    }

    @Override
    public void handle() {
        if ("admin".equals(action)) {
            try {
                String accessToken = config.getAccessToken();
                if (Strings.isNullOrEmpty(accessToken)) {
                    log.info("Access token is not set, admin access disabled");
                } else {
                    if (accessToken.equals(requestParams.get("access_token"))) {
                        userSessionSource.getUserSession().setAttribute(BaseWindowManager.ADMIN_ACCESS_ATTR, true);
                        log.info("Granted admin access to session " + userSessionSource.getUserSession().getId());
                    } else {
                        windowManagerProvider.get().showNotification("Access denied");
                        log.info("Invalid access token, admin access disabled for session " + userSessionSource.getUserSession().getId());
                    }
                }
            } finally {
                requestParams.clear();
            }
        } else {
            super.handle();
        }
    }
}
