package org.turter.patrocl.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Table_restaurant: ImageVector
    get() {
        if (_Table_restaurant != null) {
            return _Table_restaurant!!
        }
        _Table_restaurant = ImageVector.Builder(
            name = "Table_restaurant",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(173f, 360f)
                horizontalLineToRelative(614f)
                lineToRelative(-34f, -120f)
                horizontalLineTo(208f)
                close()
                moveToRelative(499f, 80f)
                horizontalLineTo(289f)
                lineToRelative(-11f, 80f)
                horizontalLineToRelative(404f)
                close()
                moveTo(160f, 800f)
                lineToRelative(49f, -360f)
                horizontalLineToRelative(-89f)
                quadToRelative(-20f, 0f, -31.5f, -16f)
                reflectiveQuadTo(82f, 389f)
                lineToRelative(57f, -200f)
                quadToRelative(4f, -13f, 14f, -21f)
                reflectiveQuadToRelative(24f, -8f)
                horizontalLineToRelative(606f)
                quadToRelative(14f, 0f, 24f, 8f)
                reflectiveQuadToRelative(14f, 21f)
                lineToRelative(57f, 200f)
                quadToRelative(5f, 19f, -6.5f, 35f)
                reflectiveQuadTo(840f, 440f)
                horizontalLineToRelative(-88f)
                lineToRelative(48f, 360f)
                horizontalLineToRelative(-80f)
                lineToRelative(-27f, -200f)
                horizontalLineTo(267f)
                lineToRelative(-27f, 200f)
                close()
            }
        }.build()
        return _Table_restaurant!!
    }

private var _Table_restaurant: ImageVector? = null
