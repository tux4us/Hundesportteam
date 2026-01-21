package de.hundesportteam.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WordPressPost(
    val id: Int,
    val date: String,
    @SerialName("date_gmt")
    val dateGmt: String,
    val modified: String,
    val slug: String,
    val status: String,
    val type: String,
    val link: String,
    val title: RenderedContent,
    val content: RenderedContent,
    val excerpt: RenderedContent,
    @SerialName("featured_media")
    val featuredMedia: Int = 0,
    @SerialName("_embedded")
    val embedded: Embedded? = null
)

@Serializable
data class WordPressPage(
    val id: Int,
    val date: String,
    val modified: String,
    val slug: String,
    val status: String,
    val type: String,
    val link: String,
    val title: RenderedContent,
    val content: RenderedContent,
    val excerpt: RenderedContent,
    @SerialName("featured_media")
    val featuredMedia: Int = 0,
    val parent: Int = 0,
    @SerialName("menu_order")
    val menuOrder: Int = 0,
    @SerialName("_embedded")
    val embedded: Embedded? = null
)

@Serializable
data class RenderedContent(
    val rendered: String
)

@Serializable
data class Embedded(
    @SerialName("wp:featuredmedia")
    val featuredMedia: List<FeaturedMedia>? = null
)

@Serializable
data class FeaturedMedia(
    val id: Int,
    @SerialName("source_url")
    val sourceUrl: String,
    @SerialName("media_details")
    val mediaDetails: MediaDetails? = null
)

@Serializable
data class MediaDetails(
    val width: Int? = null,
    val height: Int? = null,
    val sizes: Map<String, MediaSize>? = null
)

@Serializable
data class MediaSize(
    @SerialName("source_url")
    val sourceUrl: String
)

data class RssItem(
    val title: String,
    val link: String,
    val pubDate: String,
    val description: String,
    val imageUrl: String?
)
