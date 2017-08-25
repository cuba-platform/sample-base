package com.haulmont.addon.samplebase;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.Default;
import com.haulmont.cuba.core.config.type.Factory;
import com.haulmont.cuba.core.config.type.StringListTypeFactory;

import java.util.List;

@Source(type = SourceType.APP)
public interface BaseConfig extends Config {

    @Property("base.infoConfig")
    String getInfoConfigPath();

    @Property("base.docRoot")
    String getDocRoot();

    @Property("base.fileRoot")
    String getFileRoot();

    @Property("base.accessToken")
    String getAccessToken();

    @Property("base.permittedScreenPrefix")
    @Default("sample$")
    String getPermittedScreenPrefix();

    @Property("base.permittedScreens")
    @Factory(factory = StringListTypeFactory.class)
    @Default("aboutWindow|jmxConsole|jmxConsoleInspectMbean|jmxConsoleEditAttribute|jmxConsoleOperationResult")
    List<String> getPermittedScreens();

    @Property("base.permittedJmxDomains")
    @Factory(factory = StringListTypeFactory.class)
    List<String> getPermittedJmxDomains();
}
