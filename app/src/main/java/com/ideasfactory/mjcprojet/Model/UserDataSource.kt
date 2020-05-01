package com.ideasfactory.mjcprojet.Model

import com.google.firebase.database.DataSnapshot
import java.lang.Exception

class UserDataSource (
    var datasource_user_id: String,
    var datasource_user_name: String,
    var datasource_user_phone: String,
    var datasource_user_amount_init: String,
    var datasource_user_firstname: String,
    var datasource_user_load_date: String)
{

    constructor(): this("","","","","","")



    /* init{
         try{
             val data : HashMap<String, Any> = snapshot.value as HashMap<String, Any>
             id = snapshot.key ?: ""
             datasource_user_name = data ["datasource_user_name"] as String
             datasource_user_phone = data["datasource_user_phone"] as String
             datasource_user_amount_init = data["datasource_user_amount_init"] as String
             datasource_user_firstname = data["datasource_user_firstname"] as String
             datasource_user_load_date = data["datasource_user_load_date"] as String
         } catch (e : Exception){
             e.printStackTrace()
         }
     }*/

}