package com.ideasfactory.mjcprojet.Fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentDonnationBinding
import kotlinx.android.synthetic.main.fragment_comunication.*

/**
 * A simple [Fragment] subclass.
 */
class donnationFragment : Fragment() {

    lateinit var binding : FragmentDonnationBinding
    lateinit var phoneAssociation : TextView
    lateinit var emailAssociation : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_donnation, container, false )
        phoneAssociation = binding.tvPhoneAssociation
        emailAssociation = binding.tvEmailAssociation


        phoneAssociation.setOnClickListener {
            call()
        }

        emailAssociation.setOnClickListener {
            sendEmail()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        return binding.root
    }

    fun call(){
        //Toast.makeText(context, "${comunication_telephone.text.toString()}", Toast.LENGTH_SHORT).show();
        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:0327871607")
        )

        startActivity(intent)
    }

    fun sendEmail(){
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("mailto: contact@mjclambres.fr")//contact@mjclambres.fr
        )
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Demande de Mise à Jour du Numéro Téléphone Adhérent"
        )
        intent.putExtra(Intent.EXTRA_TEXT, "Bonjour, blablabla")

        startActivity(intent)
    }


}
