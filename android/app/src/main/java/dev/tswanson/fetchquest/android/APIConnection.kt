package dev.tswanson.fetchquest.android

import retrofit2.Retrofit
import retrofit2.http.GET
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.http.POST
import retrofit2.converter.scalars.ScalarsConverterFactory

class APIConnection(private val token: String) {
    companion object {
        private const val BASE_URL =
            "http://10.0.2.2:8080"
        lateinit var instance: APIConnection
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
            chain.proceed(request)
        }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }

    interface APIService {
        @GET("/me")
        fun getUserInfo()
        @GET("/events")
        fun getEvents()
        @POST("/events")
        fun postEvents(event: Event)
    }
}

@Serializable
data class User(
    val name: String,
    val googleUserId: String,
    val email: String,
    val picture: String,
)

@Serializable
data class Event(
    val lat: Float,
    val long: Float,
    val title: String,
    val description: String,
    val hours: String,
    val createdBy: Long
)