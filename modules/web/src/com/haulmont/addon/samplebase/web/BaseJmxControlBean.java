package com.haulmont.addon.samplebase.web;

import com.haulmont.addon.samplebase.BaseConfig;
import com.haulmont.cuba.core.entity.JmxInstance;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.web.jmx.JmxControlBean;
import com.haulmont.cuba.web.jmx.entity.ManagedBeanDomain;
import com.haulmont.cuba.web.jmx.entity.ManagedBeanInfo;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.haulmont.addon.samplebase.web.BaseWindowManager.ADMIN_ACCESS_ATTR;

public class BaseJmxControlBean extends JmxControlBean {

    @Inject
    private BaseConfig config;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public List<ManagedBeanInfo> getManagedBeans(JmxInstance instance) {
        List<ManagedBeanInfo> managedBeans = super.getManagedBeans(instance);

        Boolean adminAccess = userSessionSource.getUserSession().getAttribute(ADMIN_ACCESS_ATTR);
        if (Boolean.TRUE.equals(adminAccess))
            return managedBeans;
        else
            return managedBeans.stream()
                    .filter(managedBeanInfo -> config.getPermittedJmxDomains().contains(managedBeanInfo.getDomain()))
                    .collect(Collectors.toList());
    }

    @Override
    public List<ManagedBeanDomain> getDomains(JmxInstance instance) {
        List<ManagedBeanDomain> domains = super.getDomains(instance);

        Boolean adminAccess = userSessionSource.getUserSession().getAttribute(ADMIN_ACCESS_ATTR);
        if (Boolean.TRUE.equals(adminAccess))
            return domains;
        else
            return domains.stream()
                    .filter(managedBeanDomain -> config.getPermittedJmxDomains().contains(managedBeanDomain.getName()))
                    .collect(Collectors.toList());
    }
}
