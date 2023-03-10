package com.radzhab.bulletinboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.radzhab.bulletinboard.act.EditAdsActivity
import com.radzhab.bulletinboard.adaptors.AdsRcAdapter
import com.radzhab.bulletinboard.databinding.ActivityMainBinding
import com.radzhab.bulletinboard.dialogHelper.DialogConst
import com.radzhab.bulletinboard.dialogHelper.DialogHelper
import com.radzhab.bulletinboard.viewmodel.FirebaseViewModel

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var tvAccount: TextView
    private lateinit var rootElement: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val myAuth = Firebase.auth
    val adapter = AdsRcAdapter(myAuth)
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        initRecyclerView()
        initViewModel()
        firebaseViewModel.loadAllAds()
        buttonMenuOnClick()
    }

    override fun onResume() {
        super.onResume()
        rootElement.mainContent.bNavView.selectedItemId = R.id.id_home
    }

   /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_new_ads) {
        }
        return super.onOptionsItemSelected(item)
    }*/

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

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this) {
            adapter.update(it)
        }
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

    private fun buttonMenuOnClick() = with(rootElement) {
        mainContent.bNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsActivity::class.java)
                    startForResult.launch(i)
                    true
                }
                R.id.id_my_ads -> {
                    Toast.makeText(this@MainActivity, "MyAds", Toast.LENGTH_LONG)
                        .show()
                    true
                }
                R.id.id_favs -> {
                    Toast.makeText(this@MainActivity, "Favs", Toast.LENGTH_LONG)
                        .show()
                    true
                }
                R.id.id_home -> {
                    Toast.makeText(this@MainActivity, "Home", Toast.LENGTH_LONG)
                        .show()
                    true
                }
                else -> false
            }
        }
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


}