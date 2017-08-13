package com.haulmont.addon.samplebase;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;

@Source(type = SourceType.APP)
public interface SampleConfig extends Config {

    @Property("samplebase.infoConfig")
    String getInfoConfigPath();

    @Property("samplebase.docRoot")
    String getDocRoot();

    @Property("samplebase.fileRoot")
    String getFileRoot();
}
