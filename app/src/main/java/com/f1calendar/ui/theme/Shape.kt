package com.f1calendar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Expressive Shapes - more dynamic and varied corner treatments
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 8.dp,
        bottomEnd = 4.dp,
        bottomStart = 8.dp
    ),
    small = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 16.dp,
        bottomEnd = 8.dp,
        bottomStart = 16.dp
    ),
    medium = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 20.dp,
        bottomEnd = 16.dp,
        bottomStart = 20.dp
    ),
    large = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 28.dp,
        bottomEnd = 24.dp,
        bottomStart = 28.dp
    ),
    extraLarge = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 40.dp,
        bottomEnd = 32.dp,
        bottomStart = 40.dp
    )
)
