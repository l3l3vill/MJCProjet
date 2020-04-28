package com.ideasfactory.mjcprojet.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentDistributeBinding

/**
 * A simple [Fragment] subclass.
 */
class distributeFragment : Fragment(), View.OnClickListener {

    lateinit var binding : FragmentDistributeBinding
    lateinit var navController: NavController
    lateinit var etDon : EditText
    lateinit var etAdvance : TextView
    lateinit var buttonValidateDon : Button
    lateinit var buttonValidateAdvance : Button
    lateinit var soldeToDistribute : TextView

    //TODO -> traer con un bundle la información del saldo del adheridp... o traer de la nubetambien puede ser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_distribute, container, false)
        etDon = binding.etSoldeToDon
        etAdvance = binding.etSoldeToAvoir
        buttonValidateAdvance = binding.btnValiderAvoir
        buttonValidateDon = binding.btnValiderDon
        soldeToDistribute = binding.tvSoldeToDistribute

        buttonValidateDon.setOnClickListener(this)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    fun setingSoldeToDon (){
        val soldeInput = Integer.parseInt(soldeToDistribute.text.toString())
        Log.i("DISTRIBUTE FRAGMENT","$soldeInput")

        val soldeToDonInput = Integer.parseInt(etDon.text.toString())
        Log.i("DISTRIBUTE FRAGMENT","$soldeToDonInput")
        var soldeToAdvanceInput = soldeInput  - soldeToDonInput
        Log.i("DISTRIBUTE FRAGMENT", "$soldeToAdvanceInput")

        //TODO-> TENGO UN CRASH CUANDO EL STRING ES VACÍO.... -> NumberFormatException: For input string: ""
        if(etDon.text.toString().isEmpty()||soldeToDonInput> soldeInput|| soldeToDonInput < 0){
            etDon.error = "donation invalid"
            etDon.requestFocus()
            return
        }else{
            etAdvance.setText(soldeToAdvanceInput.toString())
        }

    }



    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_valider_don -> setingSoldeToDon()

        }
    }


}
