package com.bz.movies.presentation.screens.more

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bz.movies.presentation.theme.MoviesTheme

@Composable
fun More(modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "More",
            modifier = modifier,
        )

//        RadioButton(
//            selected = selectedOption.value == "Option1",
//            onClick = { selectedOption.value = "Option1" },
//        )
//        Text("Option 1")
//
//        RadioButton(
//            selected = selectedOption.value == "Option2",
//            onClick = { selectedOption.value = "Option2" },
//        )
//        Text("Option 2")
    }
}

@Preview(showBackground = true)
@Composable
fun MorePreview() {
    MoviesTheme {
        More()
    }
}
