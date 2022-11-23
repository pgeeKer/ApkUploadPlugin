package com.apk.upload.plugin.task

import com.android.build.gradle.api.BaseVariant
import com.apk.upload.plugin.PluginConstants
import com.apk.upload.plugin.helper.HttpHelper
import com.apk.upload.plugin.helper.SendMsgHelper
import com.apk.upload.plugin.model.BasePgyResult
import com.apk.upload.plugin.model.PgyCOSTokenResult
import com.apk.upload.plugin.model.PgyUploadResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * 任务基类
 *
 * @author panxiangxing
 */
open class BaseTask : DefaultTask() {

    open lateinit var variant: BaseVariant
    open lateinit var p0: Project

    fun init(variant: BaseVariant, project: Project) {
        this.variant = variant
        this.p0 = project
        description = PluginConstants.TASK_DES
        group = PluginConstants.TASK_GROUP_NAME
    }

    /**
     * 快速上传蒲公英方式
     */
    fun uploadPgyQuickWay(
        apiKey: String,
        installType: Int,
        buildPassword: String,
        buildUpdateDescription: String,
        buildInstallDate: Int,
        buildChannelShortcut: String,
        apk: File
    ) {
        val bodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("_api_key", apiKey)
        bodyBuilder.addFormDataPart("buildType", "android")
        if (buildUpdateDescription.isNotEmpty()) {
            bodyBuilder.addFormDataPart("buildUpdateDescription", buildUpdateDescription)
        }
        if (installType != 1) {
            bodyBuilder.addFormDataPart("buildInstallType", installType.toString())
        }
        if (installType == 2 && buildPassword.isNotEmpty()) {
            bodyBuilder.addFormDataPart("buildPassword", buildPassword)
        }
        bodyBuilder.addFormDataPart("buildInstallDate", buildInstallDate.toString())
        if (buildChannelShortcut.isNotEmpty()) {
            bodyBuilder.addFormDataPart("buildChannelShortcut", buildChannelShortcut)
        }
        println("upload pgy --- 快速上传方式接口：Start getCOSToken")
        val request = getRequestBuilder()
            ?.url("https://www.pgyer.com/apiv2/app/getCOSToken")
            ?.post(bodyBuilder.build())
            ?.build()
            ?: return
        try {
            val response = HttpHelper.getOkHttpClient()
                .newCall(request).execute()
            if (!response.isSuccessful || response.body() == null) {
                println("upload pgy ---- 响应失败了")
                return
            }
            val result = response.body()!!.string()
            if (result.isEmpty()) {
                return
            }
            println("upload pgy ---- getCOSToken result: $result")
            val type = object : TypeToken<BasePgyResult<PgyCOSTokenResult>>() {}.type
            val pgyCOSTokenResult = Gson().fromJson<BasePgyResult<PgyCOSTokenResult>>(result, type)
            if (pgyCOSTokenResult.code != 0) {
                println("upload pgy --- getCOSToken result error msg: " + pgyCOSTokenResult.message)
                return
            }
            uploadFileToPgy(pgyCOSTokenResult, apk, apiKey)
        } catch (e: Exception) {
            println("upload pgy ---- request getCOSToken call failed $e")
        }
    }

