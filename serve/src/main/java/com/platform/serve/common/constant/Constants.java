package com.platform.serve.common.constant;

/**
 * 代码使用的常量类
 *
 * @author lids
 * @version 1.0
 * @date 2021/1/3 18:37
 */
public class Constants {

    /** 数据状态：0-无效，1-有效，2-草稿 */
    public static final String DATA_STATUE_OK = "1";
    public static final String DATA_STATUE_NO = "0";
    public static final String DATA_STATUE_LOSE = "2";
    public static final String DATA_STATUE_DRAFT = "2";

    /* 激活状态：0-未激活，1-激活 */
    public static final String UNACTIVAT_STATUE = "0";
    public static final String ACTIVAT_STATUE = "1";

    /** 来源邮件类型：0-发件箱，1-收件箱 */
    public static final String MAIL_TYPE_STORE = "1";
    public static final String MAIL_TYPE_TRANSPORT = "2";

    /** 大小默认值 */
    public static final String MAIL_CODE_STORE = "POP3";
    public static final String MAIL_CODE_TRANSPORT = "imap";

    /** 邮件大小（KB）默认值 */
    public static final Long SIZE_DEFAULT = 0L;

    /** 来源邮件类型：1-收件箱，2-发件箱 */
    public static final String EMAIL_TYPE_IN = "1";
    public static final String EMAIL_TYPE_OUT = "2";

    /** 是否存在附件：0-无，1-有 */
    public static final String CONTAIN_FILE_YES = "1";
    public static final String CONTAIN_FILE_NO = "0";

    /** 1(High):紧急  3:普通(Normal)  5:低(Low) */
    public static final Integer PRIORITY_HIGH = 1;
    public static final Integer PRIORITY_NORMAL = 3;
    public static final Integer PRIORITY_LOW = 5;

    /** 是否已读：0-已读，1-未读 */
    public static final String IS_SEEN_YES = "0";
    public static final String IS_SEEN_NO = "1";

    /** 是否需要回执：0-不回执，1-需要回执 */
    public static final String IS_REPLY_SIGN_YES = "1";
    public static final String IS_REPLY_SIGN_NO = "0";

    /** 解析地址头拼接信息 */
    public static final String ADDRESS_HEAD = "<";
    public static final String ADDRESS_TRAIL = ">";

    /** 是否有密保问题：0-无，1-有 */
    public static final String SECURITY_QUESTION_YES = "1";
    public static final String SECURITY_QUESTION_NO = "0";

    /** 会员等级：0-无，1-会员1级，2-会员2级 */
    public static final String UNMEMBER = "0";
    public static final String MEMBER_ONE = "1";

    /* 发送状态：0-失败，1-成功 */
    public static final String SEND_STATUE_NO = "0";
    public static final String SEND_STATUE_OK = "1";

    public static final Integer VERIFY_LONGIN_MIN = 60;

}
