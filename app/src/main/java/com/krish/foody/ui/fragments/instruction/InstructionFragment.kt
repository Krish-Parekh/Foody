package com.krish.foody.ui.fragments.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.krish.foody.databinding.FragmentInstructionBinding
import com.krish.foody.models.Result
import com.krish.foody.util.Constants
import java.util.*


class InstructionFragment : Fragment() {

    private lateinit var binding: FragmentInstructionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInstructionBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result = args!!.getParcelable<Result>(Constants.RECIPE_RESULT_KEY) as Result

        binding.instructionWebView.webViewClient = object : WebViewClient(){}
        binding.instructionWebView.loadUrl(myBundle.sourceUrl)

        return binding.root
    }
}