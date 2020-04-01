package com.example.dogsproject.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.dogsproject.R
import com.example.dogsproject.util.PERMISSION_SEND_SMS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.fragmentMain)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun checkSmsPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
                AlertDialog.Builder(this)
                    .setTitle("Send SMS Permission")
                    .setMessage("This app requires access to send SMS")
                    .setPositiveButton("Ask me"){dialog, which ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No"){dialog, which ->
                        notifyDetailsFragment(false)
                    }
                    .show()
            } else{
                requestSmsPermission()
            }
        } else{
            notifyDetailsFragment(true)
        }
    }

    fun requestSmsPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_SEND_SMS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyDetailsFragment(true)
                } else{
                    notifyDetailsFragment(false)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun notifyDetailsFragment(permissionGranted: Boolean){
        val activeFragment = fragmentMain.childFragmentManager.primaryNavigationFragment
        if(activeFragment is DetailFragment){
            activeFragment.onPermissionResult(permissionGranted)
        }
    }
}
