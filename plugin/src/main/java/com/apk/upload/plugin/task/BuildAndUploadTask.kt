package com.apk.upload.plugin.task

import com.apk.upload.plugin.params.UploadPgyParams
import com.google.gson.Gson
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * 编译后上传的任务
 *
 * @author panxiangxing
 */
open class BuildAndUploadTask : BaseTask() {

    @TaskAction
    fun uploadPGY() {
        variant.outputs.forEach { output ->
            val apkDir = output.outputFile
            if (apkDir == null || !apkDir.exists()) {
                throw GradleException("The compiled APK file to upload does not exist!")
            }
            println("ApkDir path: " + apkDir.absolutePath)
            var apk: File? = null
            if (apkDir.name.endsWith(".apk")) {
                apk = apkDir
            } else {
                if (apkDir.listFiles() != null) {
                    val size = apkDir.listFiles()?.size
                    for (i in 0 until size as Int) {
                        val file = apkDir.listFiles()!![i]
                        if (!file?.exists()!! || !file.name.endsWith(".apk")) {
                            continue
                        }
                        apk = file
                    }
                }
            }

            if (apk == null || !apk.exists()) {
                throw GradleException("The compiled APK file to upload does not exist!")
            }
            println("上传的 apk 路径: " + apk.absolutePath)
            val params = UploadPgyParams.getConfig(p0)
            println("上传配置的参数: " + Gson().toJson(params))
            uploadPgyQuickWay(params.apiKey, params.buildInstallType
                , params.buildPassword, params.buildUpdateDescription
                , params.buildInstallDate, params.buildChannelShortcut, apk)
        }
    }
}