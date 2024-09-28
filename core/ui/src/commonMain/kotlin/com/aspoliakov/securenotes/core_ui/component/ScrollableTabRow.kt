package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.LocalCustomColorSchemeProvider

/**
 * Project SecureNotes
 */

@Composable
fun ScrollableTabRow(
        modifier: Modifier = Modifier,
        tabs: List<String> = listOf(),
        selectedTabIndex: Int = 0,
        currentPageOffsetFraction: Float = 0F,
        onTabClick: (index: Int) -> Unit = {},
) {
    Row(
            modifier = modifier
                    .fillMaxWidth()
                    .background(color = LocalCustomColorSchemeProvider.current.tabMenuBackground)
                    .padding(start = 20.dp),
    ) {
        androidx.compose.material3.ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                containerColor = LocalCustomColorSchemeProvider.current.tabMenuBackground,
                contentColor = LocalCustomColorSchemeProvider.current.tabMenuAccent,
                indicator = {
                    Indicator(
                            modifier = modifier,
                            tabPositions = it,
                            selectedTabIndex = selectedTabIndex,
                            currentPageOffsetFraction = currentPageOffsetFraction,
                    )
                },
                divider = {},
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                        text = { Text(text = tab.uppercase()) },
                        selected = selectedTabIndex == index,
                        onClick = { onTabClick(index) },
                        selectedContentColor = LocalCustomColorSchemeProvider.current.tabMenuAccentSelect,
                        unselectedContentColor = LocalCustomColorSchemeProvider.current.tabMenuInactive,
                )
            }
        }
    }
}

@Composable
fun Indicator(
        modifier: Modifier = Modifier,
        tabPositions: List<TabPosition> = emptyList(),
        selectedTabIndex: Int = 0,
        currentPageOffsetFraction: Float = 0F,
) {
    val currentTabPosition = tabPositions[selectedTabIndex]
    val currentTabWidth by animateDpAsState(
            targetValue = currentTabPosition.width,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "currentTabWidth",
    )
    val indicatorOffset = if (currentPageOffsetFraction < 0) {
        tabPositions.getOrNull(selectedTabIndex - 1)?.let { previousTabPosition ->
            currentTabPosition.left.value + (currentPageOffsetFraction * previousTabPosition.width.value)
        }
    } else {
        tabPositions.getOrNull(selectedTabIndex + 1)?.let { nextTabPosition ->
            currentTabPosition.left.value + (currentPageOffsetFraction * nextTabPosition.width.value)
        }
    }
    TabRowDefaults.Indicator(
            modifier = modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomStart)
                    .offset(x = indicatorOffset?.dp ?: currentTabPosition.left)
                    .width(currentTabWidth)
    )
}
