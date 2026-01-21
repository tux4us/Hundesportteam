package de.hundesportteam.app.data.remote

import de.hundesportteam.app.data.model.WordPressPage
import de.hundesportteam.app.data.model.WordPressPost
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordPressApiService {
    
    @GET("wp-json/wp/v2/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 100,
        @Query("_embed") embed: Boolean = true,
        @Query("orderby") orderBy: String = "date",
        @Query("order") order: String = "desc"
    ): List<WordPressPost>

    @GET("wp-json/wp/v2/posts/{id}")
    suspend fun getPost(
        @Path("id") id: Int,
        @Query("_embed") embed: Boolean = true
    ): WordPressPost

    @GET("wp-json/wp/v2/pages")
    suspend fun getPages(
        @Query("per_page") perPage: Int = 100,
        @Query("_embed") embed: Boolean = true,
        @Query("orderby") orderBy: String = "menu_order",
        @Query("order") order: String = "asc"
    ): List<WordPressPage>

    @GET("wp-json/wp/v2/pages/{id}")
    suspend fun getPage(
        @Path("id") id: Int,
        @Query("_embed") embed: Boolean = true
    ): WordPressPage

    @GET("wp-json/wp/v2/pages")
    suspend fun getPagesByParent(
        @Query("parent") parent: Int,
        @Query("per_page") perPage: Int = 100,
        @Query("_embed") embed: Boolean = true,
        @Query("orderby") orderBy: String = "menu_order",
        @Query("order") order: String = "asc"
    ): List<WordPressPage>
}
