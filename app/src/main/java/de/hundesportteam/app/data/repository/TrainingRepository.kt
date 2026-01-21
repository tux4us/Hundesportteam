package de.hundesportteam.app.data.repository

import android.util.Log
import de.hundesportteam.app.data.local.dao.TrainingPageDao
import de.hundesportteam.app.data.local.entity.PageEntity
import de.hundesportteam.app.data.local.entity.TrainingPageEntity
import de.hundesportteam.app.data.remote.WordPressApiService
import de.hundesportteam.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TrainingRepository"

@Singleton
class TrainingRepository @Inject constructor(
    private val apiService: WordPressApiService,
    private val trainingPageDao: TrainingPageDao,
    private val pageRepository: PageRepository
) {

    fun getTrainingPages(forceRefresh: Boolean = false): Flow<Result<List<TrainingPageEntity>>> = flow {
        emit(Result.Loading)

        try {
            // First emit cached data if available
            val cachedPages = trainingPageDao.getAllTrainingPages().first()
            if (cachedPages.isNotEmpty() && !forceRefresh) {
                emit(Result.Success(cachedPages))
                return@flow
            }

            // Fetch from API
            try {
                val allPages = apiService.getPages(perPage = 100)

                // Find the training main page (trainingsordnung)
                val trainingMainPage = allPages.find { it.slug == "trainingsordnung" }

                val entities = if (trainingMainPage != null) {
                    // Get all pages including the main page and its children
                    val trainingPages = allPages.filter { page ->
                        page.slug == "trainingsordnung" ||
                                page.parent == trainingMainPage.id ||
                                page.slug.startsWith("trainingsordnung/") ||
                                page.link.contains("/trainingsordnung/")
                    }.map { page ->
                        val imageUrl = page.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
                        TrainingPageEntity(
                            id = page.id,
                            title = cleanHtml(page.title.rendered),
                            content = page.content.rendered,
                            excerpt = cleanHtml(page.excerpt.rendered),
                            link = page.link,
                            slug = page.slug,
                            parent = page.parent,
                            menuOrder = page.menuOrder,
                            imageUrl = imageUrl,
                            cachedAt = System.currentTimeMillis()
                        )
                    }.sortedWith(compareBy(
                        // Trainingsordnung immer zuerst
                        { it.slug != "trainingsordnung" },
                        // Dann nach Nummer im Slug sortieren (trainingsparcour-01, -02, etc.)
                        {
                            val match = Regex("trainingsparcour-(\\d+)").find(it.slug)
                            match?.groupValues?.get(1)?.toIntOrNull() ?: Int.MAX_VALUE
                        },
                        // Falls keine Nummer, alphabetisch nach Titel
                        { it.title }
                    ))
                    trainingPages
                } else {
                    emptyList()
                }

                trainingPageDao.deleteAll()
                if (entities.isNotEmpty()) {
                    trainingPageDao.insertAll(entities)
                }
                emit(Result.Success(entities))
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching training pages from API", e)
                if (cachedPages.isEmpty()) {
                    emit(Result.Error(e.message ?: "Fehler beim Laden der Trainingsseiten"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getTrainingPages", e)
            emit(Result.Error(e.message ?: "Ein Fehler ist aufgetreten"))
        }
    }

    suspend fun getTrainingPage(id: Int): TrainingPageEntity? {
        return trainingPageDao.getTrainingPageById(id)
    }

    suspend fun getTrainingPageById(id: Int): TrainingPageEntity? {
        return trainingPageDao.getTrainingPageById(id)
    }

    private fun cleanHtml(html: String): String {
        return Jsoup.parse(html).text()
    }

    private fun PageEntity.toTrainingPageEntity(): TrainingPageEntity {
        return TrainingPageEntity(
            id = this.id,
            title = this.title,
            content = this.content,
            excerpt = this.excerpt,
            link = this.link,
            slug = this.slug,
            parent = this.parent,
            menuOrder = this.menuOrder,
            imageUrl = this.imageUrl,
            cachedAt = System.currentTimeMillis() // Update cache time
        )
    }
}