    private fun uploadFileToPgy(
        tokenResult: BasePgyResult<PgyCOSTokenResult>?,
        apk: File,
        apiKey: String
    ) {
        println("开始正式上传 apk 文件到蒲公英: " + Gson().toJson(tokenResult))
        val endpoint = tokenResult?.data?.endpoint
        if (endpoint?.isEmpty()!!) {
            println("上传 apk --- endpoint url is empty")
            return
        }
        println("上传 apk --- endpoint url = $endpoint")
        val params = tokenResult.data.params
        val bodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("key", params.key)
        bodyBuilder.addFormDataPart("signature", params.signature)
        bodyBuilder.addFormDataPart("x-cos-security-token", params.token)
        bodyBuilder.addFormDataPart(
            "file", apk.name, RequestBody
                .create(MediaType.parse("*/*"), apk)
        )
        val request = getRequestBuilder()
            ?.url(endpoint)
            ?.post(bodyBuilder.build())
            ?.build()
            ?: return
        try {
            val response = HttpHelper.getOkHttpClient()
                .newCall(request).execute()
            if (!response.isSuccessful || response.body() == null) {
                println("上传 apk ---- 响应失败了")
                return
            }
            val result = response.body()
            println("上传 apk --- endpoint: upload apkFile to pgy result:" + Gson().toJson(result))
            checkPgyUploadBuildInfo(apiKey, tokenResult.data.key)
        } catch (e: Exception) {
            println("上传 apk 失败 ---- call failed $e")
        }
    }

    /**
     * 检测应用是否发布完成，并获取发布应用的信息
     *
     * @param apiKey
     * @param key 发布成功失败返回数据
     *                 code	Integer	错误码，1216 应用发布失败
     *                 message	String	信息提示
     *                 <p>
     *                 正在发布返回数据
     *                 code	Integer	错误码，1246 应用正在发布中
     *                 message	String	信息提示
     *                 如果返回 code = 1246 ，可间隔 3s ~ 5s 重新调用 URL 进行检测，直到返回成功或失败。
     *                 <p>
     *                 buildInfo: upload pgy buildInfo result: {"code":1247,"message":"App is uploded, please wait"}
     */
    private fun checkPgyUploadBuildInfo(apiKey: String, key: String) {
        val urlBuilder = StringBuilder()
        urlBuilder.append("https://www.pgyer.com/apiv2/app/buildInfo?_api_key=")
            .append(apiKey).append("&buildKey=").append(key)
        val request = getRequestBuilder()
            ?.url(urlBuilder.toString())
            ?.get()
            ?.build()
            ?: return
        try {
            val response = HttpHelper.getOkHttpClient()
                .newCall(request).execute()
            if (!response.isSuccessful || response.body() == null) {
                println("检查上传是否完成 ---- 响应失败了")
                return
            }
            val result = response.body()?.string()
            val type = object : TypeToken<BasePgyResult<PgyUploadResult>>() {}.type
            val uploadResult = Gson().fromJson<BasePgyResult<PgyUploadResult>>(result, type)
            when (uploadResult.code) {
                0 -> {
                    val apkDownUrl =
                        "https://www.pgyer.com/" + uploadResult.data.buildShortcutUrl
                    println("上传成功，应用下载链接: $apkDownUrl")
//                    String gitLog = CmdHelper.checkGetGitParamsWithLog(mTargetProject);
//                    SendMsgHelper.sendMsgToDingDing(mTargetProject, uploadResult.getData(), gitLog);
//                    SendMsgHelper.sendMsgToFeishu(mTargetProject, uploadResult.getData(), gitLog);
                    SendMsgHelper.sendMsgToWXGroup(p0, uploadResult.data)
                }
                1246, 1247 -> {
                    pgyUploadBuildInfoTimer(apiKey, key)
                }
            }
            if (uploadResult.code != 0 && uploadResult.code != 1246 && uploadResult.code != 1247) {
                println("upload pgy --- buildInfo: upload pgy buildInfo result error msg: " + Gson().toJson(uploadResult))
            }
        } catch (e: Exception) {
            println("upload pgy --- buildInfo: upload pgy buildInfo result failure $e")
        }
    }

    private fun pgyUploadBuildInfoTimer(apiKey: String, key: String) {
        println("upload pgy --- 检查上传是否完成...")
        try {
            TimeUnit.SECONDS.sleep(3)
            checkPgyUploadBuildInfo(apiKey, key)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun getRequestBuilder(): Request.Builder? {
        return Request.Builder()
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Charset", "UTF-8")
    }
}