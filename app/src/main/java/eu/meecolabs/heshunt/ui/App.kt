/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import eu.meecolabs.heshunt.ui.screens.about.AboutDestination
import eu.meecolabs.heshunt.ui.screens.about.AboutScreen
import eu.meecolabs.heshunt.ui.screens.cards.CardsDestination
import eu.meecolabs.heshunt.ui.screens.cards.CardsScreen
import eu.meecolabs.heshunt.ui.screens.detail.CardDetailDestination
import eu.meecolabs.heshunt.ui.screens.detail.CardDetailScreen
import eu.meecolabs.heshunt.ui.screens.edit.EditDestination
import eu.meecolabs.heshunt.ui.screens.edit.EditScreen

@Composable
fun App() {
    val backStack = rememberNavBackStack(CardsDestination)

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<CardsDestination> {
                CardsScreen(
                    onCardClick = {
                        backStack.add(CardDetailDestination(it))
                    },
                    onAboutClick = {
                        backStack.add(AboutDestination)
                    }
                )
            }

            entry<AboutDestination> {
                AboutScreen(
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<CardDetailDestination> {
                CardDetailScreen(
                    it.id,
                    onEdit = {
                        backStack.add(EditDestination(it.id))
                    },
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<EditDestination>(
                metadata = metadata {
                    put(NavDisplay.TransitionKey) {
                        slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween()
                        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                    }
                    put(NavDisplay.PopTransitionKey) {
                        EnterTransition.None togetherWith
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween()
                                )
                    }
                    put(NavDisplay.PredictivePopTransitionKey) {
                        EnterTransition.None togetherWith
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween()
                                )
                    }
                }
            ) {
                EditScreen(
                    it.id,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
