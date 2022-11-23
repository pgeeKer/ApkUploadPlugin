package com.apk.upload.plugin.params;

import org.gradle.api.Project;

/**
 *
 * 发送到企业微信群的消息参数
 *
 * @author panxiangxing
 */
public class SendWXGroupParams {

    public String webHookUrl;
    public String msgType = "markdown";
    /**
     * 如果使用文本可添加参数是否@全体群人员，默认true：isAtAll = true。其他类型不支持
     */
    public boolean isAtAll = true;
    public String contentTitle;
    public String contentText;

    public static SendWXGroupParams getWXGroupConfig(Project project) {
        SendWXGroupParams extension = project.getExtensions().findByType(SendWXGroupParams.class);
        if (extension == null) {
            extension = new SendWXGroupParams();
        }
        return extension;
    }
}