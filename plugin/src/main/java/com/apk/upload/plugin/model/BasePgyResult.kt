package com.apk.upload.plugin.model

/**
 * @author panxiangxing
 */
data class BasePgyResult<DATA>(
    val code: Int,
    @JvmField val message: String,
    val data: DATA
)