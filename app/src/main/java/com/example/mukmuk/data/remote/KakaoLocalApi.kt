package com.example.mukmuk.data.remote

import com.example.mukmuk.data.remote.dto.KakaoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApi {
    @GET("v2/local/search/keyword.json")
    suspend fun searchByKeyword(
        @Header("Authorization") auth: String,
        @Query("query") query: String,
        @Query("x") longitude: String,
        @Query("y") latitude: String,
        @Query("radius") radius: Int = 2000,
        @Query("category_group_code") category: String = "FD6",
        @Query("size") size: Int = 15
    ): KakaoSearchResponse
}
