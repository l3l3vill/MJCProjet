package com.ideasfactory.mjcprojet.Fragments


import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentPdfViewBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class pdfViewFragment : Fragment() {

    //TODO -> implementar pdf editable con nombre usuario

    lateinit var binding : FragmentPdfViewBinding
    lateinit var signAdvance : Button
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pdf_view, container, false )






        signAdvance = binding.bttnSignAdvance

        if (ActivityCompat.checkSelfPermission(
                context!!,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context!!,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                activity!!, arrayOf(
                    WRITE_EXTERNAL_STORAGE
                ), PackageManager.PERMISSION_GRANTED
            )
        } else {
            Log.e("DB", "PERMISSION GRANTED")
        }

        signAdvance.setOnClickListener {
            //navController.navigate(R.id.action_pdfViewFragment_to_aboutUs)

            createPDF()
        }

        return binding.root
    }

    private fun createPDF() {

            var pdfDocument : PdfDocument = PdfDocument()
            var paint : Paint = Paint()

            var mPageInfo1 = PdfDocument.PageInfo.Builder(250,400, 1).create()
            var mPage1 = pdfDocument.startPage(mPageInfo1)

            var canvas = mPage1.canvas
            canvas.drawText("",40F,50F,paint)

            pdfDocument.finishPage(mPage1)

            var file : File = File(Environment.getExternalStorageDirectory(),"/Avoir.pdf")

            try{
                pdfDocument.writeTo(FileOutputStream(file))
            }catch (e: IOException){
                e.printStackTrace()
            }

            pdfDocument.close()




    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }


}
