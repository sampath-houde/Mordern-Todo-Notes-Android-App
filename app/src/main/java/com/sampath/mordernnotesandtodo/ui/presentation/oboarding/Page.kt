package com.sampath.mordernnotesandtodo.ui.presentation.oboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.sampath.mordernnotesandtodo.R

data class Page(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)

val page = listOf(
    Page(
        title = R.string.onBoarding1Title,
        description = R.string.onBoarding1Description,
        image = R.drawable.on_boarding_1
    ),
    Page(
        title = R.string.onBoarding2Title,
        description = R.string.onBoarding2Description,
        image = R.drawable.on_boarding_2

    ),
    Page(
        title = R.string.onBoarding3Title,
        description = R.string.onBoarding3Description,
        image = R.drawable.on_boarding_3
    )
)

