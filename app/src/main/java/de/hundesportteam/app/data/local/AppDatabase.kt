package de.hundesportteam.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import de.hundesportteam.app.data.local.dao.BlogPostDao
import de.hundesportteam.app.data.local.dao.PageDao
import de.hundesportteam.app.data.local.dao.TrainingPageDao
import de.hundesportteam.app.data.local.entity.BlogPostEntity
import de.hundesportteam.app.data.local.entity.PageEntity
import de.hundesportteam.app.data.local.entity.TrainingPageEntity

@Database(
    entities = [
        BlogPostEntity::class,
        PageEntity::class,
        TrainingPageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blogPostDao(): BlogPostDao
    abstract fun pageDao(): PageDao
    abstract fun trainingPageDao(): TrainingPageDao
}
