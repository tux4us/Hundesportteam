package de.hundesportteam.app.data.repository

import de.hundesportteam.app.data.local.dao.ElementTemplateDao
import de.hundesportteam.app.data.local.dao.ParcourDao
import de.hundesportteam.app.data.local.defaultElementTemplates
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.local.entity.ParcourEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParcourRepository @Inject constructor(
    private val dao: ParcourDao
) {
    fun getAllParcours(): Flow<List<ParcourEntity>> = dao.getAllParcours()

    suspend fun getParcourById(id: Long): ParcourEntity? = dao.getParcourById(id)

    /** Legt einen neuen Parcour an oder überschreibt einen bestehenden (id != 0). */
    suspend fun save(parcour: ParcourEntity): Long = dao.insert(parcour)

    suspend fun update(parcour: ParcourEntity) = dao.update(parcour)

    suspend fun delete(parcour: ParcourEntity) = dao.delete(parcour)
}

class ElementTemplateRepository @Inject constructor(
    private val dao: ElementTemplateDao
) {
    fun getAllTemplates(): Flow<List<ElementTemplateEntity>> = dao.getAllTemplates()

    suspend fun getTemplateById(id: Long): ElementTemplateEntity? = dao.getTemplateById(id)

    suspend fun upsert(template: ElementTemplateEntity): Long = dao.insert(template)

    suspend fun update(template: ElementTemplateEntity) = dao.update(template)

    suspend fun delete(template: ElementTemplateEntity) = dao.delete(template)

    /** Befüllt den Katalog beim allerersten App-Start mit dem Standard-Set. */
    suspend fun seedDefaultsIfEmpty() {
        if (dao.getCount() == 0) {
            dao.insertAll(defaultElementTemplates())
        }
    }
}
