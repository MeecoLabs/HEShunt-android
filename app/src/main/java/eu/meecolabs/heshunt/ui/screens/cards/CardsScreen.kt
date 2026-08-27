package eu.meecolabs.heshunt.ui.screens.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
    onAboutClick: () -> Unit,
    viewModel: CardsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentView by viewModel.currentView.collectAsStateWithLifecycle()
    val mapFilter by viewModel.mapFilter.collectAsStateWithLifecycle()
    val showMapFilter by viewModel.showMapFilter.collectAsStateWithLifecycle()
    val selectedProperty by viewModel.selectedProperty.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    SingleChoiceSegmentedButtonRow {
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
                    when (currentView) {
                        CardsView.List ->
                            IconButton(onClick = onAboutClick) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_info),
                                    contentDescription = stringResource(R.string.cards_about)
                                )
                            }

                        CardsView.Map ->
                            Box {
                                TextButton(onClick = {
                                    viewModel.showMapFilter(true)
                                }) {
                                    Text(text = stringResource(mapFilter.labelRes))
                                }

                                DropdownMenu(
                                    expanded = showMapFilter,
                                    onDismissRequest = {
                                        viewModel.showMapFilter(false)
                                    }
                                ) {
                                    MapFilter.entries.forEach { filter ->
                                        DropdownMenuItem(
                                            leadingIcon = {
                                                RadioButton(
                                                    onClick = null,
                                                    selected = mapFilter == filter
                                                )
                                            },
                                            text = {
                                                Text(text = stringResource(filter.labelRes))
                                            },
                                            onClick = {
                                                viewModel.setMapFilter(filter)
                                            }
                                        )
                                    }
                                }
                            }
                    }
                }
            )
        }
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding())

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
                when (currentView) {
                    CardsView.List ->
                        CardListContent(
                            state = state,
                            onCardClick = onCardClick,
                            onToggleCollected = { id, collected ->
                                viewModel.toggleCollected(id, collected)
                            },
                            modifier = contentModifier
                        )

                    CardsView.Map ->
                        CardMapContent(
                            state = state,
                            selectedProperty = selectedProperty,
                            onSelectProperty = viewModel::selectProperty,
                            onCardClick = onCardClick,
                            modifier = contentModifier
                        )
                }
            }
        }
    }
}
