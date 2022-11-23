package com.apk.upload.plugin.params;

import org.gradle.api.Project;

/**
 * 蒲公英上传参数设置
 */
public class UploadPgyParams {

    //(必填) 蒲公英API Key
    public String apiKey;
    public String buildTypeName;//非蒲公英参数，用于控制打包release还是debug，默认release
    // 待上传的 apk 路径
    public String uploadApkFilePath;
    //(选填)应用安装方式，值为(1,2,3，默认为1 公开安装)。1：公开安装，2：密码安装，3：邀请安装
    public int buildInstallType = 1;
    //(选填) 设置App安装密码，密码为空时默认公开安装
    public String buildPassword = "";
    //(选填) 版本更新描述，请传空字符串，或不传。
    public String buildUpdateDescription = "";
    //(选填)是否设置安装有效期，值为：1 设置有效时间， 2 长期有效，如果不填写不修改上一次的设置
    public int buildInstallDate = 2;
    //(选填)所需更新的指定渠道的下载短链接，只可指定一个渠道，字符串型，如：abcd
    public String buildChannelShortcut = "";

    public static UploadPgyParams getConfig(Project project) {
        UploadPgyParams extension = project.getExtensions().findByType(UploadPgyParams.class);
        if (extension == null) {
            extension = new UploadPgyParams();
        }
        return extension;
    }

}
