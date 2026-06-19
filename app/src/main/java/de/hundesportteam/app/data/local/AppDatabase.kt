package de.hundesportteam.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.hundesportteam.app.data.local.dao.BlogPostDao
import de.hundesportteam.app.data.local.dao.ElementTemplateDao
import de.hundesportteam.app.data.local.dao.PageDao
import de.hundesportteam.app.data.local.dao.ParcourDao
import de.hundesportteam.app.data.local.dao.TrainingPageDao
import de.hundesportteam.app.data.local.entity.BlogPostEntity
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.local.entity.PageEntity
import de.hundesportteam.app.data.local.entity.ParcourEntity
import de.hundesportteam.app.data.local.entity.TrainingPageEntity

@Database(
    entities = [
        BlogPostEntity::class,
        PageEntity::class,
        TrainingPageEntity::class,
        ParcourEntity::class,
        ElementTemplateEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blogPostDao(): BlogPostDao
    abstract fun pageDao(): PageDao
    abstract fun trainingPageDao(): TrainingPageDao
    abstract fun parcourDao(): ParcourDao
    abstract fun elementTemplateDao(): ElementTemplateDao
}
