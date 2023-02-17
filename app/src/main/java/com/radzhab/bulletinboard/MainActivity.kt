package com.radzhab.bulletinboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.SearchEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.radzhab.bulletinboard.databinding.ActivityMainBinding
import com.radzhab.bulletinboard.dialogHelper.DialogConst
import com.radzhab.bulletinboard.dialogHelper.DialogHelper

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var tvAccount: TextView
    private lateinit var rootElement: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val myAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    private fun init() {
        val toggle =
            ActionBarDrawerToggle(
                this,
                rootElement.drawerLayout,
                rootElement.mainContent.toolbar,
                R.string.open,
                R.string.close
            )
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_my_adds -> Toast.makeText(this, "Pressed $item", Toast.LENGTH_LONG)
                .show()

            R.id.id_car -> Toast.makeText(this, "Pressed $item", Toast.LENGTH_LONG)
                .show()
            R.id.id_pc -> Toast.makeText(this, "Pressed $item", Toast.LENGTH_LONG)
                .show()
            R.id.id_phone -> Toast.makeText(this, "Pressed $item", Toast.LENGTH_LONG)
                .show()
            R.id.id_dm -> Toast.makeText(this, "Pressed $item", Toast.LENGTH_LONG)
                .show()
            R.id.id_sign_in -> dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            R.id.id_sign_up -> dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            R.id.id_sign_out -> {
                uiUpdate(null)
                myAuth.signOut()
            }
        }
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) resources.getString(R.string.not_reg) else user.email

    }

}