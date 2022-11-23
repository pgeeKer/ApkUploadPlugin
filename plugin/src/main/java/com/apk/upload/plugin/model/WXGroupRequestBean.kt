package com.apk.upload.plugin.model

import com.google.gson.annotations.SerializedName

/**
 * 企业微信发送消息请求实体类
 *
 * @author panxiangxing
 */
data class WXGroupRequestBean(
    var msgtype: String = "markdown",
    var text: TextDTO? = null,
    var markdown: MarkdownDTO ? = null,
    var news: NewsDTO? = null
)

data class TextDTO(
    var content: String = "",
    var mentioned_list: List<String> = mutableListOf(),
    var mentioned_mobile_list: List<String> = mutableListOf()
)

data class MarkdownDTO(
    var content: String = ""
)

data class NewsDTO(
    var articles: List<ArticlesDTO> = mutableListOf()
)

data class ArticlesDTO(
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var picurl: String =""
)