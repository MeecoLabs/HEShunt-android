/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.ui.screens.edit.components.EditForm
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    cardId: String,
    onBackClick: () -> Unit,
    viewModel: EditScreenViewModel = koinViewModel { parametersOf(cardId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()
    val property by viewModel.property.collectAsStateWithLifecycle()
    val hasChanged by viewModel.hasChanged.collectAsStateWithLifecycle()

    var showConfirmDismiss by rememberSaveable { mutableStateOf(false) }

    BackHandler(hasChanged) {
        showConfirmDismiss = true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (hasChanged) {
                                showConfirmDismiss = true
                            } else {
                                onBackClick()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.cancel_cd)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.edit_collect_title)
                    )
                },
                actions = {
                    val state = uiState
                    if (state is UiState.Success) {
                        IconButton(
                            onClick = {
                                viewModel.save(onBackClick)
                            },
                            enabled = hasChanged
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_checkmark),
                                contentDescription = stringResource(R.string.edit_collect_save_action)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = contentModifier
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                EditForm(
                    card = state.card,
                    date = date,
                    onSetDate = viewModel::setDate,
                    property = property,
                    availableAt = state.availableAt,
                    allSites = state.allSites,
                    onSetProperty = viewModel::setProperty,
                    modifier = contentModifier
                )
            }

            is UiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = contentModifier
                ) {
                    Text(text = state.message)
                }
            }
        }
    }

    if (showConfirmDismiss) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDismiss = false
            },
            title = {
                Text(
                    text = stringResource(R.string.edit_collect_confirm_dismiss_title)
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.edit_collect_confirm_dismiss_description)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onBackClick
                ) {
                    Text(
                        text = stringResource(R.string.edit_collect_confirm_dismiss_confirm)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDismiss = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.edit_collect_confirm_dismiss_cancel)
                    )
                }
            }
        )
    }
}
