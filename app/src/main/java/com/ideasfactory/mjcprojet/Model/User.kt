package com.ideasfactory.mjcprojet.Model

 class User(
    //el id del usuario de la app está regido por el id de firebase Auth
    val app_user_actiontype : String,
    val app_user_amount_donation : Int,
    val app_user_amount_final : Int,
    val app_user_amount_voucher : Int,
    val app_user_firstname : String,
    val app_user_mail: String,
    val app_user_modified_date : String,
    val app_user_name : String,
    val app_user_phone : String

) {

     override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if (javaClass != other?.javaClass) return false

         other as User

         if (app_user_actiontype != other.app_user_actiontype) return false
         if (app_user_amount_donation != other.app_user_amount_donation) return false
         if (app_user_amount_final != other.app_user_amount_final) return false
         if (app_user_amount_voucher != other.app_user_amount_voucher) return false
         if (app_user_firstname != other.app_user_firstname) return false
         if (app_user_mail != other.app_user_mail) return false
         if (app_user_modified_date != other.app_user_modified_date) return false
         if (app_user_name != other.app_user_name) return false
         if (app_user_phone != other.app_user_phone) return false

         return true
     }

     override fun hashCode(): Int {
         var result = app_user_actiontype.hashCode()
         result = 31 * result + app_user_amount_donation
         result = 31 * result + app_user_amount_final
         result = 31 * result + app_user_amount_voucher
         result = 31 * result + app_user_firstname.hashCode()
         result = 31 * result + app_user_mail.hashCode()
         result = 31 * result + app_user_modified_date.hashCode()
         result = 31 * result + app_user_name.hashCode()
         result = 31 * result + app_user_phone.hashCode()
         return result
     }
 }