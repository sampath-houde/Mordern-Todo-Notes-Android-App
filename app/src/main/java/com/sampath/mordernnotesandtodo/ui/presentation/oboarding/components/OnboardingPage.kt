package com.sampath.mordernnotesandtodo.ui.presentation.oboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sampath.mordernnotesandtodo.R
import com.sampath.mordernnotesandtodo.ui.presentation.Dimens
import com.sampath.mordernnotesandtodo.ui.presentation.oboarding.Page
import com.sampath.mordernnotesandtodo.ui.theme.MordernNotesTodoTheme

@Composable
fun OnboardingPage(
    page: Page
) {
    val context = LocalContext.current
    
    Column {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.6f),
            painter = painterResource(id = page.image),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )

        Spacer(
            modifier = Modifier.height(Dimens.MediumPadding1)
        )

        Text(
            text = context.getString((page.title)),
            modifier = Modifier.padding(horizontal = Dimens.MediumPadding2),
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            color = colorResource(id = R.color.display_small)
        )

        Text(
            text = context.getString(page.description),
            modifier = Modifier.padding(horizontal = Dimens.MediumPadding2),
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
            color = colorResource(id = R.color.text_medium)
        )
    }

}