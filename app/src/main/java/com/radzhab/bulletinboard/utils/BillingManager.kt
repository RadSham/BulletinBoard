package com.radzhab.bulletinboard.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.radzhab.bulletinboard.R

class BillingManager(val act: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener())
            .enablePendingPurchases().build()
    }

    private fun savePurchase(isPurchase: Boolean){
        val pref = act.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_PREF, isPurchase)
        editor.apply()
    }

    fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                getItem()
            }
        })
    }


    private fun getItem() {
        val productList =
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(REMOVE_ADS)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList)

        billingClient?.queryProductDetailsAsync(params.build()) { billingResult,
                                                                  productDetailsList ->
            run {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (productDetailsList.isNotEmpty()) {
                        val productDetailsParamsList =
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetailsList[0])
                                    .build()
                            )
                        val billingFlowParams =
                            BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()
                        // Launch the billing flow
                        billingClient?.launchBillingFlow(act, billingFlowParams)
                    }
                }
            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()

                billingClient?.acknowledgePurchase(acParams) { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePurchase(true)
                        Toast.makeText(act, act.getString(R.string.purchase_thanks), Toast.LENGTH_LONG).show()
                    } else {
                        savePurchase(false)
                        Toast.makeText(act, act.getString(R.string.purchase_failed), Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener {
        return PurchasesUpdatedListener { result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }

    companion object {
        const val REMOVE_ADS = "remove_ads"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_ADS_PREF = "remove_ads_pref"
    }
}