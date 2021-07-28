package com.cg.couponsapp.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class NetworkUtil {
    fun getConnectivityStatusString(context: Context): String? {
        var status: String? = "No internet is available"
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = cm.activeNetwork ?: return status
            val activeNetwork = cm.getNetworkCapabilities(network) ?: return status
            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return "Wifi enabled"
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return "Mobile data enabled"
                else ->   return "No internet is available"
            }
        }else{
            if(cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI) return "Wifi enabled"
            else if(cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_MOBILE)  return "Mobile data enabled"
            else return "No internet is available"
        }
        return status
    }
    fun checkStatus(context: Context,intent: Intent) : Boolean{
        var status = NetworkUtil().getConnectivityStatusString(context)
        var dialog = Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen)
        dialog.setCancelable(false)
        dialog.setContentView(com.cg.couponsapp.R.layout.no_internet_dialog)
        val restartapp: Button = dialog.findViewById<View>(com.cg.couponsapp.R.id.restartapp) as Button
        var nettext = dialog.findViewById<View>(com.cg.couponsapp.R.id.nettext) as TextView

        restartapp.setOnClickListener {
            val packageMgr = context.packageManager
            val intent = packageMgr.getLaunchIntentForPackage(context.packageName)
            val compName = intent?.component
            val mainIntent = Intent.makeRestartActivityTask(compName)
            context.startActivity(mainIntent!!)
            Runtime.getRuntime().exit(0)
        }
        if (status!!.isEmpty() || status == "No internet is available" || status == "No Internet Connection") {
            status = "No Internet Connection"
            dialog.show()
        }
//        Toast.makeText(context, status, Toast.LENGTH_LONG).show()
        if(status.equals("No Internet Connection")){
            return false
        }
        return true
    }
}