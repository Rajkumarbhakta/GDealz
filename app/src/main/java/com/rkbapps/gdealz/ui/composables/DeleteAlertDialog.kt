package com.rkbapps.gdealz.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.stringResource
import com.rkbapps.gdealz.R

@Composable
fun DeleteAlertDialog(
    warningText:String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.delete_confirm_title))
        },
        text = { Text(warningText) },
        confirmButton = {
            OutlinedButton(
                onClick = onDelete
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}