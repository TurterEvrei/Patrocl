package org.turter.patrocl.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Restaurant_menu: ImageVector
    get() {
        if (_Restaurant_menu != null) {
            return _Restaurant_menu!!
        }
        _Restaurant_menu = ImageVector.Builder(
            name = "Restaurant_menu",
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
                moveTo(175f, 840f)
                lineToRelative(-56f, -56f)
                lineToRelative(410f, -410f)
                quadToRelative(-18f, -42f, -5f, -95f)
                reflectiveQuadToRelative(57f, -95f)
                quadToRelative(53f, -53f, 118f, -62f)
                reflectiveQuadToRelative(106f, 32f)
                reflectiveQuadToRelative(32f, 106f)
                reflectiveQuadToRelative(-62f, 118f)
                quadToRelative(-42f, 44f, -95f, 57f)
                reflectiveQuadToRelative(-95f, -5f)
                lineToRelative(-50f, 50f)
                lineToRelative(304f, 304f)
                lineToRelative(-56f, 56f)
                lineToRelative(-304f, -302f)
                close()
                moveToRelative(118f, -342f)
                lineTo(173f, 378f)
                quadToRelative(-54f, -54f, -54f, -129f)
                reflectiveQuadToRelative(54f, -129f)
                lineToRelative(248f, 250f)
                close()
            }
        }.build()
        return _Restaurant_menu!!
    }

private var _Restaurant_menu: ImageVector? = null
