package com.ideasfactory.mjcprojet.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentAdvanceBinding

/**
 * A simple [Fragment] subclass.
 */
class advanceFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentAdvanceBinding
    lateinit var navController: NavController
    lateinit var advancedButton : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_advance, container, false)
        advancedButton = binding.btnConfirmAdvance

        advancedButton.setOnClickListener(this)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_confirm_advance -> {
                navController.navigate(R.id.action_advanceFragment_to_pdfViewFragment)
            }
        }
    }
}
