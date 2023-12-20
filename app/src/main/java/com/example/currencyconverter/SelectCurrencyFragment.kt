package com.example.currencyconverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter.api.OpenExchangeRatesApi
import com.example.currencyconverter.api.models.Currency
import com.example.currencyconverter.databinding.SelectCurrencyFragmentBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectCurrencyFragment : Fragment() {

    private var _binding: SelectCurrencyFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SelectCurrencyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val api = Retrofit.Builder()
        .baseUrl("https://openexchangerates.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenExchangeRatesApi::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.convertButton.setOnClickListener {
            if (checkFields()) {
                val request = api.getLatestRates("ee897f284c7344e183c9935994fa8e53")
                request.enqueue(object : Callback<Currency> {
                    override fun onResponse(call: Call<Currency>, response: Response<Currency>) {
                        if (response.isSuccessful) {
                            val currencyResult = convertCurrency(
                                response.body()?.rates,
                                getCurrencyFromSpinner(binding.sourceSpinner),
                                getCurrencyFromSpinner(binding.destinationSpinner),
                                binding.amountEditText.text.toString().toDouble()
                            );
                            val gson = Gson();
                            val json = gson.toJson(currencyResult)
                            findNavController().navigate(R.id.action_SelectCurrencyFragment_to_ResultsFragment,
                                Bundle().apply { putSerializable("result", json) })
                        } else {
                            showToast("Something went wrong with connection :(")
                        }
                    }

                    override fun onFailure(call: Call<Currency>, t: Throwable) {
                        showToast("Something went wrong with connection :(")
                    }
                })
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val baseRateName = "USD"

    private fun convertCurrency(
        rates: Map<String, Double>?,
        source: String,
        destination: String,
        amount: Double
    ): String {
        val sourceToUsdRate =
            rates?.get(source) ?: throw RuntimeException("Undefined value: $source")
        val destinationToUsdRate =
            rates[destination] ?: throw RuntimeException("Undefined value: $destination")

        if (source == baseRateName) {
            return formatResult(amount * destinationToUsdRate, destination);
        }

        if (destination == baseRateName) {
            return formatResult(amount / sourceToUsdRate, destination)
        }
        return formatResult(amount * (destinationToUsdRate / sourceToUsdRate), destination)
    }

    private fun formatResult(amount: Double, currency: String): String {
        return "$amount $currency"
    }

    private fun getCurrencyFromSpinner(spinner: Spinner): String {
        val value = spinner.getItemAtPosition(spinner.selectedItemPosition)
        return value.toString()
    }

    private fun checkFields(): Boolean {
        val text = binding.amountEditText.text.toString()
        if (text == "0" || text == "") {
            showToast("Please, type correct amount to convert!")
            return false;
        }

        return true;
    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}