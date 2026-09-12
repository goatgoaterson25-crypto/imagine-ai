package com.imagine.ai.data
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
val Context.ds by preferencesDataStore("app")
@Singleton class KeyStore(@ApplicationContext private val c: Context){
    private val K = stringPreferencesKey("key")
    val flow: Flow<String> = c.ds.data.map { it[K]?: "" }
    suspend fun save(k: String){ c.ds.edit { it[K] = k.trim() } }
}
@Module @InstallIn(SingletonComponent::class) object AppModule{
    @Provides @Singleton fun api(): GeminiApi {
        val json = Json{ ignoreUnknownKeys = true }
        return Retrofit.Builder().baseUrl("https://generativelanguage.googleapis.com/")
          .client(OkHttpClient.Builder().build())
          .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
          .build().create(GeminiApi::class.java)
    }
    @Provides @Singleton fun repo(api: GeminiApi, ks: KeyStore) = Repo(api, ks)
}
@Singleton class Repo(private val api: GeminiApi, private val ks: KeyStore){
    suspend fun gen(prompt: String, key: String): Result<Bitmap> = try{
        val res = api.generate(key, GeminiRequest(listOf(Content(listOf(Part(prompt))))))
        val b64 = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull{ it.inlineData!= null }?.inlineData?.data?: return Result.failure(Exception("No image. Try more detailed prompt."))
        val bytes = Base64.decode(b64, Base64.DEFAULT)
        Result.success(BitmapFactory.decodeByteArray(bytes,0,bytes.size))
    }catch(e: Exception){ Result.failure(e) }
}
