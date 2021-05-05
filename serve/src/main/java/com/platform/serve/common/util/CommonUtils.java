package com.platform.serve.common.util;

import com.platform.serve.config.exception.CheckException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 公共方法工具类
 *
 * @author lids
 * @version 1.0
 * @date 2021/2/28 21:57
 */
@Component
public class CommonUtils {

    public static final String FILE_ROOT_PATH = System.getProperty("user.dir") + "\\mailFiles";

    @PostConstruct
    private void createFileRootPath() {
        File file = new File(FILE_ROOT_PATH);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 校验字符串
     *
     * @param data
     * @param msg
     */
    public static void checkStringException(String data, String msg) {
        if (StringUtils.isEmpty(data)) {
            throw new CheckException(msg);
        }
    }

    /**
     * 校验Long数值
     *
     * @param data
     * @param msg
     */
    public static void checkLongException(Long data, String msg) {
        if (data == null) {
            throw new CheckException(msg);
        }
    }


    /**
     * 获取邮件发送时间
     *
     * @param makerDate
     */
    public static Date getMakerDate(Date makerDate) {
        Date date = new Date();
        if (makerDate != null) {
            if (date.before(makerDate)) {
                date = makerDate;
            }
        }
        return date;
    }


    /**
     * 获取当前时间的yyyy-MM-dd格式
     *
     * @return
     */
    public static String getFormatDate() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    /**
     * 得到32位的uuid
     *
     * @return
     */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 雪花ID
     *
     * @return
     */
    public static String getMailContactId() {
        //TODO
        return null;
    }

    /**
     * 文件存储路径
     *
     * @return
     */
    public static String getFileAbsolutePath(String userCode) {
        return new StringBuilder(FILE_ROOT_PATH).append("\\").append(userCode).append("\\").append(getFormatDate()).toString();
    }

    /**
     * 文件重命名
     *
     * @param suffix
     * @return
     */
    public static String getFileNewName(String suffix) {
        return StringUtils.isNotEmpty(suffix) ? getMailContactId() + suffix : getMailContactId();
    }


    /**
     * 生成6位随机数
     *
     * @return
     */
    public static String random6Code() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    public static Boolean isMSBrowser(HttpServletRequest request) {
        String[] iESingnls = {"MSIE","Trident","Edge"};
        String userAgent = request.getHeader("User-Agent");
        for (String iESingnl : iESingnls) {
            if (userAgent.contains(iESingnl)) {
                return true;
            }
        }
        return false;
    }

}
