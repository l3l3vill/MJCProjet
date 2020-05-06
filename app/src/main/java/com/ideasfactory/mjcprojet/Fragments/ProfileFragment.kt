package com.ideasfactory.mjcprojet.Fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ideasfactory.mjcprojet.LoginActivity
import com.ideasfactory.mjcprojet.Model.User

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentProfileBinding
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    lateinit var userLastName : EditText
    lateinit var userName : EditText
    lateinit var userPhone : TextView
    lateinit var userMail : TextView
    lateinit var updateButton : Button
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private val TAG = "PROFILEFRAGMENT"
    lateinit var refDataBaseApp : DatabaseReference
    lateinit var userId : String
    lateinit var signOut : ImageView
    lateinit var linkLegalMentions : TextView
    lateinit var consultVoucher : TextView
    lateinit var navController: NavController




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
        updateButton = binding.btnUpdate
        signOut = binding.signOut
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        linkLegalMentions = binding.linkLegalMentions
        consultVoucher = binding.consulter

        userId = currentUser.uid
        refDataBaseApp =  FirebaseDatabase.getInstance().getReference("mjc_users_app")

        displayUserInformation()



        updateButton.setOnClickListener {
            updateUserInformation()
        }

        signOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        linkLegalMentions.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mjclambres.fr/article/liens-utiles/mentions-legales"))
            startActivity(intent)
        }

        consultVoucher.setOnClickListener {
            navController.navigate(R.id.action_profileFragment2_to_pdfViewFragment)
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    fun displayUserInformation(){
        refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userName.hint = dataSnapshot.child("app_user_first_name").value.toString()
                userLastName.hint = dataSnapshot.child("app_user_name").value.toString()
                userPhone.text = dataSnapshot.child("app_user_phone").value.toString()
                userMail.text = dataSnapshot.child("app_user_mail").value.toString()


            }

        })
    }

    fun updateUserInformation(){
        var nameInput : String = userName.getText().toString()
        var lastNameInput = userLastName.getText().toString()

        if(nameInput.isEmpty()){
            userName.error ="Metre à jour votre prenom"
            userName.requestFocus()
            return
        }

        if(lastNameInput.isEmpty()){
            userLastName.error = "Metre à jour votre nom"
            userLastName.requestFocus()
            return
        }

        val update = mapOf(
            "app_user_first_name" to nameInput ,
            "app_user_name" to lastNameInput)


        refDataBaseApp.child(userId).updateChildren(update)




    }



}
