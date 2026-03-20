package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment: Fragment() {
    private val viewModel by viewModel<SettingsViewModel>()
    private var suppressThemeListener = false
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel?.observerIsTheme()?.observe(viewLifecycleOwner) {
            suppressThemeListener = true
            binding.themeSwitcher.isChecked = it
            suppressThemeListener = false
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            if (suppressThemeListener) return@setOnCheckedChangeListener
            viewModel?.setTheme(checked)
        }

        binding.share.setOnClickListener {
            viewModel?.shareApp()
        }

        binding.support.setOnClickListener {
            viewModel?.openSupport()
        }

        binding.contract.setOnClickListener {
            viewModel?.openTerms()
        }
    }
}