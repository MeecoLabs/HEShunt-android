package eu.meecolabs.heshunt.ui.screens.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.CardWithStatus
import eu.meecolabs.heshunt.model.Property
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

internal sealed interface UiState {
    data object Loading : UiState

    data class Success(
        val available: List<CardWithStatus>,
        val upcoming: List<CardWithStatus>,
        val collected: List<CardWithStatus>,
        val expired: List<CardWithStatus>,
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

    private val _mapFilter = MutableStateFlow(MapFilter.Missing)
    val mapFilter  = _mapFilter.asStateFlow()

    private val _showMapFilter = MutableStateFlow(false)
    val showMapFilter = _showMapFilter.asStateFlow()

    private val _selectedProperty = MutableStateFlow<Property?>(null)
    val selectedProperty = _selectedProperty.asStateFlow()

    internal val uiState: StateFlow<UiState> = combine(
        _properties,
        getCardsUseCase(),
        _mapFilter
    ) { properties, cards, mapFilter ->
        val now = timeProvider.now()

        val allWithStatus = cards.map { it.withStatus(now) }.sortedByStatus()

        val filteredProperties = when (mapFilter) {
            MapFilter.All -> {
                val propertyIdsWithCards = cards.flatMap { it.siteIds + it.availability.flatMap { a -> a.siteIds ?: emptyList() } }.toSet()
                properties.filter { propertyIdsWithCards.contains(it.id) }
            }

            else -> {
                val filteredCardSiteIds = allWithStatus.filter { !it.card.isCollected && it.status == mapFilter.targetStatus }
                    .flatMap { card ->
                        card.card.siteIds + card.card.availability.flatMap { it.siteIds ?: emptyList() }
                    }.toSet()
                properties.filter { filteredCardSiteIds.contains(it.id) }
            }
        }

        UiState.Success(
            available = allWithStatus.filter { !it.card.isCollected && it.status == CardStatus.ACTIVE },
            upcoming = allWithStatus.filter { !it.card.isCollected && it.status == CardStatus.UPCOMING },
            collected = allWithStatus.filter { it.card.isCollected },
            expired = allWithStatus.filter { !it.card.isCollected && it.status == CardStatus.EXPIRED },
            allCards = allWithStatus,
            properties = filteredProperties
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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
