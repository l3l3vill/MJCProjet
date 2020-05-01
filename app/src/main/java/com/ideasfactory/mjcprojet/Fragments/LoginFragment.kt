package com.ideasfactory.mjcprojet.Fragments


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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.ideasfactory.mjcprojet.MainActivity
import com.ideasfactory.mjcprojet.Model.User
import com.ideasfactory.mjcprojet.Model.UserDataSource
import com.ideasfactory.mjcprojet.Model.UserDataSourceModel
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentLoginBinding
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
    lateinit var forgotPassWord : TextView
    //lateinit var changeTelephone : TextView
    var userAppAmountInit = ""



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
        forgotPassWord = binding.forgotPassword
        //changeTelephone = binding.changeTelephone
        auth = FirebaseAuth.getInstance()

        listUser = mutableListOf()


        signUnUserButton.setOnClickListener(this)

        forgotPassWord.setOnClickListener {
            alertDialogForgotPassword()
        }
/*
        changeTelephone.setOnClickListener{
            Toast.makeText(context, "contactez nous pour valider votre numéro de téléphone", Toast.LENGTH_SHORT).show();
        }*/


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

        if(telephoneInput.isEmpty()|| telephoneInput.length != 10){
            telephone.error = "Entrez un numéro de télephone portable valid"
            telephone.requestFocus()
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



        if(passwordInput.isEmpty() || passwordInput.length < 8 ){
            password.error = "Entrez un mot de passe avec au moins 8 caractères"
            password.requestFocus()
            return
        }



//Listen the reference of database "data_source" to see all the data in it.
        //the data that we need to find is phone number

        refDataBaseDatasource.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("LoginFragment", "DataSnapShot ${dataSnapshot.children}")
                if (dataSnapshot.exists()) {
                    listUser.clear()
                    var x = 0 //-> variable to save the state (0 = phone different , 1 = phone equal)

                    for (snapshot in dataSnapshot.children) {
                        //Snapshot represents each element in database
                        Log.i("LoginFragment", "SnapShot = each firebase user $snapshot")
                        //We need the value of the child "phone_number" of each snapshot
                        var userPhoneSourceDB = snapshot.child("datasource_user_phone").value
                        Log.i("LoginFragment", "user phone ${userPhoneSourceDB.toString()}")
                        //we compare each phone of the database whith the typed number.

                        if (telephoneInput != userPhoneSourceDB) {
                            x = x //-> variable = 0 to phone different
                        } else {
                            x = x +1 //-> variable = 1 to phone equal
                            userAppAmountInit = snapshot.child("datasource_user_amount_init").value.toString()

                        }
                    }
                    if(x == 0){
                        Log.i("LoginFragment", "votre numero n'exites pas dans notre base donée")
                        //Toast.makeText(context, "votre numero n'exites pas dans notre base donée", Toast.LENGTH_SHORT).show()
                        alertDialogChangeTelephone()
                    }else{
                        Log.i("LoginFragment", "votre numero exists dans notre base donée")
                        //Toast.makeText(context, "votre numero exites dans notre base donée", Toast.LENGTH_SHORT).show();

                        //-------------------------------PONER TODO ESTO EN UN MÉTODO: CONECCIÓN CON EMAIL Y PASWORD--------------------------------------
                        //conectionEmailPasword (email : String , pasword: String)
                        progressBar.visibility = View.VISIBLE

                        auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful ) {
                                    val fbUser = auth.currentUser
                                    userId = fbUser!!.uid


                                    val user = User("","",userAppAmountInit,"",nameInput,emailInput,"",lastNameInput,telephoneInput,"Android")
                                    refDataBaseApp.child(userId).setValue(user).addOnCompleteListener {
                                        //Toast.makeText(context, "RealTime DataBase User updated", Toast.LENGTH_SHORT).show();
                                    }

                                    val profileUpdates_name : UserProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(nameInput).build()
                                    fbUser?.updateProfile(profileUpdates_name)

                                    //-------------------------
                                    refDataBaseApp.child(userId).child("app_user_phone").addValueEventListener(object : ValueEventListener{
                                        override fun onCancelled(p0: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            Log.i("LoginFRagment", " phone number APP data base $dataSnapshot")
                                        }

                                    })

                                    //-----------------
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


                }
            }


        })





    }

    fun authWithMailAndPassword(password: String, email : String){

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_sign_up_user -> signUpUser()
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

    private fun alertDialogChangeTelephone(){
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_change_telephone,null)
        //val textChangeTelephone = view.findViewById<TextView>(R.id.change_telephone_dialog)
        builder.setView(view)
        builder.setNegativeButton("Annuer", DialogInterface.OnClickListener(){_, _ ->


        })
        builder.setPositiveButton("Envoyer un mail",DialogInterface.OnClickListener() { _ , _ ->
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("mailto: lg.villamil.guerrero@gmail.com")//contact@mjclambres.fr
            )
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Demande de Mise à Jour du Numéro Téléphone Adhérent"
            )
            intent.putExtra(Intent.EXTRA_TEXT, "Bonjour, blablabla")

            startActivity(intent)
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
                }
            }


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
