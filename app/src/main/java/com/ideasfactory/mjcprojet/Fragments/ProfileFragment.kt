package com.ideasfactory.mjcprojet.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentProfileBinding

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    lateinit var userLastName : EditText
    lateinit var userName : EditText
    lateinit var userPhone : EditText
    lateinit var userMail : TextView
    lateinit var userNumber : EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private val TAG = "PROFILEFRAGMENT"
    lateinit var refDataBaseApp : DatabaseReference
    lateinit var refDataBaseDatasource : DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false )
        userLastName = binding.etLastname
        userName = binding.etName
        userPhone = binding.etPhone
        userMail = binding.etEmail
        userNumber = binding.etAdherentNumber

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!


        return binding.root
    }




}
