package com.example.mtls_final

import android.content.Context
import nl.altindag.ssl.SSLFactory
import okhttp3.OkHttpClient
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509ExtendedKeyManager
import javax.net.ssl.X509ExtendedTrustManager
import nl.altindag.ssl.pem.util.PemUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

object Retrofit {

    private const val BASE_URL = "https://domain.test/"

    private fun getMtls(context: Context): OkHttpClient{

        val client_cert: InputStream = context.resources.openRawResource(R.raw.client_cert)
        val client_key: InputStream = context.resources.openRawResource(R.raw.client)
        val server_cert: InputStream = context.resources.openRawResource(R.raw.server_cert)

        val keyManager: X509ExtendedKeyManager = PemUtils.loadIdentityMaterial(client_cert, client_key)

        val server_trustManager: X509ExtendedTrustManager = PemUtils.loadTrustMaterial(server_cert)

        val sslFactory: SSLFactory = SSLFactory.builder()
            .withIdentityMaterial(keyManager)
            .withTrustMaterial(server_trustManager)
            .build()

        val sslSocketFactory: SSLSocketFactory = sslFactory.getSslSocketFactory()
        val trustManager: X509ExtendedTrustManager = sslFactory.trustManager.orElseThrow()

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .build()
    }

    fun provideRetrofit(context: Context): Retrofit {

        val okHttpClient = getMtls(context)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}