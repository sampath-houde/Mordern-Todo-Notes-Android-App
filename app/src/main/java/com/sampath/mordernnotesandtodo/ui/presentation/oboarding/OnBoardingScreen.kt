package com.sampath.mordernnotesandtodo.ui.presentation.oboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sampath.mordernnotesandtodo.ui.presentation.Dimens.MediumPadding2
import com.sampath.mordernnotesandtodo.ui.presentation.Dimens.PagerWidth
import com.sampath.mordernnotesandtodo.ui.presentation.common.NotesBackButton
import com.sampath.mordernnotesandtodo.ui.presentation.common.NotesButton
import com.sampath.mordernnotesandtodo.ui.presentation.oboarding.components.OnboardingPage
import com.sampath.mordernnotesandtodo.ui.presentation.oboarding.components.PageIndicator
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun OnBoardingScreen(
    event: (OnBoardingEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = 0) {
            page.size
        }

        val buttonState = remember {
            derivedStateOf {
                when(pagerState.currentPage) {
                    0 -> listOf("","Next")
                    1 -> listOf("Back","Next")
                    2 -> listOf("Back","Get Started")
                    else -> emptyList()
                }
            }
        }
        
        HorizontalPager(state = pagerState) { index ->
            OnboardingPage(page = page[index])
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = MediumPadding2)
                .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
        ) {
            PageIndicator(
                modifier = Modifier.width(PagerWidth),
                pageSize = page.size,
                selectedPage = pagerState.currentPage
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                val scope = rememberCoroutineScope()

                if (buttonState.value[0].isNotEmpty()) {
                    NotesBackButton(
                        text = buttonState.value[0],
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }

                        }
                    )
                }

                NotesButton(
                    text = buttonState.value[1],
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage == page.size - 1) {
                                event(OnBoardingEvent.SaveAppEntry)
                            } else {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }

                        }
                    }
                )

            }
        }

        Spacer(modifier = Modifier.weight(0.5f))
    }
}