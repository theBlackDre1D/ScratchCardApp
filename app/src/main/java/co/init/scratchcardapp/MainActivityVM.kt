package co.init.scratchcardapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.init.base.BaseVM
import co.init.common.extensions.doInCoroutine
import co.init.common.extensions.doInIOCoroutine
import co.init.scratchcardapp.data.Card
import co.init.scratchcardapp.data.ScratchCardState
import co.init.scratchcardapp.features.card_activation.domain.ActivateScratchedCardUseCase
import co.init.scratchcardapp.features.card_activation.domain.CardActivationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val activateScratchedCardUseCase: ActivateScratchedCardUseCase
) : BaseVM() {

    private val TAG = MainActivityVM::class.java.simpleName

    private val _scratchCardLiveData = MutableLiveData(Card())
    val scratchCardLiveData: LiveData<Card> = _scratchCardLiveData

    private val _activateCardResult = MutableLiveData<Result<CardActivationResponse>>()
    val activateCardResult: LiveData<Result<CardActivationResponse>> = _activateCardResult

    fun activateCard() {
        doInIOCoroutine {
            _scratchCardLiveData.value?.let { card ->
                if (card.cardState == ScratchCardState.NEEDS_ACTIVATION && card.cardNumber != null) {
                    activateScratchedCardUseCase.invoke(card.cardNumber).collect { result ->
                        result.fold(
                            onSuccess = {
                                _scratchCardLiveData.postValue(_scratchCardLiveData.value?.copy(cardState = ScratchCardState.ACTIVATED))
                            },
                            onFailure = {
                                Log.e(TAG, it.message.orEmpty())
                            }
                        )
                        _activateCardResult.postValue(result)
                    }
                } // po spravnosti by tu bolo treba dorobit nejaku info hlasku, ze karta nie je pripravena na aktivaciu
            }
        }
    }

    fun scratchCard() {
        doInCoroutine {
            setCardState(ScratchCardState.GETS_SCRATCH)

            delay(2000)
            var card = _scratchCardLiveData.value
            card = card?.copy(cardNumber = Card.generatePartNumber())
            _scratchCardLiveData.value = card

            setCardState(ScratchCardState.NEEDS_ACTIVATION)
        }
    }

    private fun setCardState(state: ScratchCardState) {
        _scratchCardLiveData.value = _scratchCardLiveData.value?.copy(cardState = state)
    }
}