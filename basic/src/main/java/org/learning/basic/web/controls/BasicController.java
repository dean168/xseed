package org.learning.basic.web.controls;

import org.apache.commons.io.FileUtils;
import org.learning.basic.core.ITempService;
import org.learning.basic.core.SessionContext;
import org.learning.basic.i18n.utils.I18nUtils;
import org.learning.basic.utils.ByteUtils;
import org.learning.basic.utils.FreemarkerUtils;
import org.learning.basic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

import static org.learning.basic.utils.JsonUtils.MediaStatus;
import static org.learning.basic.utils.JsonUtils.Status;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

//@RestController
//@RequestMapping("basic")
public class BasicController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier(ITempService.SERVICE_ID)
    protected ITempService tempService;
//	@Value("${basic.media.root}")
//	protected String mediaRoot;

//    @RequestMapping(method = POST, value = "media", produces = {APPLICATION_JSON_UTF8_VALUE})
    public MediaStatus media(@RequestParam(value = "media") MultipartFile media) throws IOException {

        File parent = getTempDirectory();
        File temp = createTempFile(parent);
        if (logger.isDebugEnabled()) {
            logger.debug("upload media#" + media.getOriginalFilename() + " content-type#" + media.getContentType() + " temp#" + temp);
        }

        try (InputStream is = media.getInputStream()) {
            FileUtils.copyToFile(is, temp);
        }

        MediaStatus status = new MediaStatus();

        status.setContentType(media.getContentType());
        status.setContentLength(media.getSize());
        status.setName(media.getName());
        status.setFilename(media.getOriginalFilename());
        if (valid(media.getContentType(), temp)) {
            status.setTemp(temp.getName());
            status.setCode(Status.TRUE);
            status.setMessage(I18nUtils.message("BASIC.UPLOADMEDIA.SUCCESS"));
        } else {
            status.setCode(Status.FALSE);
            status.setMessage(I18nUtils.message("BASIC.UPLOADMEDIA.FAIL"));
        }

        return status;
    }

    protected boolean valid(String contentType, File temp) {
        if (StringUtils.startsWith(contentType, "image/")) {
            return isImage(temp);
        } else if (StringUtils.startsWith(contentType, "application/zip")) {
            return isZip(temp);
        }
        return true;
    }

    protected boolean isImage(File temp) {
        // 先验证文件头字节
        try (InputStream is = new FileInputStream(temp)) {
            int b1 = is.read() & 0xff;
            int b2 = is.read() & 0xff;
            // 判断是否是图片文件头字节
            if (b1 == 0x89 && b2 == 0x50 || b1 == 0xff && b2 == 0xd8) {
                // 获取图片的高和宽，有高和宽的才是图片
                Image img = ImageIO.read(temp);
                return !(img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0);
            } else {
                return false;
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(null, e);
            }
            return false;
        }
    }

    protected boolean isZip(File temp) {
        try (InputStream is = new FileInputStream(temp)) {
            int b1 = is.read() & 0xff;
            int b2 = is.read() & 0xff;
            return b1 == 0x50 && b2 == 0x4b;
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(null, e);
            }
            return false;
        }
    }

//    @RequestMapping(method = GET, value = "media")
    public void media(@RequestParam(value = "id") String id,
                      @RequestParam(value = "contentType", required = false) String contentType,
                      @RequestParam(value = "attachmentName", required = false) String attachmentName,
                      HttpServletResponse response) throws IOException {

        File media = null;

        if (StringUtils.contains(id, ITempService.SUFFIX)) {
            String name = StringUtils.substringBefore(id, ITempService.SUFFIX) + ITempService.SUFFIX;
            if (StringUtils.startsWith(name, "/")) {
                name = StringUtils.substringAfterLast(name, "/");
            }
            media = tempService.get(name);
            String subs = StringUtils.substringAfter(id, ITempService.SUFFIX);
            if (StringUtils.isNotEmpty(subs)) {
                media = new File(media.getCanonicalPath() + subs);
            }
        } else {
            media = new File(mediaRoot() + "/" + mediaPath(id));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("get media#" + id + " -> " + media);
        }

        if (StringUtils.isNotEmpty(contentType)) {
            response.setContentType(contentType);
        }

        if (StringUtils.isNotEmpty(attachmentName)) {
            String attachmentNameToUse = URLEncoder.encode(attachmentName, ByteUtils.CHARSET_NAME);
            attachmentNameToUse = StringUtils.replace(attachmentNameToUse, "+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + attachmentNameToUse);
        }

        if (media.exists()) {
            FileUtils.copyFile(media, response.getOutputStream());
        } else if (logger.isWarnEnabled()) {
            logger.warn(media + " is not exist");
        }
    }

    /**
     * 子类继承，根据 media 的 id 返回对应的路径
     *
     * @param id
     * @return
     */
    protected String mediaPath(String id) {
//      throw new UnsupportedOperationException(getClass().getName() + " must implements method: mediaPath(id)");
        return id;
    }

    /**
     * 子类继承，获取 media 的根目录
     *
     * @return
     */
    protected String mediaRoot() {
        throw new UnsupportedOperationException(getClass().getName() + " must implements method: mediaRoot()");
    }

    protected File getTempDirectory() {
        return tempService.getRoot();
    }

    protected File createTempFile(File parent) {
        // 默认一天后删除临时创建的文件
        return tempService.create(1000 * 60 * 60 * 24);
    }

    protected File getTempFile(String temp) {
        return tempService.get(temp);
    }

    protected String replaceEvalPath(String path, Map<String, String> args) {
        return StringUtils.replace(path, "{", "}", (String key, StringBuffer src, int prefixIndex, int suffixIndex) -> {
            String value = args.get(key);
            return StringUtils.isEmpty(value) ? "{" + key + "}" : value;
        });
    }

    protected void render(String name, Object root) throws IOException {
        render(null, name, root, APPLICATION_JSON_UTF8_VALUE);
    }

    protected void render(Class<?> clazz, String name, Object root) throws IOException {
        render(clazz, name, root, APPLICATION_JSON_UTF8_VALUE);
    }

    protected void render(Class<?> clazz, String name, Object root, String contentType) throws IOException {
        SessionContext context = SessionContext.get();
        HttpServletResponse response = context.response();
        response.setContentType(contentType);
        try (OutputStream os = response.getOutputStream()) {
            FreemarkerUtils.render(clazz, name, root, os);
        }
    }
}
