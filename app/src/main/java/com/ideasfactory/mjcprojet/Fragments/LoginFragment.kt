package com.ideasfactory.mjcprojet.Fragments


import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ideasfactory.mjcprojet.MainActivity
import com.ideasfactory.mjcprojet.Model.User
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(), View.OnClickListener {

    lateinit var binding : FragmentLoginBinding
    lateinit var navController: NavController
    lateinit var telephone : EditText
    lateinit var email : EditText
    lateinit var lastName : EditText
    lateinit var name : EditText
    lateinit var progressBar : ProgressBar
    lateinit var signUnUserButton : Button
    lateinit var checkBox: CheckBox
    lateinit var password : EditText

    lateinit var auth :FirebaseAuth
    lateinit var userId : String
    lateinit var refDataBase : DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        telephone = binding.etPhone
        email = binding.etEmail
        lastName = binding.etLastname
        name = binding.etName
        progressBar = binding.progressSignIn
        signUnUserButton= binding.btnSignUpUser
        checkBox = binding.checkBox
        password = binding.etPassword
        auth = FirebaseAuth.getInstance()

        signUnUserButton.setOnClickListener(this)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun signUpUser (){
        val telephoneInput : String =telephone.text.toString()
        val emailInput : String = email.text.toString()
        val lastNameInput : String = lastName.text.toString()
        val nameInput : String = name.text.toString()
        val passwordInput : String = password.text.toString()

        if(nameInput.isEmpty()){
            name.error ="Entrez votre prenom"
            name.requestFocus()
            return
        }

        if(lastNameInput.isEmpty()){
            lastName.error = "Entrez votre nom"
            lastName.requestFocus()
            return
        }
        if(emailInput.isEmpty()){
            email.error = "Entrez votre email"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.error = "Entrez un email valide"
            email.requestFocus()
            return
        }

        if(telephoneInput.isEmpty()|| telephoneInput.length != 10){
            telephone.error = "Entrez un numéro de télephone portable valid"
            telephone.requestFocus()
            return
        }
        if(passwordInput.isEmpty() || passwordInput.length < 8 ){
            password.error = "Entrez un mot de passe avec au moins 8 caractères"
            password.requestFocus()
            return
        }


        if(!checkBox.isChecked){
            checkBox.error = "Acceptex les CGU"
            checkBox.requestFocus()
            return

            //TODO-> poner boton de ver condiciones de uso
        }




        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener { task ->
                if (task.isSuccessful ) {
                    val fbUser = auth.currentUser
                    userId = fbUser!!.uid
                    refDataBase = FirebaseDatabase.getInstance().getReference("mjc_users_app")

                    val user = User("",0,0,0,nameInput,emailInput,"",lastNameInput,telephoneInput)
                    refDataBase.child(userId).setValue(user).addOnCompleteListener {
                        Toast.makeText(context, "RealTime DataBase User updated", Toast.LENGTH_SHORT).show();
                    }

                    val profileUpdates_name : UserProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(nameInput).build()
                    fbUser?.updateProfile(profileUpdates_name)


                    fbUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(), "Authentification reussie",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                //navController.navigate(R.id.action_loginFragment_to_soldFragment)

                            }
                        }

                } else {

                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Authentification echouee", Toast.LENGTH_SHORT)
                        .show()
                }
            }


        //todo -> si el id del usuario ya existe en la base de datos de FirebaseAuthentication, mostrar un Toast diciendo "usuario existente -- olvidé contraseña"

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_sign_up_user -> signUpUser()
        }
    }




}
