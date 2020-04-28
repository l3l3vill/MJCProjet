package com.ideasfactory.mjcprojet.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentSoldBinding

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_make_a_don->{
                Toast.makeText(requireContext(), "Merci pour votre don!", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_soldFragment_to_donnationFragment)

                //TODO -> Enviar mail a las chicas con la información del usuario que recién hizo donación
            }
            R.id.tv_make_advance-> navController.navigate(R.id.action_soldFragment_to_advanceFragment)
            R.id.tv_make_repartition-> navController.navigate(R.id.action_soldFragment_to_distributeFragment)
        }
    }
}
