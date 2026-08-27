package eu.meecolabs.heshunt.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.meecolabs.heshunt.model.CardWithStatus
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.model.withStatus
import eu.meecolabs.heshunt.repositories.PropertyRepository
import eu.meecolabs.heshunt.usecase.GetCardsUseCase
import eu.meecolabs.heshunt.usecase.ToggleCardCollectedUseCase
import eu.meecolabs.heshunt.util.TimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

internal sealed interface UiState {
    data object Loading : UiState

    data class Error(
        val message: String
    ) : UiState

    data class Success(
        val card: CardWithStatus,
        val availableAt: List<Property>,
        val allSites: List<Property>
    ) : UiState
}

@KoinViewModel
class CardDetailViewModel(
    @InjectedParam private val cardId: String,
    getCardsUseCase: GetCardsUseCase,
    private val propertyRepository: PropertyRepository,
    private val toggleCardCollectedUseCase: ToggleCardCollectedUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {
    private val _properties = MutableStateFlow<List<Property>>(emptyList())

    internal val uiState: StateFlow<UiState> = combine(
        getCardsUseCase(),
        _properties
    ) { cards, properties ->
        val card = cards.find { it.id == cardId }
        if (card == null) {
            UiState.Error("Card not found")
        } else {
            val now = timeProvider.now()
            val availableAt = properties.filter { card.isAvailableAt(it.id, now) }
            val allSites = properties.filter { card.isAssociatedWith(it.id) }
            UiState.Success(card.withStatus(now), availableAt, allSites)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    init {
        loadProperties()
    }

    private fun loadProperties()  = viewModelScope.launch {
        try {
            _properties.value = propertyRepository.getProperties()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun toggleCollected(isCollected: Boolean) = viewModelScope.launch {
        toggleCardCollectedUseCase(cardId, isCollected)
    }
}
