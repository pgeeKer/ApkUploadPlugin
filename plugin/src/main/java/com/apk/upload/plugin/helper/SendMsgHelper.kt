package com.apk.upload.plugin.helper

import com.apk.upload.plugin.model.*
import com.apk.upload.plugin.params.SendWXGroupParams
import groovy.json.JsonOutput
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.api.Project

/**
 * 发送消息辅助类
 *
 * @author panxiangxing
 */
class SendMsgHelper {

    companion object {
        private const val defaultTitle = "测试包"
        private const val defaultText = "最新开发测试包已上传 "
//        private const val defaultClickText = "点我进行下载"
//        private const val defaultLogTitle = "更新内容：\n "

        /**
         * 发送消息到微信群
         */
        fun sendMsgToWXGroup(
            p0: Project,
            data: PgyUploadResult
        ) {
            val wxMsgParams = SendWXGroupParams.getWXGroupConfig(p0)
            val webHookUrl = wxMsgParams.webHookUrl
            if (webHookUrl.isEmpty()) {
                println("请配置企业微信群的 webHookUrl ~~")
                return
            }
            var contentTitle = wxMsgParams.contentTitle
            if (contentTitle.isEmpty()) {
                contentTitle = defaultTitle
            }
            var contentText = wxMsgParams.contentText
            if (contentText.isEmpty()) {
                contentText = defaultText
            }
            val requestBean = WXGroupRequestBean()
            when (wxMsgParams.msgType) {
                "text" -> {
                    requestBean.msgtype = "text"
                    val textDTO = TextDTO()
                    val contentBuilder = StringBuilder()
                    contentBuilder.append(data.buildName).append(contentTitle)
                        .append("\n").append("下载链接：https://www.pgyer.com/")
                        .append(data.buildShortcutUrl)
                        .append("\n").append(contentText)
                    textDTO.content = contentBuilder.toString()
                    if (wxMsgParams.isAtAll) {
                        val mentionedList = mutableListOf<String>()
                        mentionedList.add("@all")
                        textDTO.mentioned_list = mentionedList
                        textDTO.mentioned_mobile_list = mentionedList
                    }
                    requestBean.text = textDTO
                }
                "news" -> {
                    requestBean.msgtype = "news"
                    val newsDTO = NewsDTO()
                    val articlesDTO = ArticlesDTO()
                    articlesDTO.title =
                        data.buildName + " V" + data.buildVersion + " " + data.buildCreated
                    var desStr = "最新开发测试包已上传,请下载测试吧！ "
                    if (contentTitle.isNotEmpty() && contentText.isNotEmpty()) {
                        desStr = "$contentTitle \n$contentText"
                    } else if (contentTitle.isNotEmpty()) {
                        desStr = contentTitle
                    } else if (contentText.isNotEmpty()) {
                        desStr = contentText
                    }
                    articlesDTO.description = desStr
                    articlesDTO.url = "https://www.pgyer.com/" + data.buildShortcutUrl
                    articlesDTO.picurl = data.buildQRCodeURL
                    val articlesDTOList = mutableListOf(articlesDTO)
                    newsDTO.articles = articlesDTOList
                    requestBean.news = newsDTO
                }
                "markdown" -> {
                    requestBean.msgtype = "markdown"
                    val markdownDTO = MarkdownDTO()
                    val markdownBuilder = StringBuilder()
                    markdownBuilder.append("**").append(data.buildName).append("** V")
                        .append(data.buildVersion)
                        .append("  ").append(data.buildCreated).append(" \n")
                        .append(contentTitle).append(" \n").append(contentText).append(" \n")
                        .append("<font color=\"info\">[下载链接，点击下载](https://www.pgyer.com/")
                        .append(data.buildShortcutUrl).append(")</font>")
                    markdownDTO.content = markdownBuilder.toString()
                    requestBean.markdown = markdownDTO
                }
            }

            val json = JsonOutput.toJson(requestBean)
            println("send to WeiXin group request json：$json")
            val requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json)
            val request = Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8")
                .url(webHookUrl)
                .post(requestBody)
                .build()
            try {
                val response = HttpHelper.getOkHttpClient().newCall(request).execute()
                if (response.isSuccessful && response.body() != null) {
                    println("send to WeiXin group success!!!!!!!")
                } else {
                    println("send to WeiXin group failure")
                }
                println("*************** sendMsgToWeiXinGroup finish ***************")
            } catch (e: Exception) {
                println("send to WeiXin group failure $e")
            }
        }
    }
}