package co.init.scratchcardapp.features.card_activation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import co.init.base.BaseFragment
import co.init.common.extensions.onClickDebounce
import co.init.scratchcardapp.MainActivityVM
import co.init.scratchcardapp.R
import co.init.scratchcardapp.databinding.CardActivationFragmentBinding

class CardActivationFragment : BaseFragment<CardActivationFragmentBinding>() {

    private val sharedActivityViewModel: MainActivityVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CardActivationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupButton()
        initObservers()
    }

    private fun setupButton() {
        binding.activateCardB.onClickDebounce {
            sharedActivityViewModel.activateCard()
        }
    }

    private fun initObservers() {
        sharedActivityViewModel.activateCardResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    context?.let { nonNullContext ->
                        Toast.makeText(nonNullContext, R.string.activation_card__activation_success, Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    context?.let { nonNullContext ->
                        Toast.makeText(nonNullContext, R.string.activation_card__activation_error, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        sharedActivityViewModel.scratchCardLiveData.observe(viewLifecycleOwner) { card ->
            binding.scratchCardStateValueTV.text = card.cardState.toString()
        }
    }
}