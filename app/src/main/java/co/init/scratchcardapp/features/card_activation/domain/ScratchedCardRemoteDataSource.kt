package co.init.scratchcardapp.features.card_activation.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScratchedCardRemoteDataSource @Inject constructor(
    private val service: ScratchedCardService
) {

    suspend fun activateCard(cardNumber: String): Flow<Result<CardActivationResponse>> {
        return flow {
            try {
                val response = service.activateCard(cardNumber)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        emit(Result.success(body))
                    }
                } else {
                    emit(Result.failure(Throwable(response.message())))
                }
            } catch (t: Throwable) {
                emit(Result.failure(t))
            }
        }
    }
}