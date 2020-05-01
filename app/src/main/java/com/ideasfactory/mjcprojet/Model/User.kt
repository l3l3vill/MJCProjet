package com.ideasfactory.mjcprojet.Model

 class User(
    //el id del usuario de la app está regido por el id de firebase Auth
    val app_user_actiontype : String,
    val app_user_amount_donation : String,
    val app_user_amount_final : String,
    val app_user_amount_voucher : String,
    val app_user_firstname : String,
    val app_user_mail: String,
    val app_user_modified_date : String,//TODO-> timestamp
    val app_user_name : String,
    val app_user_phone : String,
    val app_user_os : String = "Android"

) {

     constructor(): this("","","","","","","","","","Android")



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
         if (app_user_os != other.app_user_os) return false

         return true
     }

     override fun toString(): String {
         return "User(app_user_actiontype='$app_user_actiontype', app_user_amount_donation=$app_user_amount_donation, app_user_amount_final=$app_user_amount_final, app_user_amount_voucher=$app_user_amount_voucher, app_user_firstname='$app_user_firstname', app_user_mail='$app_user_mail', app_user_modified_date='$app_user_modified_date', app_user_name='$app_user_name', app_user_phone='$app_user_phone', app_user_os='$app_user_os')"
     }


 }