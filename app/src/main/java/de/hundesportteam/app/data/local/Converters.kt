package de.hundesportteam.app.data.local

import androidx.room.TypeConverter
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.ElementCategory
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.LineStrokeStyle

class Converters {
    @TypeConverter
    fun fromElementCategory(value: ElementCategory?): String? = value?.name

    @TypeConverter
    fun toElementCategory(value: String?): ElementCategory? = value?.let { ElementCategory.valueOf(it) }

    @TypeConverter
    fun fromLineGeometry(value: LineGeometry?): String? = value?.name

    @TypeConverter
    fun toLineGeometry(value: String?): LineGeometry? = value?.let { LineGeometry.valueOf(it) }

    @TypeConverter
    fun fromLineStrokeStyle(value: LineStrokeStyle?): String? = value?.name

    @TypeConverter
    fun toLineStrokeStyle(value: String?): LineStrokeStyle? = value?.let { LineStrokeStyle.valueOf(it) }

    @TypeConverter
    fun fromCurveDirection(value: CurveDirection?): String? = value?.name

    @TypeConverter
    fun toCurveDirection(value: String?): CurveDirection? = value?.let { CurveDirection.valueOf(it) }
}
