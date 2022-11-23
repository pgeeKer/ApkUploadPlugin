package com.apk.upload.plugin.model

import com.google.gson.annotations.SerializedName

/**
 * @author panxiangxing
 */
data class PgyCOSTokenResult(
    val key: String,
    val endpoint: String,
    val params: ParamsDTO
)

data class ParamsDTO(
    val signature: String,
    @SerializedName("x-cos-security-token") val token: String,
    val key: String
)