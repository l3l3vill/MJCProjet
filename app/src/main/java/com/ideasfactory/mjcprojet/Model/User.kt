package com.ideasfactory.mjcprojet.Model

import android.util.Log

class User(
    //el id del usuario de la app está regido por el id de firebase Auth
     //val app_user_first_connect : String,
     val app_user_action_date : String,
     val app_user_action_type : String,
    val app_user_amount_donation : String,
    val app_user_amount : String,
    val app_user_amount_voucher : String,
    val app_user_first_name : String,
    val app_user_mail: String,
    val app_user_modified_date : String,//TODO-> timestamp
    val app_user_name : String,
    val app_user_phone : String,
    val app_user_os : String = "Android"

) {

     companion object {
         var numberExistInDataBase = false

     }
     init {
         Log.i("COMPANION", "User class $numberExistInDataBase")
     }
     constructor(): this("","","","","","","","","","","Android")

     override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if (javaClass != other?.javaClass) return false

         other as User

         if (app_user_action_date != other.app_user_action_date) return false
         if (app_user_action_type != other.app_user_action_type) return false
         if (app_user_amount_donation != other.app_user_amount_donation) return false
         if (app_user_amount != other.app_user_amount) return false
         if (app_user_amount_voucher != other.app_user_amount_voucher) return false
         if (app_user_first_name != other.app_user_first_name) return false
         if (app_user_mail != other.app_user_mail) return false
         if (app_user_modified_date != other.app_user_modified_date) return false
         if (app_user_name != other.app_user_name) return false
         if (app_user_phone != other.app_user_phone) return false
         if (app_user_os != other.app_user_os) return false

         return true
     }

     override fun hashCode(): Int {
         var result = app_user_action_date.hashCode()
         result = 31 * result + app_user_action_type.hashCode()
         result = 31 * result + app_user_amount_donation.hashCode()
         result = 31 * result + app_user_amount.hashCode()
         result = 31 * result + app_user_amount_voucher.hashCode()
         result = 31 * result + app_user_first_name.hashCode()
         result = 31 * result + app_user_mail.hashCode()
         result = 31 * result + app_user_modified_date.hashCode()
         result = 31 * result + app_user_name.hashCode()
         result = 31 * result + app_user_phone.hashCode()
         result = 31 * result + app_user_os.hashCode()
         return result
     }

     override fun toString(): String {
         return "User(app_user_action_date='$app_user_action_date', app_user_action_type='$app_user_action_type', app_user_amount_donation='$app_user_amount_donation', app_user_amount='$app_user_amount', app_user_amount_voucher='$app_user_amount_voucher', app_user_first_name='$app_user_first_name', app_user_mail='$app_user_mail', app_user_modified_date='$app_user_modified_date', app_user_name='$app_user_name', app_user_phone='$app_user_phone', app_user_os='$app_user_os')"
     }


 }