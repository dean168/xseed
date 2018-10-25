package org.learning.basic.utils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.learning.basic.core.BasicException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class FreemarkerUtils {

    private static final Configuration CFG = new Configuration(Configuration.VERSION_2_3_23);
    private static Object statics;
    private static Object enums;

    static {
        CFG.setClassLoaderForTemplateLoading(FreemarkerUtils.class.getClassLoader(), "/");
        BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_23).build();
        statics = wrapper.getStaticModels();
        enums = wrapper.getEnumModels();
    }

    public static String render(Class<?> clazz, String name, Object root) {
        StringWriter sw = new StringWriter();
        render(clazz, name, root, sw);
        return sw.toString();
    }

    public static void render(Class<?> clazz, String name, Object root, OutputStream os) {
        try {
            render(clazz, name, root, new OutputStreamWriter(os, ByteUtils.CHARSET_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new BasicException(null, null, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void render(Class<?> clazz, String name, Object root, Writer os) {
        try {
            String nameToUse = clazz != null ? StringUtils.replace(clazz.getPackage().getName(), ".", "/") + "/" + name : name;
            Template template = CFG.getTemplate(nameToUse, ByteUtils.CHARSET_NAME);
            Map<String, Object> rootToUse;
            if (root == null || !(Map.class.isAssignableFrom(root.getClass()))) {
                rootToUse = new HashMap<>();
                if (root != null) {
                    rootToUse.put("model", root);
                }
            } else {
                rootToUse = (Map<String, Object>) root;
            }
            rootToUse.put("statics", statics);
            rootToUse.put("enums", enums);
            template.process(rootToUse, os);
        } catch (IOException | TemplateException e) {
            throw new BasicException(null, null, e);
        }
    }
}
