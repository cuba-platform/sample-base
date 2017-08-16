package com.haulmont.addon.samplebase.web;

import com.haulmont.addon.samplebase.BaseConfig;
import com.haulmont.addon.samplebase.web.screens.HasInfoFrame;
import com.haulmont.addon.samplebase.web.screens.InfoFrame;
import com.haulmont.cuba.core.global.AccessDeniedException;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.security.entity.PermissionType;
import com.haulmont.cuba.web.WebWindowManager;

import java.util.ArrayList;
import java.util.List;

public class BaseWindowManager extends WebWindowManager {

    public static final String ADMIN_ACCESS_ATTR = "admin_access";

    @Override
    protected void showWindow(Window window, String caption, String description, OpenType type, boolean multipleOpen) {
        Boolean adminAccess = userSessionSource.getUserSession().getAttribute(ADMIN_ACCESS_ATTR);
        BaseConfig config = configuration.getConfig(BaseConfig.class);
        if (!Boolean.TRUE.equals(adminAccess)
                && !window.getId().startsWith(config.getPermittedScreenPrefix())
                && !config.getPermittedScreens().contains(window.getId())) {
            throw new AccessDeniedException(PermissionType.SCREEN, window.getId());
        }

        super.showWindow(window, caption, description, type, multipleOpen);

        Window.TopLevelWindow topLevelWindow = getApp().getTopLevelWindow();
        if (topLevelWindow instanceof HasInfoFrame) {
            InfoFrame infoFrame = ((HasInfoFrame) topLevelWindow).getInfoFrame();
            infoFrame.showInfo(window.getId());
        }
    }

    @Override
    protected void closeWindow(Window window, WindowOpenInfo openInfo) {
        super.closeWindow(window, openInfo);

        Window.TopLevelWindow topLevelWindow = getApp().getTopLevelWindow();
        if (topLevelWindow instanceof HasInfoFrame) {
            InfoFrame infoFrame = ((HasInfoFrame) topLevelWindow).getInfoFrame();

            List<Window> openWindows = new ArrayList<>(getOpenWindows());
            if (openWindows.size() >= 2) {
                infoFrame.showInfo(openWindows.get(openWindows.size() - 2).getId());
            } else {
                infoFrame.closeInfo();
            }
        }
    }
}
