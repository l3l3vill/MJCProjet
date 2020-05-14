package com.ideasfactory.mjcprojet.Fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.ideasfactory.mjcprojet.ContactUs
import com.ideasfactory.mjcprojet.MainActivity
import com.ideasfactory.mjcprojet.Model.User
import com.ideasfactory.mjcprojet.Model.UserDataSource
import com.ideasfactory.mjcprojet.Model.UserDataSourceModel
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentLoginBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(), View.OnClickListener, Observer {

    lateinit var binding : FragmentLoginBinding
    lateinit var navController: NavController
    lateinit var telephone : EditText
    lateinit var email : EditText
    lateinit var lastName : EditText
    lateinit var name : EditText
    lateinit var progressBar : ProgressBar
    lateinit var signUnUserButton : Button
    lateinit var password : EditText

    lateinit var auth :FirebaseAuth
    lateinit var userId : String
    lateinit var refDataBaseApp : DatabaseReference
    lateinit var refDataBaseDatasource : DatabaseReference
    lateinit var userIdSourceDB : String
    lateinit var listUser : MutableList<UserDataSource>
    lateinit var iHaveAccount : TextView
    //lateinit var changeTelephone : TextView
    var userAppAmountInit = ""
    lateinit var timeStamp : String
    private val TAG : String = "LOGIN_FRAGMENT"






    @SuppressLint("SimpleDateFormat")
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
        password = binding.etPassword
        iHaveAccount = binding.tvIHaveAccount
        timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())
        //changeTelephone = binding.changeTelephone

        iHaveAccount.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()

        listUser = mutableListOf()


        signUnUserButton.setOnClickListener(this)
        refDataBaseApp = FirebaseDatabase.getInstance().getReference("mjc_users_app")
        refDataBaseDatasource = FirebaseDatabase.getInstance().getReference("mjc_users_datasource")

        UserDataSourceModel
        UserDataSourceModel.addObserver(this)


        userIdSourceDB = refDataBaseDatasource.key!!



        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun signUpUser () {
        val telephoneInput: String = telephone.text.toString()
        val emailInput: String = email.text.toString()
        val lastNameInput: String = lastName.text.toString()
        val nameInput: String = name.text.toString()
        val passwordInput: String = password.text.toString()

        if (lastNameInput.isEmpty()) {
            lastName.error = "Entrez votre nom"
            lastName.requestFocus()
            return
        }


        if (nameInput.isEmpty()) {
            name.error = "Entrez votre prénom"
            name.requestFocus()
            return
        }



        if (telephoneInput.isEmpty() || telephoneInput.length != 10) {
            telephone.error = "Veuillez entrer un numéro de téléphone valide"
            telephone.requestFocus()
            return
        }

        if (emailInput.isEmpty()) {
            email.error = "Veuillez entrer une adresse email valide"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.error = "Veuillez entrer une adresse email valide"
            email.requestFocus()
            return
        }



        if (passwordInput.isEmpty() || passwordInput.length < 8) {
            password.error = "Veuillez entrer un mot de passe avec au moins 8 caractères"
            password.requestFocus()
            return
        }

        progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fbUser = auth.currentUser
                    userId = fbUser!!.uid


                    val user = User(
                        "",
                        "",
                        "",
                        userAppAmountInit,
                        "",
                        nameInput,
                        emailInput,
                        timeStamp,
                        lastNameInput,
                        telephoneInput,
                        "Android"
                    )
                    refDataBaseApp.child(userId).setValue(user).addOnCompleteListener {
                        //Toast.makeText(context, "RealTime DataBase User updated", Toast.LENGTH_SHORT).show();
                    }

                    val profileUpdates_name: UserProfileChangeRequest =
                        UserProfileChangeRequest.Builder().setDisplayName(nameInput)
                            .build()
                    fbUser?.updateProfile(profileUpdates_name)

                    //-------------------------
                    refDataBaseApp.child(userId).child("app_user_phone")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                Log.i(
                                    "LoginFRagment",
                                    " phone number APP data base $dataSnapshot"
                                )
                            }

                        })

                    //-----------------
                    fbUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                /*Toast.makeText(
                                    requireContext(), "Authentification reussie",
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        MainActivity::class.java
                                    )
                                )

                            }
                        }

                } else {

                    progressBar.visibility = View.INVISIBLE
                    /*Toast.makeText(
                            context,
                            "Authentification echouee",
                            Toast.LENGTH_SHORT
                        )
                        .show()
*/

                }

            }




        }

    //TODO -> READING ALL PHONE NUMBERS FROM APP_DATABASE
    //if numberExistInDataBase == true -> alert Dialog
    //else -> singUpUser method

    fun checkNumberRepeated(){
        val telephoneInput: String = telephone.text.toString()
        refDataBaseApp.addValueEventListener(object : ValueEventListener {
            var numberExistInDataBase = 0
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    //Log.i(TAG,"snapshot app_user_phone $snapshot")
                    var userPhoneAppDatabase = snapshot.child("app_user_phone").value
                    Log.i(TAG, "userPhoneAppDatabas $userPhoneAppDatabase")
                    if (telephoneInput == userPhoneAppDatabase) {
                        Log.i(TAG, "userPhoneInput $telephoneInput")
                        Log.i(TAG, "userPhoneAppDatabas $userPhoneAppDatabase")
                        numberExistInDataBase = numberExistInDataBase
                        Log.i(TAG, "numberExistInDataBase $numberExistInDataBase")

                    } else {
                        numberExistInDataBase = numberExistInDataBase + 1
                    }
                }

                if (numberExistInDataBase == 1) {
                    //alertDialogChangeTelephone()
                    Log.i(TAG, "Yes number exist $numberExistInDataBase")
                } else {
                    Log.i(TAG, "No number doesn't exist  $numberExistInDataBase")
                    signUpUser ()

                }
            }
        })

    }




    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_sign_up_user -> {
                //checkNumberExistInDataBase()
                VerificationUserExistense()
                signUpUser()

            }
            R.id.tv_i_have_account -> navController.navigate(R.id.action_loginFragment2_to_signInFragment)
        }
    }

    private fun VerificationUserExistense(){
        var userPhoneIntput = telephone.text.toString()

        //TODO 1. Listener data source for retreave value whit phone number
        refDataBaseDatasource.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var x = 0
                    for (snapshot in dataSnapshot.children) {
                        var userPhoneDataSoruce = snapshot.child("datasource_user_phone").value

                        if (userPhoneIntput != userPhoneDataSoruce) {
                            x = x //-> variable = 0 to phone different
                        } else {
                            x = x + 1 //-> variable = 1 to phone equal
                            userAppAmountInit =
                                snapshot.child("datasource_user_amount_init").value.toString()

                        }
              /*          if (x == 0) {
                            Log.i(
                                "LoginFragment",
                                "Votre numéro n’existe pas dans notre base de données. Veuillez contacter la MJC afin de mettre à jour vos données personnelles"
                            )
                            //Toast.makeText(context, "votre numero n'exites pas dans notre base donée", Toast.LENGTH_SHORT).show()
                            *//*val updateUserAmountInit = mapOf(
                                "app_user_amount" to "0.0"
                            )
                            refDataBaseApp.child(userId).updateChildren(updateUserAmountInit)
*//*
                            //alertDialogChangeTelephone()
                        }*/
                    }
                }

            }
            })
            }






    private fun alertDialogChangeTelephone(){
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_change_telephone,null)

        Log.i(TAG,"aqui viene el alert dialog")
        //Votre numéro de téléphone est déjà associé à un compte existant, veuillez vérifier votre email
        //val textChangeTelephone = view.findViewById<TextView>(R.id.change_telephone_dialog)
        builder.setView(view)
        builder.setNegativeButton("Annulerxxxxxxxxxxxxxxxx", DialogInterface.OnClickListener(){_, _ ->


        })
        builder.setPositiveButton("Envoyer un mail",DialogInterface.OnClickListener() { _ , _ ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("mailto: contact@mjclambres.fr")//contact@mjclambres.fr
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Demande de Mise à Jour du Numéro Téléphone Adhérent"
            )
            intent.putExtra(Intent.EXTRA_TEXT, "Bonjour,  Je demande la Mise à Jour du mon Numéro Téléphone")

            startActivity(intent)
        })

        builder.show()
        return
    }



    override fun update(o: Observable?, arg: Any?) {
        val data = UserDataSourceModel.getData()
        if(data!= null){
            Toast.makeText(context, "update $data", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        UserDataSourceModel.deleteObserver(this)
    }

    override fun onPause() {
        super.onPause()
        UserDataSourceModel.deleteObserver(this)
    }

    override fun onStop() {
        super.onStop()
        UserDataSourceModel.deleteObserver(this)
    }
}
