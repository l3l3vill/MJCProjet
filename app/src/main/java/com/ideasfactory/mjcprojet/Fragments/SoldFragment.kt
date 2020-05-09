package com.ideasfactory.mjcprojet.Fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ideasfactory.mjcprojet.Model.User
import com.ideasfactory.mjcprojet.Model.UserDataSource

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentSoldBinding
import java.security.Timestamp
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class SoldFragment : Fragment(), View.OnClickListener {

    lateinit var binding : FragmentSoldBinding
    lateinit var navController: NavController
    lateinit var makeDon : TextView
    lateinit var makeAdvance : TextView
    lateinit var makeDistribution : TextView
    lateinit var solde : TextView


    lateinit var refDataBaseApp : DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase
    var timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())

    val TAG : String = "SOLDFRAGMENT"


    lateinit var auth : FirebaseAuth
    lateinit var userId : String
    var listUser = ArrayList<UserDataSource?>()
    /*var userPhone : String = ""*/
    var userSolde : String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sold, container, false)
        makeDon = binding.tvMakeADon
        makeAdvance = binding.tvMakeAdvance
        makeDistribution = binding.tvMakeRepartition
        solde = binding.tvSolde

        makeDon.setOnClickListener(this)
        makeAdvance.setOnClickListener(this)
        makeDistribution.setOnClickListener(this)
        firebaseDatabase = FirebaseDatabase.getInstance()

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        refDataBaseApp = FirebaseDatabase.getInstance().getReference("mjc_users_app")

        refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                solde.text = dataSnapshot.child("app_user_amount").value.toString()


            }

        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun alertDialogConfirmDon(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Donation")
        val view = layoutInflater.inflate(R.layout.dialog_confirmer_donation,null)
        val textDonation = view.findViewById<TextView>(R.id.tv_donation_dialog)
        //todo 1 ->concatenar con valores actuales de donación
        textDonation.setText("Veuillez confirmer votre don de la totalité de votre crédit de ${solde.text.toString()}€")//
        Log.i(TAG,"${solde.text}")
        builder.setView(view)
        builder.setPositiveButton("Valider donation", DialogInterface.OnClickListener { _, _ ->
            //todo 2 -> actualizar valores en base de datos
            val update = mapOf(
                "app_user_action_type" to "Donation" ,
                "app_user_action_date" to timeStamp.toString(),
                "app_user_amount_donation" to solde.text.toString() ,
                "app_user_amount_voucher" to "0.00",
                "app_user_amount" to "0.00")

            refDataBaseApp.child(userId).updateChildren(update)
            //todo 3 ->toast con validación
            //Toast.makeText(context, "Validation de donation", Toast.LENGTH_SHORT).show();
            //todo 4 -> navigate to thanks activity
            navController.navigate(R.id.action_soldFragment_to_donnationFragment)

        })
        builder.setNegativeButton("Non, je veux réfléchir", DialogInterface.OnClickListener { _, _ ->

        })
        builder.show()
    }

    private fun alertDialogConfirmAvoir(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Avoir")
        val view = layoutInflater.inflate(R.layout.dialog_confirmer_avoir,null)
        val textDonation = view.findViewById<TextView>(R.id.tv_avoir_dialog)
        //todo 1 ->concatenar con valores actuales de donación
        textDonation.setText("Veuillez confirmer votre avoir de la totalité de votre crédit de ${solde.text.toString()}€?")//
        Log.i(TAG,"${solde.text}")
        builder.setView(view)
        builder.setPositiveButton("Je demande mon avoir", DialogInterface.OnClickListener { _, _ ->
            //todo 2 -> actualizar valores en base de datos
            val update = mapOf(
                "app_user_action_type" to "Avoir" ,
                "app_user_action_date" to timeStamp.toString(),
                "app_user_amount_donation" to "0.00" ,
                "app_user_amount_voucher" to solde.text.toString(),
                "app_user_amount" to "0.00")

            refDataBaseApp.child(userId).updateChildren(update)
            //todo 3 ->toast con validación
            Toast.makeText(context, "Validation de avoir", Toast.LENGTH_SHORT).show();
            //todo 4 -> navigate to thanks activity
            navController.navigate(R.id.action_soldFragment_to_advanceFragment)

        })
        builder.setNegativeButton("Non, je veux réfléchir", DialogInterface.OnClickListener { _, _ ->

        })
        builder.show()
    }

    private fun alertDialogNoCredit(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Vous n’avez plus de crédit")
        val view = layoutInflater.inflate(R.layout.dialog_no_credit,null)
        //val textDonation = view.findViewById<TextView>(R.id.tv_avoir_dialog)
        //todo 1 ->concatenar con valores actuales de donación
        //textDonation.setText("voudrais vouz confirmer votre donation de la totalité de vote sold ${solde.text.toString()}€?")//

        builder.setView(view)

        builder.show()
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_make_a_don->{
                Log.i(TAG,"SOLDE FROM FIREBASE ${solde.text.toString()}")
                if(solde.text.toString().toDouble()== 0.00){
                    alertDialogNoCredit()
                }else{
                    alertDialogConfirmDon()
                }

            }
            R.id.tv_make_advance-> {
                if(solde.text.toString().toDouble()== 0.00){
                    alertDialogNoCredit()
                }else{
                    alertDialogConfirmAvoir()
                }

            }
            R.id.tv_make_repartition->{
                if(solde.text.toString().toDouble()== 0.00){
                    alertDialogNoCredit()
                }else{ navController.navigate(R.id.action_soldFragment_to_distributeFragment)
            } }

        }
    }
}
