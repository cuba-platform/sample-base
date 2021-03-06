package com.haulmont.addon.samplebase.web.screens;

import com.google.common.base.Strings;
import com.haulmont.addon.samplebase.BaseConfig;
import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.FlowBoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.Link;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.io.IOUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoFrame extends AbstractFrame {

    private Logger log = LoggerFactory.getLogger(InfoFrame.class);

    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

    private static final Pattern MD_LINK_PATTERN = Pattern.compile("\\[(.+?)\\]\\((\\S+?)\\)");

    @Inject
    private Metadata metadata;

    @Inject
    private Resources resources;

    @Inject
    private WindowConfig windowConfig;

    @Inject
    private Label infoLab;

    @Inject
    private FlowBoxLayout linksBox;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private BaseConfig config;

    private Element infoConfigRootEl;

    @Override
    public void init(Map<String, Object> params) {
        loadReadme();
        loadInfoConfig();
        showDefaultLinks();
    }

    private void loadReadme() {
        List<String> rootPackages = metadata.getRootPackages();
        if (rootPackages.isEmpty()) {
            log.debug("Empty rootPackages");
            return;
        }
        String rootPackage = rootPackages.get(rootPackages.size() - 1);
        String resourcePath = "/" + rootPackage.replace('.', '/') + "/README.md";
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            log.debug("Resource {} not found", resourcePath);
            return;
        }

        StringBuilder sb = new StringBuilder();
        try {
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                    bufferedReader.lines().forEach(line -> toHtml(line, sb));
                }
            } catch (IOException e) {
                log.error("Error reading resource {}", resourcePath, e);
                return;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        infoLab.setHtmlEnabled(true);
        infoLab.setValue(sb.toString());
    }

    private void toHtml(String line, StringBuilder sb) {
        String str = line.trim();
        if (str.equals(""))
            return;

        int h = 0;
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i) == '#')
                h++;
        }

        if (h > 0) {
            sb.append("<h").append(h).append(">");
        } else {
            sb.append("<p>");
        }

        sb.append(replaceLinks(str.substring(h)));

        if (h > 0) {
            sb.append("</h").append(h).append(">");
        } else {
            sb.append("</p>");
        }
    }

    private String replaceLinks(String markdown) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = MD_LINK_PATTERN.matcher(markdown);
        while (matcher.find()) {
            matcher.appendReplacement(result, "<a href='" + matcher.group(2) + "' target='_blank'>" + matcher.group(1) + "</a>");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private void loadInfoConfig() {
        String infoConfigPath = config.getInfoConfigPath();
        if (Strings.isNullOrEmpty(infoConfigPath)) {
            log.warn("samplebase.infoConfig app property is not defined");
            return;
        }
        String infoConfigXml = resources.getResourceAsString(infoConfigPath);
        if (infoConfigXml == null) {
            log.warn("Info config not found at " + infoConfigPath);
            return;
        }
        infoConfigRootEl = Dom4j.readDocument(infoConfigXml).getRootElement();
    }

    public void showInfo(String windowId) {
        log.debug("Opening window with id = " + windowId);

        clearLinks();

        if (infoConfigRootEl == null)
            return;

        for (Element screenEl : Dom4j.elements(infoConfigRootEl, "screen")) {
            if (screenEl.attributeValue("id").equals(windowId)) {
                WindowInfo windowInfo = windowConfig.getWindowInfo(windowId);
                String templatePath = windowInfo.getDescriptor().attributeValue("template");

                for (Element el : Dom4j.elements(screenEl)) {
                    if (el.getName().equals("doc")) {
                        showDoc(el.attributeValue("caption"), el.attributeValue("page"), el.attributeValue("style"));

                    } else if (el.getName().equals("link")) {
                        showLink(el.attributeValue("caption"), el.attributeValue("url"), el.attributeValue("style"));

                    } else if (el.getName().equals("descriptor")) {
                        if (!Strings.isNullOrEmpty(templatePath)) {
                            showFile("modules/web/src/" + templatePath, el.attributeValue("style"));
                        } else {
                            log.warn("Descriptor is not defined for " + windowId);
                        }

                    } else if (el.getName().equals("controller")) {
                        if (!Strings.isNullOrEmpty(templatePath)) {
                            String templateXml = resources.getResourceAsString(templatePath);
                            if (!Strings.isNullOrEmpty(templateXml)) {
                                Element descrRootEl = Dom4j.readDocument(templateXml).getRootElement();
                                String className = descrRootEl.attributeValue("class");
                                if (!Strings.isNullOrEmpty(className)) {
                                    String path = className.replace('.', '/') + ".java";
                                    showFile("modules/web/src/" + path, el.attributeValue("style"));
                                } else {
                                    log.warn("'class' attribute not found in " + templatePath);
                                }
                            } else {
                                log.warn("Descriptor not found at " + templatePath);
                            }

                        } else {
                            log.warn("Descriptor is not defined for " + windowId);
                        }

                    } else if (el.getName().equals("file")) {
                        showFile(el.attributeValue("path"), el.attributeValue("style"));
                    }
                }
            }
        }
    }

    private void showDoc(String caption, String page, String style) {
        showLink(caption, config.getDocRoot() + page, style);
    }

    private void showFile(String path, String style) {
        showLink(path.substring(path.lastIndexOf('/') + 1), config.getFileRoot() + path, style);
    }

    private void showLink(String caption, String url, String style) {
        Link link = componentsFactory.createComponent(Link.class);
        link.setCaption(substituteVariables(caption));
        link.setUrl(substituteVariables(url));
        link.setTarget("_blank");
        if (!Strings.isNullOrEmpty(style))
            link.setStyleName(style);
        linksBox.add(link);
    }

    private String substituteVariables(String value) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = VAR_PATTERN.matcher(value);
        while (matcher.find()) {
            String name = matcher.group(1);
            String property = AppContext.getProperty(name);
            if (property != null) {
                matcher.appendReplacement(sb, property);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public void closeInfo() {
        clearLinks();
    }

    private void clearLinks() {
        linksBox.removeAll();
        showDefaultLinks();
    }

    private void showDefaultLinks() {
        if (infoConfigRootEl == null)
            return;
        Element defEl = infoConfigRootEl.element("default");
        if (defEl != null) {
            for (Element el : Dom4j.elements(defEl)) {
                if (el.getName().equals("doc")) {
                    showDoc(el.attributeValue("caption"), el.attributeValue("page"), el.attributeValue("style"));

                } else if (el.getName().equals("link")) {
                    showLink(el.attributeValue("caption"), el.attributeValue("url"), el.attributeValue("style"));

                } else if (el.getName().equals("file")) {
                    showFile(el.attributeValue("path"), el.attributeValue("style"));
                }
            }
        }
    }
}