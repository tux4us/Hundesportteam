package de.hundesportteam.app.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.prof18.rssparser.RssParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.hundesportteam.app.data.local.AppDatabase
import de.hundesportteam.app.data.local.dao.BlogPostDao
import de.hundesportteam.app.data.local.dao.ElementTemplateDao
import de.hundesportteam.app.data.local.dao.PageDao
import de.hundesportteam.app.data.local.dao.ParcourDao
import de.hundesportteam.app.data.local.dao.TrainingPageDao
import de.hundesportteam.app.data.remote.WordPressApiService
import de.hundesportteam.app.data.repository.ElementTemplateRepository
import de.hundesportteam.app.data.repository.PageRepository
import de.hundesportteam.app.data.repository.ParcourRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://hundesportteam.de/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideWordPressApiService(retrofit: Retrofit): WordPressApiService {
        return retrofit.create(WordPressApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRssParser(): RssParser {
        return RssParser()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hundesportteam_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBlogPostDao(database: AppDatabase): BlogPostDao {
        return database.blogPostDao()
    }

    @Provides
    @Singleton
    fun providePageDao(database: AppDatabase): PageDao {
        return database.pageDao()
    }

    @Provides
    @Singleton
    fun provideTrainingPageDao(database: AppDatabase): TrainingPageDao {
        return database.trainingPageDao()
    }

    @Provides
    @Singleton
    fun provideParcourDao(database: AppDatabase): ParcourDao {
        return database.parcourDao()
    }

    @Provides
    @Singleton
    fun provideElementTemplateDao(database: AppDatabase): ElementTemplateDao {
        return database.elementTemplateDao()
    }

    @Provides
    @Singleton
    fun provideParcourRepository(parcourDao: ParcourDao): ParcourRepository {
        return ParcourRepository(parcourDao)
    }

    @Provides
    @Singleton
    fun provideElementTemplateRepository(elementTemplateDao: ElementTemplateDao): ElementTemplateRepository {
        return ElementTemplateRepository(elementTemplateDao)
    }

    @Provides
    @Singleton
    fun providePageRepository(apiService: WordPressApiService, pageDao: PageDao): PageRepository {
        return PageRepository(apiService, pageDao)
    }
}
