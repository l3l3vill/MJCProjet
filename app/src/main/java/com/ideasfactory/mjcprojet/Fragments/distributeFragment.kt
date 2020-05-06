package com.ideasfactory.mjcprojet.Fragments


import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentDistributeBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class distributeFragment : Fragment(), View.OnClickListener {

    lateinit var binding : FragmentDistributeBinding
    lateinit var navController: NavController
    lateinit var etDon : EditText
    lateinit var etAdvance : TextView
    lateinit var buttonValidate : Button
    lateinit var buttonCalculate : TextView
    lateinit var soldeToDistribute : TextView
    lateinit var refDataBaseApp :DatabaseReference
    lateinit var auth : FirebaseAuth
    lateinit var userId : String
    lateinit var euro : TextView
    var timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())
    var buttonCalculateClicked : Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_distribute, container, false)
        etDon = binding.etSoldeToDon
        etAdvance = binding.etSoldeToAvoir
        buttonCalculate = binding.btnCalculer
        buttonValidate = binding.btnValiderDon
        soldeToDistribute = binding.tvSoldeToDistribute
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        refDataBaseApp = FirebaseDatabase.getInstance().getReference("mjc_users_app")
        euro = binding.euroSoldeToAvoir

        buttonValidate.setOnClickListener(this)
        buttonCalculate.setOnClickListener(this)

        setUserSolde()



        return binding.root
    }


    fun setUserSolde(){
        refDataBaseApp.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                soldeToDistribute.text = dataSnapshot.child("app_user_amount").value.toString()
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    fun setingSoldeToDon (){

        val soldeInput = soldeToDistribute.text.toString().toDouble()
        Log.i("DISTRIBUTE FRAGMENT","amount to distribute $soldeInput")

        if(etDon.text.isEmpty()){
            etDon.error = "donation invalid"
            etDon.requestFocus()
            return
        }
        val soldeToDonInput = etDon.text.toString().toDouble()
        Log.i("DISTRIBUTE FRAGMENT"," amount to don $soldeToDonInput")

        var soldeToAdvanceInput = soldeInput  - soldeToDonInput


        if(soldeToDonInput> soldeInput|| soldeToDonInput < 0){
            etDon.error = "Montant supérieur à votre crédit"
            etDon.requestFocus()
            return
        }else{

            Log.i("DISTRIBUTE FRAGMENT", "$soldeToAdvanceInput")
            etAdvance.setText(soldeToAdvanceInput.toString())
        }

    }

    private fun alertDialogConfirmDon(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Repartition")
        val view = layoutInflater.inflate(R.layout.dialog_confirmer_desition,null)
        val textDesition = view.findViewById<TextView>(R.id.tv_desition)
        //todo 1 ->concatenar con valores actuales de donación
        textDesition.setText("Voudrais vouz confirmer votre donation de ${etDon.text}€ et votre avoir de ${etAdvance.text}€?")
        builder.setView(view)
        builder.setPositiveButton("Je valide cette répartition solidarie", DialogInterface.OnClickListener { _, _ ->
            //todo 2 -> actualizar valores en base de datos
            val update = mapOf(
                "app_user_action_type" to "Repartition" ,
                "app_user_action_date" to timeStamp.toString(),
                "app_user_amount_donation" to etDon.text.toString().toDouble().toString() ,
                "app_user_amount_voucher" to etAdvance.text.toString().toDouble().toString(),
                "app_user_amount" to "0.0")

            refDataBaseApp.child(userId).updateChildren(update)

            //todo 3 ->toast con validación
            Toast.makeText(context, "Validation de repartition", Toast.LENGTH_SHORT).show();
            //todo 4 -> navigate to pdf activity
            navController.navigate(R.id.action_distributeFragment_to_distributedConfirmPfFragment)

        })
        builder.setNegativeButton("Non, je veux réfléchir", DialogInterface.OnClickListener { _, _ ->
           etDon.setHint("0.00")
           etAdvance.setText("0.00")
        })
        builder.show()
    }



    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.btn_valider_don -> {

                verifications()
            }
            R.id.btn_calculer -> {
                buttonCalculateClicked = true
                setingSoldeToDon()
            }

        }

    }

    fun verifications(){

        //todo-> verification button calculation clicked
        val donToConfirm = etDon.text.toString()
        val avoirToConfirm = etAdvance.text.toString().toDouble()
        val solde = soldeToDistribute.text.toString().toDouble()
        if(buttonCalculateClicked == false || donToConfirm.isEmpty() || avoirToConfirm!= solde - donToConfirm.toDouble() ){
            etDon.error //= "click check pour calculer"
            Toast.makeText(context, "donation invalid , click check pour calculer", Toast.LENGTH_SHORT).show()
            //etDon.requestFocus()
            //etDon.clearFocus()
            buttonEffect(buttonCalculate)

            return
        }else{
            alertDialogConfirmDon()
        }

        //todo -> verification amount of avoir is correct
     /*   val avoirToConfirm = etAdvance.text.toString().toDouble()
        val solde = soldeToDistribute.text.toString().toDouble()

        if(avoirToConfirm!= solde - donToConfirm.toDouble() ){
            etDon.error = "donation invalid , click check pour calculer"
            etDon.requestFocus()
            //Toast.makeText(context, "donation invalid , click check pour calculer", Toast.LENGTH_SHORT).show();
            return
        }*/
    }

    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(R.color.colorPrimaryDark.toInt(), PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }


}
