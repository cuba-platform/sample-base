package com.haulmont.addon.samplebase.web.screens;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Label;
import org.apache.commons.io.IOUtils;
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

    private Pattern MD_LINK_PATTERN = Pattern.compile("\\[(.+?)\\]\\((\\S+?)\\)");

    @Inject
    private Metadata metadata;

    @Inject
    private Label infoLab;

    @Override
    public void init(Map<String, Object> params) {
        List<String> rootPackages = metadata.getRootPackages();
        if (rootPackages.isEmpty()) {
            log.debug("Empty rootPackages");
        }
        String rootPackage = rootPackages.get(rootPackages.size() - 1);
        String resourcePath = "/" + rootPackage.replace('.', '/') + "/README.md";
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            log.debug("Resource {} not found", resourcePath);
        }

        StringBuilder sb = new StringBuilder();
        try {
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                    bufferedReader.lines().forEach(line -> toHtml(line, sb));
                }
            } catch (IOException e) {
                log.error("Error reading resource {}", resourcePath, e);
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
}