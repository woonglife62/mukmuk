package com.example.mukmuk.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class KakaoSearchResponse(
    val documents: List<KakaoPlace>,
    val meta: KakaoMeta
)

@Serializable
data class KakaoMeta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean
)
