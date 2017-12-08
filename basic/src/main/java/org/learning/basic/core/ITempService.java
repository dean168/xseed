package org.learning.basic.core;

import java.io.File;
import java.io.InputStream;

public interface ITempService {

    String SERVICE_ID = "basic.tempService";

    String SUFFIX = ".expires";

    File create(long delay);

    File create(long delay, byte[] content);

    File create(long delay, InputStream is);

    File get(String name);

    File getRoot();
}
