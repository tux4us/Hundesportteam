package de.hundesportteam.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blog_posts")
data class BlogPostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val excerpt: String,
    val date: String,
    val link: String,
    val imageUrl: String?,
    val cachedAt: Long
)

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val excerpt: String,
    val link: String,
    val slug: String,
    val parent: Int,
    val menuOrder: Int,
    val imageUrl: String?,
    val cachedAt: Long
)

@Entity(tableName = "training_pages")
data class TrainingPageEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    val excerpt: String,
    val link: String,
    val slug: String,
    val parent: Int,
    val menuOrder: Int,
    val imageUrl: String?,
    val cachedAt: Long
)
