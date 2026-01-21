package de.hundesportteam.app.data.repository

import android.util.Log
import com.prof18.rssparser.RssParser
import de.hundesportteam.app.data.local.dao.BlogPostDao
import de.hundesportteam.app.data.local.entity.BlogPostEntity
import de.hundesportteam.app.data.remote.WordPressApiService
import de.hundesportteam.app.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first // <-- Import this
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlogRepository @Inject constructor(
    private val apiService: WordPressApiService,
    private val blogPostDao: BlogPostDao,
    private val rssParser: RssParser
) {
    private val TAG = "BlogRepository"

    fun getBlogPosts(forceRefresh: Boolean = false): Flow<Result<List<BlogPostEntity>>> = flow {
        emit(Result.Loading)

        // --- START OF FIX ---

        // Step 1: Get the current cached posts ONCE. Do not collect the flow.
        val cachedPosts = blogPostDao.getAllPosts().first()

        // Step 2: Emit cached data if it exists.
        if (cachedPosts.isNotEmpty()) {
            emit(Result.Success(cachedPosts))
        }

        // Step 3: If we don't need to refresh, we can stop here.
        if (cachedPosts.isNotEmpty() && !forceRefresh) {
            return@flow
        }

        // Step 4: Fetch from API.
        try {
            val posts = apiService.getPosts(perPage = 100)
            val entities = posts.map { post ->
                val imageUrl = post.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
                BlogPostEntity(
                    id = post.id,
                    title = cleanHtml(post.title.rendered),
                    content = post.content.rendered,
                    excerpt = cleanHtml(post.excerpt.rendered),
                    date = post.date,
                    link = post.link,
                    imageUrl = imageUrl,
                    cachedAt = System.currentTimeMillis()
                )
            }

            // Step 5: Update the database and emit the new data.
            blogPostDao.deleteAll()
            blogPostDao.insertAll(entities)
            // The ViewModel will get this new list because it's re-collecting the entire flow.
            emit(Result.Success(entities))

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching blog posts from API", e)
            // Only emit an error if we didn't have any cached data to show.
            if (cachedPosts.isEmpty()) {
                emit(Result.Error(e.message ?: "Fehler beim Laden der Blog-Beiträge"))
            }
        }
        // --- END OF FIX ---
    }

    suspend fun getBlogPostById(id: Int): BlogPostEntity? {
        // This function looks fine, but adding logging for clarity.
        return try {
            blogPostDao.getPostById(id) ?: run {
                val post = apiService.getPost(id)
                val imageUrl = post.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
                val entity = BlogPostEntity(
                    id = post.id,
                    title = cleanHtml(post.title.rendered),
                    content = post.content.rendered,
                    excerpt = cleanHtml(post.excerpt.rendered),
                    date = post.date,
                    link = post.link,
                    imageUrl = imageUrl,
                    cachedAt = System.currentTimeMillis()
                )
                blogPostDao.insertAll(listOf(entity))
                entity
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching blog post by id: $id", e)
            null
        }
    }

    private fun cleanHtml(html: String): String {
        return Jsoup.parse(html).text()
    }
}

