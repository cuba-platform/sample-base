package com.haulmont.addon.samplebase.web;

import com.haulmont.addon.samplebase.web.screens.HasInfoFrame;
import com.haulmont.addon.samplebase.web.screens.InfoFrame;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.web.WebWindowManager;

import java.util.ArrayList;
import java.util.List;

public class BaseWindowManager extends WebWindowManager {

    @Override
    protected void afterShowWindow(Window window) {
        super.afterShowWindow(window);

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
