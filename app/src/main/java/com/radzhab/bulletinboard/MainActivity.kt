package com.radzhab.bulletinboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.adaptors.AdsRcAdapter
import com.radzhab.bulletinboard.data.Ad
import com.radzhab.bulletinboard.database.DbManager
import com.radzhab.bulletinboard.database.ReadDataCallback
import com.radzhab.bulletinboard.databinding.ActivityMainBinding
import com.radzhab.bulletinboard.dialogHelper.DialogConst
import com.radzhab.bulletinboard.dialogHelper.DialogHelper

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, ReadDataCallback {

    private lateinit var tvAccount: TextView
    private lateinit var rootElement: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val myAuth = FirebaseAuth.getInstance()
    val dbManager = DbManager(this)
    val adapter = AdsRcAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        initRecyclerView()
        dbManager.readDaraFromDb()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_new_ads) {
            val i = Intent(this, EditAdsActivity::class.java)
            startForResult.launch(i)
        }
        return super.onOptionsItemSelected(item)
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken.toString())
                    }
                } catch (e: ApiException) {
                    Log.d("MyLog", "Api error : ${e.message}")
                }
            }
        }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    private fun init() {
        setSupportActionBar(rootElement.mainContent.toolbar)
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

    private fun initRecyclerView() {
        rootElement.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
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
                dialogHelper.accHelper.signOutGoogle()
            }
        }
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) resources.getString(R.string.not_reg) else user.email

    }

    override fun readData(list: List<Ad>) {
        adapter.update(list)
    }

}