package org.learning.basic.i18n.support;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import org.learning.basic.utils.StringUtils;

public class BasicResourceBundleMessageSource extends ResourceBundleMessageSource {

	private static final String META_INF = "META-INF/";
	private static final String WEB_INF_CLASSES = "/WEB-INF/classes/";
	private static final String SEPARATOR_ = "_";

	public void setBaseresources(Resource... baseresources) throws IOException {
        Set<String> fsrs = new LinkedHashSet<>();
        Set<String> urs = new LinkedHashSet<>();
        for (Resource bs : baseresources) {
            if (bs instanceof FileSystemResource) {
                FileSystemResource fsr = (FileSystemResource) bs;
                String path = StringUtils.cleanPath(fsr.getFile().getAbsolutePath());
                String pathToUse = StringUtils.substringAfterLast(path, WEB_INF_CLASSES);
                if (StringUtils.isEmpty(pathToUse)) {
                    pathToUse = StringUtils.substringAfterLast(path, META_INF);
                    if (StringUtils.isNotEmpty(pathToUse)) {
                    	pathToUse = META_INF + pathToUse;
                    }
                }
                Assert.hasText(pathToUse, fsr.getFile() + " not have '/WEB-INF/classes/' or '/META-INF/'");
                put(fsrs, pathToUse);
            } else if (bs instanceof UrlResource) {
                UrlResource ur = (UrlResource) bs;
                String path = String.valueOf(ur.getURL());
                path = StringUtils.substringAfterLast(path, ResourceUtils.JAR_URL_SEPARATOR);
                Assert.hasText(path, ur + " must be JAR URL");
                put(urs, path);
            } else {
                throw new IllegalArgumentException("not support basename resource " + bs.getClass().getName() + " -> " + bs);
            }
        }
        // 文件的在前面，jar包的在后面
        fsrs.addAll(urs);
        // 设置 basenames
        this.setBasenames(fsrs.stream().toArray(String[]::new));
    }

	private void put(Set<String> paths, String path) {
		path = StringUtils.substringBeforeLast(path, SEPARATOR_);
		if (StringUtils.contains(path, SEPARATOR_)) {
		    path = StringUtils.substringBeforeLast(path, SEPARATOR_);
		}
		paths.add(path);
	}
}
