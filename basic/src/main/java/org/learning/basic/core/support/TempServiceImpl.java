package org.learning.basic.core.support;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.learning.basic.core.ITempService;
import org.learning.basic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import static org.learning.basic.core.Asserts.Patterns.notNull;
import static org.learning.basic.core.Errors.Patterns.handler;

public class TempServiceImpl implements InitializingBean, DisposableBean, ITempService {

    private static final Logger logger = LoggerFactory.getLogger(TempServiceImpl.class);

    private static final long DELAY = 1000 * 60;

    private TaskScheduler scheduler;
    private File root;
    private ScheduledFuture<?> future;

    @Override
    public void afterPropertiesSet() {

        notNull(scheduler, "scheduler must not be null");
        notNull(root, "temp root must not be null");

        future = scheduler.scheduleWithFixedDelay(() -> {
            long now = System.currentTimeMillis();
            File[] temps = root.listFiles((File pathname) -> {
                if (StringUtils.endsWith(pathname.getName(), SUFFIX)) {
                    String name = StringUtils.substringBeforeLast(pathname.getName(), SUFFIX);
                    if (StringUtils.isEmpty(name)) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("expires " + pathname);
                        }
                        return true;
                    }
                    return Long.valueOf(name) < now;
                }
                return false;
            });
            Arrays.stream(temps).forEach(temp -> {
                if (temp.exists()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("expires " + temp);
                    }
                    try {
                        if (temp.isDirectory()) {
                            FileUtils.deleteDirectory(temp);
                        } else if (!temp.delete() && logger.isWarnEnabled()) {
                            logger.warn("can not delete temp file");
                        }
                    } catch (Exception e) {
                        logger.error("can not delete temp file", e);
                    }
                }
            });
        }, DELAY);
    }

    @Override
    public File create(long delay) {
        long now = System.currentTimeMillis() + delay;
        File temp = new File(root, (now++) + SUFFIX);
        while (temp.exists()) {
            temp = new File(root, (now++) + SUFFIX);
        }
        return temp;
    }

    @Override
    public File create(long delay, byte[] content) {
        File temp = create(delay);
        try {
            FileUtils.writeByteArrayToFile(temp, content);
        } catch (IOException e) {
            return handler(null, e);
        }
        return temp;
    }

    @Override
    public File create(long delay, InputStream is) {
        File temp = create(delay);
        try (OutputStream os = new FileOutputStream(temp)) {
            IOUtils.copy(is, os);
        } catch (IOException e) {
            return handler(null, e);
        }
        return temp;
    }

    @Override
    public File get(String name) {
        return new File(root, name);
    }

    @Override
    public void destroy() {
        if (future != null) {
            future.cancel(false);
        }
    }

    public void setRoot(String root) {
        if (StringUtils.isEmpty(root)) {
            this.root = FileUtils.getTempDirectory();
        } else {
            this.root = new File(root);
        }
    }

    @Override
    public File getRoot() {
        return root;
    }

    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
}
