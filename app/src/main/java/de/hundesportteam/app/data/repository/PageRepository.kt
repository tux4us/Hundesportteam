package de.hundesportteam.app.data.repository

import de.hundesportteam.app.data.local.dao.PageDao
import de.hundesportteam.app.data.local.entity.PageEntity
import de.hundesportteam.app.data.remote.WordPressApiService
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PageRepository @Inject constructor(
    private val apiService: WordPressApiService,
    private val pageDao: PageDao
) {

    suspend fun getPages(forceRefresh: Boolean = false): List<PageEntity> {
        val cachedPages = pageDao.getAllPages().first()
        if (cachedPages.isNotEmpty() && !forceRefresh) {
            return cachedPages.filter { it.slug != "trainingsordnung" && !it.link.contains("/trainingsordnung/") }
        }

        val remotePages = apiService.getPages(perPage = 100)
        val entities = remotePages.map { page ->
            val imageUrl = page.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
            PageEntity(
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
        }
        pageDao.deleteAll()
        pageDao.insertAll(entities)
        return entities.filter { it.slug != "trainingsordnung" && !it.link.contains("/trainingsordnung/") }
    }

    suspend fun getPage(id: Int): PageEntity? {
        return pageDao.getPageById(id)
    }

    private fun cleanHtml(html: String): String {
        return Jsoup.parse(html).text()
    }
}
