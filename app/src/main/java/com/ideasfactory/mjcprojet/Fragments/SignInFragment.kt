package com.ideasfactory.mjcprojet.Fragments


import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.ideasfactory.mjcprojet.MainActivity

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentSignInBinding

/**
 * A simple [Fragment] subclass.
 */
class SignInFragment : Fragment() {

    lateinit var binding : FragmentSignInBinding
    lateinit var email: EditText
    lateinit var password : EditText
    lateinit var forgotPassWord : TextView
    lateinit var auth : FirebaseAuth
    lateinit var userId : String
    lateinit var refDataBaseApp : DatabaseReference
    lateinit var progressBar : ProgressBar
    lateinit var sigInButton : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in, container, false )
        forgotPassWord = binding.forgotPassword
        email = binding.etEmail
        password= binding.etPassword
        auth = FirebaseAuth.getInstance()
        progressBar = binding.progressSignIn
        sigInButton = binding.btnSignUpUser

        forgotPassWord.setOnClickListener {
            alertDialogForgotPassword()
        }

        sigInButton.setOnClickListener {
            sigInUser()
        }


        return binding.root
    }

    private fun sigInUser(){
        val emailIntput: String = email.text.toString()
        val passwordInput : String = password.text.toString()




        if (emailIntput.isEmpty()) {
            email.error = "Entrez votre email"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailIntput).matches()) {
            email.error = "Entrez un email valide"
            email.requestFocus()
            return

        }

        if(passwordInput.isEmpty() || passwordInput.length < 8 ){
            password.error = "Entrez votre mot de passe"
            password.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(), "Authentification reussie",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))

                }else{
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Authentification echouee, virifiez votre e-mail ou votre mot de passe", Toast.LENGTH_SHORT).show()

                }
            }


    }




    private fun alertDialogForgotPassword(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("J'ai oublié mon mot de passe")
        val view = layoutInflater.inflate(R.layout.dialog_forgot_pasword,null)
        val emailInput = view.findViewById<EditText>(R.id.et_username)
        builder.setView(view)
        builder.setPositiveButton("Valider", DialogInterface.OnClickListener { _, _ ->
            forgotPassword(emailInput)
        })
        builder.setNegativeButton("Fermer", DialogInterface.OnClickListener { _, _ ->

        })
        builder.show()
    }

    private fun forgotPassword(email : EditText){
        val emailIntput: String = email.text.toString()
        if(emailIntput.isEmpty()){
            email.error = "Entrez votre email"
            email.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailIntput).matches()){
            email.error = "Entrez un email valide"
            email.requestFocus()
            return
        }

        auth.sendPasswordResetEmail(emailIntput)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "Email envoyé")
                    Toast.makeText(requireContext(), "Email envoyé", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "Vous n'exites pas dans notre basse donée", Toast.LENGTH_SHORT).show();
                }
            }


    }
}
