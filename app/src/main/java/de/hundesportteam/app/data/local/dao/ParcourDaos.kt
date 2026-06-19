package de.hundesportteam.app.data.local.dao

import androidx.room.*
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.local.entity.ParcourEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcourDao {
    @Query("SELECT * FROM parcours ORDER BY updatedAt DESC")
    fun getAllParcours(): Flow<List<ParcourEntity>>

    @Query("SELECT * FROM parcours WHERE id = :id")
    suspend fun getParcourById(id: Long): ParcourEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parcour: ParcourEntity): Long

    @Update
    suspend fun update(parcour: ParcourEntity)

    @Delete
    suspend fun delete(parcour: ParcourEntity)
}

@Dao
interface ElementTemplateDao {
    @Query("SELECT * FROM element_templates ORDER BY category ASC, name ASC")
    fun getAllTemplates(): Flow<List<ElementTemplateEntity>>

    @Query("SELECT * FROM element_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): ElementTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: ElementTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<ElementTemplateEntity>)

    @Update
    suspend fun update(template: ElementTemplateEntity)

    @Delete
    suspend fun delete(template: ElementTemplateEntity)

    @Query("SELECT COUNT(*) FROM element_templates")
    suspend fun getCount(): Int
}
