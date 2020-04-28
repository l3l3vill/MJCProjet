package com.ideasfactory.mjcprojet.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentComunicationBinding

/**
 * A simple [Fragment] subclass.
 */
class comunicationFragment : Fragment(), View.OnClickListener {

    lateinit var binding : FragmentComunicationBinding
    lateinit var navController : NavController

    lateinit var buttonNext : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_comunication, container, false)

        buttonNext = binding.btnSignIn

        buttonNext.setOnClickListener(this)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController =Navigation.findNavController(view)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_sign_in -> navController.navigate(R.id.action_comunicationFragment2_to_loginFragment2)
        }
    }


}
