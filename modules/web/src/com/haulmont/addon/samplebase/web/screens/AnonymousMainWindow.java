package com.haulmont.addon.samplebase.web.screens;

import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;
import com.haulmont.cuba.gui.components.Window.TopLevelWindow;

import javax.inject.Inject;

public class AnonymousMainWindow extends AppMainWindow implements TopLevelWindow, HasInfoFrame {

    @Inject
    private InfoFrame infoFrame;

    @Override
    public InfoFrame getInfoFrame() {
        return infoFrame;
    }
}