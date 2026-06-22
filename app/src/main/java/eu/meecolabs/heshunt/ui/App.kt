package eu.meecolabs.heshunt.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import eu.meecolabs.heshunt.ui.screens.about.AboutDestination
import eu.meecolabs.heshunt.ui.screens.about.AboutScreen
import eu.meecolabs.heshunt.ui.screens.cards.CardsDestination
import eu.meecolabs.heshunt.ui.screens.cards.CardsScreen
import eu.meecolabs.heshunt.ui.screens.detail.CardDetailDestination
import eu.meecolabs.heshunt.ui.screens.detail.CardDetailScreen

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
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
