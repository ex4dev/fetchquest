package dev.tswanson.fetchquest.android

import retrofit2.Retrofit
import retrofit2.http.GET
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST

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
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(Json.asConverterFactory(
            MediaType.get("application/json; charset=UTF8")
        ))
        .client(httpClient)
        .baseUrl(BASE_URL)
        .build()

    val retrofitService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }

    interface APIService {
        @GET("/me")
        suspend fun getUserInfo(): String
        @GET("/events")
        suspend fun getEvents(): List<Event>
        @POST("/events")
        suspend fun postEvents(event: Event): Unit
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