package com.ideasfactory.mjcprojet.Fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
    lateinit var refDataBaseDatasource : DatabaseReference
    var timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())
    lateinit var userPhoneIntput : String
    lateinit var soldeDataSoruce : String

    val TAG : String = "SOLDFRAGMENT"


    lateinit var auth : FirebaseAuth
    lateinit var userId : String
    var listUser = ArrayList<UserDataSource?>()
    /*var userPhone : String = ""*/
    var userSolde : String = "0.0"


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

        userPhoneIntput = ""


        refDataBaseApp = FirebaseDatabase.getInstance().getReference("mjc_users_app")
        refDataBaseDatasource = FirebaseDatabase.getInstance().getReference("mjc_users_datasource")

        //TODO 0. retreave UserPhone
        refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userPhoneIntput = dataSnapshot.child("app_user_phone").value.toString()
                Log.i("SOLDFRAGENT", "user Phone $userPhoneIntput")

                //TODO 1. Listener data source for retreave value whit phone number
                refDataBaseDatasource.addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()){
                            var x = 0
                            for(snapshot in dataSnapshot.children){
                                var userPhoneDataSoruce= snapshot.child("datasource_user_phone").value
                                var user_first_conect = snapshot.child("datasource_user_first_connect").value
                                if(userPhoneIntput != userPhoneDataSoruce  ){//&& user_first_conect != true
                                    x = x

                                }else{
                                    x = 1
                                    soldeDataSoruce = snapshot.child("datasource_user_amount_init").value.toString()
                                    //TODO 2. Update app_user with amount
                                    val updateUserAmountInit = mapOf(
                                        "app_user_amount" to soldeDataSoruce)
                                    refDataBaseApp.child(userId).updateChildren(updateUserAmountInit)

                                    //TODO 3. Set New amount to the view
                                    refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
                                        override fun onCancelled(p0: DatabaseError) {
                                        }

                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            userSolde = dataSnapshot.child("app_user_amount").value.toString()
                                            Log.i("SOLDFRAGENT" ,"user solde $userSolde")
                                            solde.text = userSolde
                                        }

                                    })
                                    //TODO 4. set firts connect to false in datasource
                                    //val updateUserFirstConnectInDataSource = mapOf(
                                      //  "datasource_user_first_connect" to "false")
                                    //refDataBaseDatasource.child("datasource_user_first_connect").updateChildren(updateUserFirstConnectInDataSource)


                                }
                            }
                            if(x == 0){
                                Log.i("LoginFragment", "Votre numéro n’existe pas dans notre base de données. Veuillez contacter la MJC afin de mettre à jour vos données personnelles")
                                //Toast.makeText(context, "votre numero n'exites pas dans notre base donée", Toast.LENGTH_SHORT).show()
                                val updateUserAmountInit = mapOf(
                                    "app_user_amount" to "0.0")
                                refDataBaseApp.child(userId).updateChildren(updateUserAmountInit)
                                solde.text = "0.0"
                                alertDialogChangeTelephone()
                            }
                        }
                    }

                })
            }



        })

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun alertDialogChangeTelephone(){
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_change_telephone,null)
        //val textChangeTelephone = view.findViewById<TextView>(R.id.change_telephone_dialog)
        builder.setView(view)
        builder.setNegativeButton("Annuler", DialogInterface.OnClickListener(){_, _ ->

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
