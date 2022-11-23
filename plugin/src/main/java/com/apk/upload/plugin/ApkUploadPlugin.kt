package com.apk.upload.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.apk.upload.plugin.params.SendWXGroupParams
import com.apk.upload.plugin.params.UploadPgyParams
import com.apk.upload.plugin.task.BuildAndUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*


/**
 * apk 上传蒲公英插件，上传完成发消息到企业微信，钉钉，飞书等能获取到 webhook 的IM工具
 *
 * - 先只实现发送消息到企业微信
 *
 * @author panxiangxing
 */
class ApkUploadPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val uploadParams = project.extensions.create(
            PluginConstants.UPLOAD_PARAMS_NAME,
            UploadPgyParams::class.java
        )
        createMessageParams(project)
        project.afterEvaluate { p0 ->
            val appExtension = p0.extensions.findByType(AppExtension::class.java)
            val appVariants = appExtension?.applicationVariants
            appVariants?.forEach { applicationVariant ->
                if (applicationVariant.buildType == null) {
                    return@forEach
                }
                dependsOnTask(applicationVariant, uploadParams, p0)
            }
        }
    }

    private fun dependsOnTask(
        applicationVariant: ApplicationVariant,
        uploadParams: UploadPgyParams,
        p0: Project
    ) {
        var variantName = applicationVariant.name?.substring(0, 1)
            ?.toUpperCase(Locale.ROOT) + applicationVariant.name?.substring(1)
        variantName = when (variantName.isEmpty()) {
            true -> {
                if (uploadParams.buildTypeName.isEmpty()) {
                    "Release"
                } else {
                    uploadParams.buildTypeName
                }
            }
            else -> {
                variantName
            }
        }
        //创建上传到蒲公英的task任务
        val uploadTask = p0.tasks.create(
            PluginConstants.TASK_EXTENSION_NAME + variantName,
            BuildAndUploadTask::class.java
        )
        uploadTask.init(applicationVariant, p0)

        //依赖关系 。上传依赖打包，打包依赖clean。
        applicationVariant.assembleProvider.get().dependsOn(p0.tasks.findByName("clean"))
        uploadTask.dependsOn(applicationVariant.assembleProvider.get())
    }

    private fun createMessageParams(project: Project) {
        project.extensions.create(
            PluginConstants.WX_GROUP_MSG_PARAMS_NAME,
            SendWXGroupParams::class.java
        )
    }
}