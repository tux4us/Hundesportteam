package de.hundesportteam.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.ElementCategory
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.LineStrokeStyle

/**
 * Ein vom Nutzer erstellter Trainingsparcour. dataJson enthält die serialisierte
 * ParcourData (Wegpunktkette). fieldWidth/fieldLength sind die reale Größe der
 * Übungsfläche in Metern (Standard 30 x 40 m), pro Parcour einstellbar.
 */
@Entity(tableName = "parcours")
data class ParcourEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val fieldWidth: Double = 30.0,
    val fieldLength: Double = 40.0,
    val dataJson: String,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Eintrag im erweiterbaren Übungs-/Streckenkatalog. Wird beim ersten App-Start mit
 * einem Standard-Set befüllt (siehe DefaultElementTemplates.kt) und kann danach vom
 * Nutzer frei ergänzt, bearbeitet oder gelöscht werden.
 */
@Entity(tableName = "element_templates")
data class ElementTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val abbreviation: String,
    val category: ElementCategory,
    val colorHex: String,
    val lineGeometry: LineGeometry? = null,      // nur category == LINIE
    val strokeStyle: LineStrokeStyle? = null,    // nur lineGeometry == GERADE
    val defaultRadius: Double? = null,           // nur lineGeometry == BOGEN
    val defaultDirection: CurveDirection? = null,// nur lineGeometry == BOGEN
    val defaultSweepAngle: Int? = null           // nur lineGeometry == BOGEN: 90/180/270/360
)
