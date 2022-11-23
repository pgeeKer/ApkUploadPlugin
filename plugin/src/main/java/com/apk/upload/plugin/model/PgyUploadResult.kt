package com.apk.upload.plugin.model

/**
 *  * 蒲公英上传成功后的数据model
 * <p>
 * buildKey	String	Build Key是唯一标识应用的索引ID
 * buildType	Integer	应用类型（1:iOS; 2:Android）
 * buildIsFirst	Integer	是否是第一个App（1:是; 2:否）
 * buildIsLastest	Integer	是否是最新版（1:是; 2:否）
 * buildFileSize	Integer	App 文件大小
 * buildName	String	应用名称
 * buildVersion	String	版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
 * buildVersionNo	String	上传包的版本编号，默认为1 (即编译的版本号，一般来说，编译一次会变动一次这个版本号, 在 Android 上叫 Version Code。对于 iOS 来说，是字符串类型；对于 Android 来说是一个整数。例如：1001，28等。)
 * buildBuildVersion	Integer	蒲公英生成的用于区分历史版本的build号
 * buildIdentifier	String	应用程序包名，iOS为BundleId，Android为包名
 * buildIcon	String	应用的Icon图标key，访问地址为 https://www.pgyer.com/image/view/app_icons/[应用的Icon图标key]
 * buildDescription	String	应用介绍
 * buildUpdateDescription	String	应用更新说明
 * buildScreenShots	String	应用截图的key，获取地址为 https://www.pgyer.com/image/view/app_screenshots/[应用截图的key]
 * buildShortcutUrl	String	应用短链接
 * buildQRCodeURL	String	应用二维码地址
 * buildCreated	String	应用上传时间
 * buildUpdated	String	应用更新时间
 *
 * @author panxiangxing
 */
data class PgyUploadResult(
    val buildKey: String,
    val buildType: String,
    val buildIsFirst: String,
    val buildIsLastest: String,
    val buildFileKey: String,
    val buildFileName: String,
    val buildFileSize: String,
    val buildName: String,
    val buildVersion: String,
    val buildVersionNo: String,
    val buildBuildVersion: String,
    val buildIdentifier: String,
    val buildIcon: String,
    val buildDescription: String,
    val buildUpdateDescription: String,
    val buildShortcutUrl: String,
    val buildCreated: String,
    val buildUpdated: String,
    val buildQRCodeURL: String
)