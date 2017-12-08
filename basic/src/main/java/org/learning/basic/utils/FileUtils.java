package org.learning.basic.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils extends org.apache.commons.io.FileUtils {

	public static void copy(File dest, InputStream is) throws IOException {
		File temp = new File(dest.getParent(), dest.getName() + ".temp");
		if (temp.exists()) {
			temp.delete();
		}
		FileUtils.copyInputStreamToFile(is, temp);
		if (dest.exists()) {
			dest.delete();
		}
		temp.renameTo(dest);
	}
}
