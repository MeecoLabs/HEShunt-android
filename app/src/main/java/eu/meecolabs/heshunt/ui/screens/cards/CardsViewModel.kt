/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.meecolabs.heshunt.model.CardSection
import eu.meecolabs.heshunt.model.CardWithStatus
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.model.SectionFilter
import eu.meecolabs.heshunt.model.sortedByStatus
import eu.meecolabs.heshunt.model.withStatus
import eu.meecolabs.heshunt.repositories.PropertyRepository
import eu.meecolabs.heshunt.usecase.GetCardsUseCase
import eu.meecolabs.heshunt.usecase.ToggleCardCollectedUseCase
import eu.meecolabs.heshunt.util.TimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

internal sealed interface UiState {
    data object Loading : UiState

    data class Success(
        val sections: List<CardSection>,
        val allCards: List<CardWithStatus>,
        val properties: List<Property>
    ) : UiState
}

@KoinViewModel
internal class CardsViewModel(
    private val propertyRepository: PropertyRepository,
    getCardsUseCase: GetCardsUseCase,
    private val toggleCardCollectedUseCase: ToggleCardCollectedUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {
    private val _properties = MutableStateFlow<List<Property>>(emptyList())

    private val _currentView = MutableStateFlow(CardsView.List)
    val currentView = _currentView.asStateFlow()

    private val _mapFilter = MutableStateFlow(MapFilter.ALL_MISSING)
    val mapFilter  = _mapFilter.asStateFlow()

    private val _showMapFilter = MutableStateFlow(false)
    val showMapFilter = _showMapFilter.asStateFlow()

    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty = _selectedProperty.asStateFlow()

    internal val uiState: StateFlow<UiState> = combine(
        _properties,
        getCardsUseCase(),
        _mapFilter,
    ) { properties, cards, mapFilter ->
        val now = timeProvider.now()

        val allWithStatus = cards.map { it.withStatus(now) }.sortedByStatus()

        val filteredCardSiteIds = allWithStatus.filter { mapFilter.filter(it) }
            .flatMap { card ->
                card.card.siteIds + card.card.availability.flatMap { it.siteIds ?: emptyList() }
            }.toSet()
        val filteredProperties = properties.filter { filteredCardSiteIds.contains(it.id) }

        val sections = SectionFilter.entries.map {
            CardSection(
                titleRes = it.titleRes,
                cards = allWithStatus.filter { card -> it.filter(card) }
            )
        }

        UiState.Success(
            sections = sections,
            allCards = allWithStatus,
            properties = filteredProperties
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = UiState.Loading
    )

    init {
        loadProperties()
    }

    private fun loadProperties() = viewModelScope.launch {
        try {
            _properties.value = propertyRepository.getProperties()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun setView(view: CardsView) {
        _currentView.value = view
    }

    fun toggleCollected(cardId: String, isCollected: Boolean) = viewModelScope.launch {
        toggleCardCollectedUseCase(cardId, isCollected)
    }

    fun showMapFilter(value: Boolean) {
        _showMapFilter.value = value
    }

    fun setMapFilter(filter: MapFilter) {
        _mapFilter.value = filter
        showMapFilter(false)
        selectProperty(null)
    }

    fun selectProperty(property: Property?) {
        _selectedProperty.value = property
    }
}
