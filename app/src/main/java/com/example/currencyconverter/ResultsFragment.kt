package com.example.currencyconverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.currencyconverter.databinding.ResultsFragmentBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultsFragment : Fragment() {

    private var _binding: ResultsFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var convertResult = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val result = arguments?.getSerializable("result").toString();
        val gson = Gson()

        val resultString: String = gson.fromJson(result, object : TypeToken<String>() {}.type)
        convertResult = resultString
        _binding = ResultsFragmentBinding.inflate(inflater, container, false)

        binding.resultTextview.text = "Converted result: $resultString"
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_ResultsFragment_to_SelectCurrencyFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}