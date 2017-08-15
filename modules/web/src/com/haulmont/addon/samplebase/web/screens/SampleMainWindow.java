package com.haulmont.addon.samplebase.web.screens;

import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;

public class SampleMainWindow extends AppMainWindow implements HasInfoFrame {

    @Inject
    private InfoFrame infoFrame;

    @Override
    public InfoFrame getInfoFrame() {
        return infoFrame;
    }
}