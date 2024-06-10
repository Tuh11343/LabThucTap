package com.example.myapplication

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private var retrofit: Retrofit? = null
        private const val BASE_URL = "https://tmmn.info/"
        private val authInterceptor = AuthInterceptor()

        fun get(): Retrofit {
            if (retrofit == null) {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                val httpClient = OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }

        fun setAuthToken(token: String) {
            authInterceptor.setToken(token)
        }

        fun setBranch(branch: String) {
            authInterceptor.setBranch(branch)
        }

    }
}

class AuthInterceptor : Interceptor {
    @Volatile
    private var token: String? = null
    @Volatile
    private var branch: String? = null

    fun setToken(token: String) {
        this.token = token
    }

    fun setBranch(branch: String) {
        this.branch = branch
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder: Request.Builder = chain.request().newBuilder()

        Log.i("Retrofit","Branch: $branch")
        Log.i("Retrofit","Token: $token")

        // Add the branch header if it is set
        branch?.let {
            requestBuilder.addHeader("branch", it)
        }

        // Add the authorization header if the token is set
        token?.let {
            requestBuilder.addHeader("Authorization", "$it")
        }

        return chain.proceed(requestBuilder.build())
    }
}