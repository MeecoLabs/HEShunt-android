package eu.meecolabs.heshunt.ui.screens.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.ui.screens.cards.list.CardListContent
import eu.meecolabs.heshunt.ui.screens.cards.map.CardMapContent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CardsScreen(
    onCardClick: (String) -> Unit,
    onAboutClick: () -> Unit
) {
    val viewModel: CardsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    SingleChoiceSegmentedButtonRow {
                        val currentView = (uiState as? UiState.Success)?.currentView
                        SegmentedButton(
                            selected = currentView == CardsView.List,
                            onClick = { viewModel.setView(CardsView.List) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text(text = stringResource(R.string.cards_view_list))
                        }

                        SegmentedButton(
                            selected = currentView == CardsView.Map,
                            onClick = { viewModel.setView(CardsView.Map) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text(text = stringResource(R.string.cards_view_map))
                        }
                    }
                },
                actions = {
                    val state = uiState as? UiState.Success
                    when (state?.currentView) {
                        CardsView.List ->
                            IconButton(onClick = onAboutClick) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_info),
                                    contentDescription = stringResource(R.string.cards_about)
                                )
                            }

                        CardsView.Map ->
                            Box {
                                TextButton(onClick = { showFilterMenu = true }) {
                                    Text(text = stringResource(state.mapFilter.labelRes))
                                }

                                DropdownMenu(
                                    expanded = showFilterMenu,
                                    onDismissRequest = { showFilterMenu = false }
                                ) {
                                    MapFilter.entries.forEach { filter ->
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                RadioButton(
                                                    onClick = null,
                                                    selected = state.mapFilter == filter
                                                )
                                            },
                                            text = {
                                                Text(text = stringResource(state.mapFilter.labelRes))
                                            },
                                            onClick = {
                                                viewModel.setMapFilter(filter)
                                                showFilterMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                        else ->
                            Unit
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                when (state.currentView) {
                    CardsView.List ->
                        CardListContent(
                            state = state,
                            onCardClick = onCardClick,
                            onToggleCollected = { id, collected -> viewModel.toggleCollected(id, collected) },
                            modifier = Modifier.padding(padding)
                        )

                    CardsView.Map ->
                        CardMapContent(
                            state = state,
                            modifier = Modifier.padding(padding)
                        )
                }
            }
        }
    }
}
