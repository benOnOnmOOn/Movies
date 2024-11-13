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
import com.bz.presentation.screens.R

@Composable
internal fun ErrorDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Unit = {}
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        title = {
            Text(text = stringResource(R.string.generic_error_tittle))
        },
        text = {
            Text(text = stringResource(R.string.generic_error_label))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.confirm_error_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss_error_button_label))
            }
        }
    )
}

@Composable
internal fun NoInternetDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onConfirmation: () -> Unit = {}
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        title = {
            Text(text = stringResource(R.string.no_internet_error_title))
        },
        text = {
            Text(text = stringResource(R.string.no_internet_error_label))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(text = stringResource(R.string.try_again_network_error_button_label))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss_error_button_label))
            }
        }
    )
}
