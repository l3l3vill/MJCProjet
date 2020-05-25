package com.ideasfactory.mjcprojet.Fragments


import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
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
import com.ideasfactory.mjcprojet.AppExecutors
import com.ideasfactory.mjcprojet.R
import com.ideasfactory.mjcprojet.databinding.FragmentPdfViewBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.activation.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


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
    lateinit var appExecutors: AppExecutors

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


        checkStoragePermitions()


        signAdvance.setOnClickListener {

            //navController.navigate(R.id.action_pdfViewFragment_to_aboutUs)
            val linearLayoutPdf = view?.findViewById<View>(R.id.linear_layout_pdf) as LinearLayout
            layoutToImage(linearLayoutPdf)
            //sendEmail()
            shareInEmail()
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

    override fun onAttach(context: Context) {
        appExecutors = AppExecutors()
        super.onAttach(context)
    }

    fun displayUserInformation(){
        refDataBaseApp.child(userId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userName.text = dataSnapshot.child("app_user_first_name").value.toString()
                userLastName.text = dataSnapshot.child("app_user_name").value.toString()
                userPhone.text = dataSnapshot.child("app_user_phone").value.toString()
                userDon.text = "${dataSnapshot.child("app_user_amount_donation").value.toString()} €"
                userAvoir.text = "${dataSnapshot.child("app_user_amount_voucher").value.toString()} €"
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

        //-----------------------------Create a PdfDocument with a page of the same size as the image----------------
       /* val document: PdfDocument = PdfDocument()
        val pageInfo: PdfDocument.PageInfo  = PdfDocument.PageInfo.Builder(bm.width, bm.height, 1).create()
        val page: PdfDocument.Page  = document.startPage(pageInfo)

        // Draw the bitmap onto the page
        val canvas: Canvas = page.canvas
        canvas.drawBitmap(bm, 0f, 0f, null)
        document.finishPage(page)

        // Write the PDF file to a file
        val directoryPath: String  = android.os.Environment.getExternalStorageDirectory().toString()
        document.writeTo( FileOutputStream(directoryPath + File.separator + "Avoir+$timeStamp.pdf"))
        document.close()*/


    }




    private fun shareInEmail() {
        //val filename = "Avoir+$timeStamp.pdf"//Avoir+$timeStamp.jpg
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


    private fun sendEmail(){
        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session =  Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
                })

            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                val emailId = currentUser.email//emailEditText.text.toString()
                //Setting sender address
                mm.setFrom(InternetAddress(Credentials.EMAIL))
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO,
                    InternetAddress(emailId))
                //Adding subject
                mm.subject = "MJC validation Avoir"

                // Create the message part
                var messageBodyPart: BodyPart = MimeBodyPart()

                // Fill the message
                messageBodyPart.setText("This is message body")

                // Create a multipar message
                val multipart: Multipart = MimeMultipart()
                multipart.addBodyPart(messageBodyPart)

                //File location to attach
                val filename = "Avoir+$timeStamp.pdf"//Avoir+$timeStamp.jpg
                val filelocation = File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename)

                // Part two is attachment
                messageBodyPart = MimeBodyPart()
                val source: DataSource = FileDataSource(filelocation)
                messageBodyPart.dataHandler = DataHandler(source)
                messageBodyPart.fileName = filename
                multipart.addBodyPart(messageBodyPart)


                val mc: MailcapCommandMap = CommandMap.getDefaultCommandMap() as MailcapCommandMap
                mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html")
                mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml")
                mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain")
                mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed")
                mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822")


                // Send the complete message parts
                mm.setContent(multipart)


                //Sending email
                Transport.send(mm)

                appExecutors.mainThread().execute {
                    Toast.makeText(context, "Email envoye", Toast.LENGTH_SHORT).show();
                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }





}
