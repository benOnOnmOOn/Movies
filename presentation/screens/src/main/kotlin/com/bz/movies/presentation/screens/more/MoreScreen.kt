package com.bz.movies.presentation.screens.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bz.presentation.screens.R

@Composable
internal fun MoreScreen(moreScreenViewModel: MoreScreenViewModel = hiltViewModel()) {
    val moreState by moreScreenViewModel.state.collectAsStateWithLifecycle()
    MoreScreen(moreState, moreScreenViewModel::sendEvent)
}

@Composable
internal fun MoreScreen(state: MoreState = MoreState(), sendEvent: (MoreEvent) -> Unit = {}) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = stringResource(R.string.more_screen_title))

        Text(stringResource(R.string.more_screen_english_label))
        RadioButton(
            selected = state.language == Language.ENG,
            onClick = { sendEvent(MoreEvent.OnLanguageClick(Language.ENG)) }
        )

        Text(stringResource(R.string.more_menu_polish_label))
        RadioButton(
            selected = state.language == Language.POL,
            onClick = { sendEvent(MoreEvent.OnLanguageClick(Language.POL)) }
        )
    }
}
