package com.example.currencyconverter.api

import com.example.currencyconverter.api.models.Currency
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeRatesApi {
    @GET("currencies.json")
    fun getCurrencies(@Query("app_id") appId: String): Call<Map<String, String>>

    @GET("latest.json")
    fun getLatestRates(@Query("app_id") appId: String): Call<Currency>
}