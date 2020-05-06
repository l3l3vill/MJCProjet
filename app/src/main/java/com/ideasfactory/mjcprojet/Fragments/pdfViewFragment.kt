package com.ideasfactory.mjcprojet.Fragments


import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentPdfViewBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class pdfViewFragment : Fragment() {



    lateinit var binding : FragmentPdfViewBinding
    lateinit var signAdvance : Button
    lateinit var navController: NavController
    lateinit var refDataBaseApp : DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var currentUser : FirebaseUser
    lateinit var userId : String
    lateinit var userName : TextView
    lateinit var userLastName : TextView
    lateinit var userPhone: TextView
    lateinit var userDon : TextView
    lateinit var userAvoir : TextView
    lateinit var date : TextView
    lateinit var timeStamp : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_pdf_view, container, false )
        refDataBaseApp = FirebaseDatabase.getInstance().getReference("mjc_users_app")
        signAdvance = binding.bttnSignAdvance
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        userId = currentUser.uid
        userName = binding.tvName
        userLastName = binding.tvLastName
        userAvoir = binding.tvAvoir
        userDon = binding.tvDonation
        userPhone = binding.tvPhone
        date = binding.tvDate
        timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())
        displayUserInformation()





        signAdvance.setOnClickListener {
            checkStoragePermitions()
            //navController.navigate(R.id.action_pdfViewFragment_to_aboutUs)
            val linearLayoutPdf = view?.findViewById<View>(R.id.linear_layout_pdf) as LinearLayout
            layoutToImage(linearLayoutPdf)
            sendEmail()
            //Toast.makeText(context, "pdf created", Toast.LENGTH_SHORT).show();
        }


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        return binding.root
    }

    fun displayUserInformation(){
        refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userName.text = dataSnapshot.child("app_user_first_name").value.toString()
                userLastName.text = dataSnapshot.child("app_user_name").value.toString()
                userPhone.text = dataSnapshot.child("app_user_phone").value.toString()
                userDon.text = dataSnapshot.child("app_user_amount_donation"). value.toString()
                userAvoir.text = dataSnapshot.child("app_user_amount_voucher").value.toString()
                date.text = dataSnapshot.child("app_user_action_date").value.toString()


            }

        })
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }

    fun checkStoragePermitions(){
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

    }

    var dirpath: String? = null
    fun layoutToImage(view: View) {
        // get view group using reference
        var linearLayoutPdf = view.findViewById<View>(R.id.linear_layout_pdf) as LinearLayout
        // convert view group to bitmap
        linearLayoutPdf.setDrawingCacheEnabled(true)
        linearLayoutPdf.buildDrawingCache()
        val bm: Bitmap = linearLayoutPdf.getDrawingCache()
        val share = Intent(Intent.ACTION_SEND)
        share.setType("image/jpeg")
        val bytes = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val f = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Avoir+$timeStamp.jpg"
        )
        try {
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun sendEmail() {
        val filename = "Avoir+$timeStamp.jpg"
        val filelocation = File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename)
        val path = Uri.fromFile(filelocation)
        val emailIntent = Intent(Intent.ACTION_SEND)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("contact@mjclambres.fr")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Transaction")
        startActivity(Intent.createChooser(emailIntent, "Transaction"))
    }

 /*   @Throws(FileNotFoundException::class)
    fun imageToPDF() {
        try {
            val document = DocumentsContract.Document()
            dirpath = Environment.getExternalStorageDirectory().toString()
            PdfWriter.getInstance(
                document,
                FileOutputStream("$dirpath/NewPDF.pdf")
            ) //  Change pdf's name.
            document.open()
            val img: Image = Image.getInstance(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "image.jpg"
            )
            val scaler: Float = (document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth() * 100
            img.scalePercent(scaler)
            img.setAlignment(Image.ALIGN_CENTER or Image.ALIGN_TOP)
            document.add(img)
            document.close()
            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
        }
    }*/


}
