package eu.meecolabs.heshunt.ui.screens.cards

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.meecolabs.heshunt.R
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.CardStatus
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.repositories.PropertyRepository
import eu.meecolabs.heshunt.usecase.GetCardsUseCase
import eu.meecolabs.heshunt.usecase.ToggleCardCollectedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import java.time.LocalDate

internal enum class MapFilter(
    @get:StringRes val labelRes: Int
) {
    All(R.string.card_map_filter_all),
    Missing(R.string.card_map_filter_missing),
    Expired(R.string.card_map_filter_expired),
    Upcoming(R.string.card_map_filter_upcoming)
}

internal enum class CardsView {
    List, Map
}

internal sealed interface UiState {
    data object Loading : UiState

    data class Success(
        val available: List<Card>,
        val upcoming: List<Card>,
        val collected: List<Card>,
        val expired: List<Card>,
        val allCards: List<Card>,
        val properties: List<Property>
    ) : UiState
}

@KoinViewModel
internal class CardsViewModel(
    private val propertyRepository: PropertyRepository,
    getCardsUseCase: GetCardsUseCase,
    private val toggleCardCollectedUseCase: ToggleCardCollectedUseCase
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
        val now = LocalDate.now()

        val filteredProperties = when (mapFilter) {
            MapFilter.All -> {
                val propertyIdsWithCards = cards.flatMap { it.siteIds + it.availability.flatMap { a -> a.siteIds ?: emptyList() } }.toSet()
                properties.filter { propertyIdsWithCards.contains(it.id) }
            }

            MapFilter.Missing -> {
                val missingCardSiteIds = cards.filter { !it.isCollected && it.getStatus(now) == CardStatus.ACTIVE }
                    .flatMap { card ->
                        card.siteIds + card.availability.flatMap { it.siteIds ?: emptyList() }
                    }.toSet()
                properties.filter { missingCardSiteIds.contains(it.id) }
            }

            MapFilter.Expired -> {
                val expiredCardSiteIds = cards.filter { !it.isCollected && it.getStatus(now) == CardStatus.EXPIRED }
                    .flatMap { card ->
                        card.siteIds + card.availability.flatMap { it.siteIds ?: emptyList() }
                    }.toSet()
                properties.filter { expiredCardSiteIds.contains(it.id) }
            }

            MapFilter.Upcoming -> {
                val upcomingCardSiteIds = cards.filter { !it.isCollected && it.getStatus(now) == CardStatus.UPCOMING }
                    .flatMap { card ->
                        card.siteIds + card.availability.flatMap { it.siteIds ?: emptyList() }
                    }.toSet()
                properties.filter { upcomingCardSiteIds.contains(it.id) }
            }
        }

        UiState.Success(
            available = cards.filter { !it.isCollected && it.getStatus(now) == CardStatus.ACTIVE },
            upcoming = cards.filter { !it.isCollected && it.getStatus(now) == CardStatus.UPCOMING },
            collected = cards.filter { it.isCollected },
            expired = cards.filter { !it.isCollected && it.getStatus(now) == CardStatus.EXPIRED },
            allCards = cards,
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
