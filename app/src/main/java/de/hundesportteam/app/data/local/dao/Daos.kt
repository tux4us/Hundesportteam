package de.hundesportteam.app.data.local.dao

import androidx.room.*
import de.hundesportteam.app.data.local.entity.BlogPostEntity
import de.hundesportteam.app.data.local.entity.PageEntity
import de.hundesportteam.app.data.local.entity.TrainingPageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlogPostDao {
    @Query("SELECT * FROM blog_posts ORDER BY date DESC")
    fun getAllPosts(): Flow<List<BlogPostEntity>>

    @Query("SELECT * FROM blog_posts WHERE id = :id")
    suspend fun getPostById(id: Int): BlogPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<BlogPostEntity>)

    @Query("DELETE FROM blog_posts")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM blog_posts")
    suspend fun getCount(): Int
}

@Dao
interface PageDao {
    @Query("SELECT * FROM pages ORDER BY menuOrder ASC, title ASC")
    fun getAllPages(): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE id = :id")
    suspend fun getPageById(id: Int): PageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pages: List<PageEntity>)

    @Query("DELETE FROM pages")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM pages")
    suspend fun getCount(): Int
}

@Dao
interface TrainingPageDao {
    @Query("SELECT * FROM training_pages ORDER BY menuOrder ASC, title ASC")
    fun getAllTrainingPages(): Flow<List<TrainingPageEntity>>

    @Query("SELECT * FROM training_pages WHERE id = :id")
    suspend fun getTrainingPageById(id: Int): TrainingPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pages: List<TrainingPageEntity>)

    @Query("DELETE FROM training_pages")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM training_pages")
    suspend fun getCount(): Int
}
