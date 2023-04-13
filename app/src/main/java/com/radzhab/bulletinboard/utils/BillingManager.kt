package com.radzhab.bulletinboard.utils

import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

class BillingManager(val act: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener())
            .enablePendingPurchases().build()
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

    private fun getPurchaseListener(): PurchasesUpdatedListener {
        return PurchasesUpdatedListener { result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0).let { }
                }
            }
        }
    }

    companion object {
        const val REMOVE_ADS = "remove_ads"
    }
}