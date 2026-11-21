package com.bz.movies.presentation.screens.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bz.movies.presentation.screens.R

@Composable
internal fun ErrorDialog(
    effect: MovieEffect,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title: Int
    val label: Int
    val confirmLabel: Int
    when (effect) {
        MovieEffect.NetworkConnectionError -> {
            title = R.string.no_internet_error_title
            label = R.string.no_internet_error_label
            confirmLabel = R.string.try_again_network_error_button_label
        }

        MovieEffect.UnknownError -> {
            title = R.string.generic_error_tittle
            label = R.string.generic_error_label
            confirmLabel = R.string.confirm_error_button
        }
    }

    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        title = { Text(text = stringResource(title)) },
        text = { Text(text = stringResource(label)) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(confirmLabel)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dismiss_error_button_label))
            }
        }
    )
}
