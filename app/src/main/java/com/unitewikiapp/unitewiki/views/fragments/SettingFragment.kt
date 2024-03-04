package com.unitewikiapp.unitewiki.views.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.unitewikiapp.unitewiki.MainActivity
import com.unitewikiapp.unitewiki.databinding.FragmentSettingBinding
import com.unitewikiapp.unitewiki.utils.PreferenceManager
import com.unitewikiapp.unitewiki.viewmodels.LoginViewModel
import com.unitewikiapp.unitewiki.views.dialogfragments.LanguageDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SettingFragment : Fragment(), LanguageDialogFragment.OnLanguageSelected {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val languageDialog = LanguageDialogFragment()
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageDialog.setListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        binding.apply {
            loginViewModel.currentUser.observe(viewLifecycleOwner){
                when(it){
                    null -> isSignedIn = false
                    else -> {isSignedIn = true
                            viewModel = loginViewModel}
                }
            }
            btnLanguage.setOnClickListener {
                languageDialog.show(requireActivity().supportFragmentManager,"SelectLanguage")
            }
            btnLanguageWhenlogout.setOnClickListener {
                languageDialog.show(requireActivity().supportFragmentManager,"SelectLanguage")
            }
            btnLogout.setOnClickListener {
                loginViewModel.getAuthUI().signOut(requireActivity())
            }
            btnLoginwithother.setOnClickListener {
                loginViewModel.getAuthUI().signOut(requireActivity()).addOnCompleteListener {
                    loginViewModel.signIn(requireActivity())
                }
            }
            btnLogin.setOnClickListener {
                loginViewModel.signIn(requireActivity())
            }
            navigateBackbtn.setOnClickListener {
                it.findNavController().popBackStack()
            }
            btnBussiness.setOnClickListener {
                clicked = !(clicked)
            }
            btnBussinessWhenlogout.setOnClickListener {
                clicked = !(clicked)
            }
        }
        return binding.root
    }

    override fun onEnglishSelected() {
        setLocale("en")
        languageDialog.dismiss()
        requireActivity().finish()
        val i = Intent(activity,MainActivity::class.java)
        startActivity(i)
    }

    override fun onKoreanSelected() {
        setLocale("ko_KR")
        languageDialog.dismiss()
        requireActivity().finish()
        val i = Intent(activity,MainActivity::class.java)
        startActivity(i)
    }

    private fun setLocale(country:String){
        val PreferenceManager = PreferenceManager()
        val locale = Locale(country)
        Locale.setDefault(locale)
        val config: Configuration = requireActivity().resources.configuration
        config.setLocale(locale)
        PreferenceManager.setString(requireActivity(),"locale",country)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}