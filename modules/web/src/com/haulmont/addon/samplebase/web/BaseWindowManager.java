package com.haulmont.addon.samplebase.web;

import com.haulmont.addon.samplebase.BaseConfig;
import com.haulmont.addon.samplebase.web.screens.HasInfoFrame;
import com.haulmont.addon.samplebase.web.screens.HasSideMenu;
import com.haulmont.addon.samplebase.web.screens.InfoFrame;
import com.haulmont.cuba.core.global.AccessDeniedException;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.mainwindow.SideMenu;
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
        if (topLevelWindow instanceof HasSideMenu) {
            SideMenu sideMenu = ((HasSideMenu) topLevelWindow).getSideMenu();
            SideMenu.MenuItem menuItem = sideMenu.getMenuItem(window.getId());
            if (menuItem != null) {
                expandMenuItem(sideMenu.getMenuItems(), menuItem);
                menuItem.setStyleName("highlight");
            }
        }
    }

    private boolean expandMenuItem(List<SideMenu.MenuItem> items, SideMenu.MenuItem selectedItem) {
        for (SideMenu.MenuItem item : items) {
            if (item == selectedItem)
                return true;
            if (expandMenuItem(item.getChildren(), selectedItem)) {
                item.setExpanded(true);
                return true;
            }
        }
        return false;
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

        if (topLevelWindow instanceof HasSideMenu) {
            SideMenu sideMenu = ((HasSideMenu) topLevelWindow).getSideMenu();
            SideMenu.MenuItem menuItem = sideMenu.getMenuItem(window.getId());
            if (menuItem != null) {
                menuItem.removeStyleName("highlight");
            }
        }
    }
}
