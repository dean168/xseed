package org.learning.basic.core.support;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.context.support.ServletContextResource;

public class CustomPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	@Override
	public void setLocations(Resource... locations) {
		Set<Resource> urls = new LinkedHashSet<>();
		Set<Resource> fsrls = new LinkedHashSet<>();
		Set<Resource> scrls = new LinkedHashSet<>();
		for (Resource location : locations) {
			if (location instanceof ServletContextResource) {
				scrls.add(location);
			} else if (location instanceof FileSystemResource) {
				fsrls.add(location);
			} else if (location instanceof UrlResource) {
				urls.add(location);
			} else {
				throw new IllegalArgumentException("not support location " + location.getClass().getName() + " -> " + location);
			}
		}
		Set<Resource> locationsToUse = new LinkedHashSet<>();
		// jar 包的最前面
		locationsToUse.addAll(urls);
		// 文件的覆盖 jar 包的
		locationsToUse.addAll(fsrls);
		// servlet 的覆盖文件的
		locationsToUse.addAll(scrls);
		// 设置新的顺序 locations
		super.setLocations(locationsToUse.stream().toArray(Resource[]::new));
	}
}
