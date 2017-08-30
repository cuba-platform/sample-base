package com.haulmont.addon.samplebase.web.screens;

import com.haulmont.cuba.gui.components.AbstractMainWindow;
import com.haulmont.cuba.gui.components.mainwindow.SideMenu;

import javax.inject.Inject;
import java.util.Map;

public class SampleMainWindow extends AbstractMainWindow implements HasInfoFrame, HasSideMenu {

    @Inject
    private SideMenu sideMenu;

    @Inject
    private InfoFrame infoFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        sideMenu.requestFocus();
        SideMenu.MenuItem menuItem = sideMenu.getMenuItem("initial-menu");
        if (menuItem != null) {
            menuItem.setExpanded(true);
        }
    }

    @Override
    public InfoFrame getInfoFrame() {
        return infoFrame;
    }

    @Override
    public SideMenu getSideMenu() {
        return sideMenu;
    }
}