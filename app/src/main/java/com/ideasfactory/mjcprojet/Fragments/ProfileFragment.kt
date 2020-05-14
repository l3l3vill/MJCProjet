package com.ideasfactory.mjcprojet.Fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ideasfactory.mjcprojet.LoginActivity
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentProfileBinding

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    lateinit var userLastName : TextView
    lateinit var userName : TextView
    lateinit var userPhone : TextView
    lateinit var userMail : TextView
    //lateinit var updateButton : Button
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private val TAG = "PROFILEFRAGMENT"
    lateinit var refDataBaseApp : DatabaseReference
    lateinit var userId : String
    lateinit var signOut : ImageView
    lateinit var linkLegalMentions : TextView
    lateinit var consultVoucher : TextView
    lateinit var navController: NavController
    lateinit var solde : String
    lateinit var refDataBaseDatasource : DatabaseReference
    lateinit var actionType : String



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
        //updateButton = binding.btnUpdate
        signOut = binding.signOut
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        linkLegalMentions = binding.linkLegalMentions
        consultVoucher = binding.consulter

        userId = currentUser.uid
        refDataBaseApp =  FirebaseDatabase.getInstance().getReference("mjc_users_app")
        refDataBaseDatasource = FirebaseDatabase.getInstance().getReference("mjc_users_datasource")


        displayUserInformation()

        userSold()

/*
        updateButton.setOnClickListener {
            //updateUserInformation()
            alertDialogUpdateName()
        }
*/


        userName.setOnClickListener {
            alertDialogUpdateName()
        }

        userLastName.setOnClickListener {
            alertDialogUpdateLastName()
        }

        signOut.setOnClickListener {
            auth.signOut()
            var intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        linkLegalMentions.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mjclambres.fr/article/liens-utiles/mentions-legales"))
            startActivity(intent)
        }

        consultVoucher.setOnClickListener {
            if (actionType.isEmpty()) {

                Toast.makeText(context, "Aucun document disponible. Veuillez vérifier les options dans votre espace Gestion.", Toast.LENGTH_SHORT).show();
            } else {
                navController.navigate(R.id.action_profileFragment2_to_pdfViewFragment)
            }
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


    fun userSold(){
        refDataBaseApp = FirebaseDatabase.getInstance().getReference("mjc_users_app")

        refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                solde = dataSnapshot.child("app_user_amount").value.toString()
                actionType = dataSnapshot.child("app_user_action_type").value.toString()


            }

        })
    }

    private fun alertDialogUpdateName(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Mettre à jour votre information")
        val view = layoutInflater.inflate(R.layout.dialog_update_user_information,null)
        val nameInputView = view.findViewById<EditText>(R.id.et_username)
        builder.setView(view)
        builder.setPositiveButton("Valider", DialogInterface.OnClickListener { _, _ ->
            updateUserName(nameInputView)

        })
        builder.setNegativeButton("Fermer", DialogInterface.OnClickListener { _, _ ->

        })
        builder.show()
    }

    private fun alertDialogUpdateLastName(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Mettre à jour votre information")
        val view = layoutInflater.inflate(R.layout.dialog_update_user_information,null)
        val lastNameInputView = view.findViewById<EditText>(R.id.et_username)
        lastNameInputView.setHint("Mettre à jour votre nom")
        builder.setView(view)
        builder.setPositiveButton("Valider", DialogInterface.OnClickListener { _, _ ->
            updateUserLastName(lastNameInputView)

        })
        builder.setNegativeButton("Fermer", DialogInterface.OnClickListener { _, _ ->

        })
        builder.show()
    }


    fun updateUserName(name: EditText){
        val nameInput = name.text.toString()

        if(nameInput.isEmpty()){
            name.error ="Metre à jour votre prenom"
            name.requestFocus()
            return
        }


        val update = mapOf(
            "app_user_first_name" to nameInput)


        refDataBaseApp.child(userId).updateChildren(update)


        userName.setText(nameInput)




    }

    fun updateUserLastName(lastName : EditText){
        val lastNameInput = lastName.text.toString()

        if(lastNameInput.isEmpty()){
            lastName.error = "Metre à jour votre nom"
            lastName.requestFocus()
            return
        }

        val update = mapOf(
            "app_user_name" to lastNameInput)


        refDataBaseApp.child(userId).updateChildren(update)

        userLastName.setText(lastNameInput)



    }




}
