package com.example.shoppinglist2

import android.os.Bundle
import android.util.Log

import com.google.gson.JsonObject;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.*
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import org.json.JSONObject
import org.json.JSONArray
import com.midtrans.sdk.corekit.models.*
import java.util.ArrayList
import java.util.HashMap


class MainActivity: FlutterActivity(), TransactionFinishedCallback {

  private val CHANNEL = "alfianlosari.com/payment"
  private val clientKey = "INSERT_CLIENT_KEY"
  private val merchantServerUrl = "INSERT_URL"

  companion object {
    val TAG = "MainActivity"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)

    MethodChannel(flutterView, CHANNEL).setMethodCallHandler { methodCall, result ->
      if (methodCall.method.equals("charge")) {
        val dict = methodCall.arguments as HashMap<String, Any>

        SdkUIFlowBuilder.init()
                .setClientKey(clientKey) // client_key is mandatory
                .setContext(MainActivity@this) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(merchantServerUrl) //set merchant url
                .enableLog(true)
                .buildSDK()


// Save the user detail. It will skip the user detail screen
//        LocalDataHandler.saveObject("user_details", userDetail);



        try {
          val total = dict.get("total") as Int
          val orderId = dict.get("orderId") as String
          val items = dict.get("items") as ArrayList<HashMap<String, Any>>


          val transactionRequest = TransactionRequest(orderId, total.toDouble())
          val itemList = items.map {
            ItemDetails(it.get("id") as String, (it.get("price") as Int).toDouble() , 1, it.get("name") as String);
          }

          val userDetail = UserDetail()
          userDetail.userFullName = "Alfian"
          userDetail.userId = "1234"
          userDetail.email = "alfian@losari.org"
          userDetail.phoneNumber = "3212312312"

          val userAddress = UserAddress()
          userAddress.address = "Jl XXX"
          userAddress.city = "Jakarta"
          userAddress.country = "IND"
          userAddress.zipcode = "60189"


          userAddress.addressType = Constants.ADDRESS_TYPE_BOTH



          userDetail.userAddresses = arrayListOf(userAddress)


          LocalDataHandler.saveObject("user_details", userDetail)



          val customer = CustomerDetails()
          customer.firstName = "Alfian"
          customer.lastName = "Losari"
          customer.email = "alfianlosari@gmail.com"
          customer.phone = "0812323232"

          val billAddress = BillingAddress()
          billAddress.firstName = "Alfian"
          billAddress.lastName = "Losari"
          billAddress.address = "JL X"
          billAddress.city = "Jakarta"
          billAddress.phone = "34234234324"
          billAddress.postalCode = "60189"
          billAddress.countryCode = "IDN"
          customer.billingAddress = billAddress

          val shipAddress = ShippingAddress()
          shipAddress.firstName = "Alfian"
          shipAddress.lastName = "Losari"
          shipAddress.address = "JL X"
          shipAddress.city = "Jakarta"
          shipAddress.phone = "323232323"
          shipAddress.postalCode = "60189"
          shipAddress.countryCode = "IDN"
          customer.shippingAddress = shipAddress



          transactionRequest.customerDetails = customer


          transactionRequest.itemDetails = ArrayList(itemList)

          val setting = MidtransSDK.getInstance().uiKitCustomSetting
          MidtransSDK.getInstance().uiKitCustomSetting = setting
          MidtransSDK.getInstance().transactionRequest = transactionRequest
          MidtransSDK.getInstance().startPaymentUiFlow(MainActivity@this)
        } catch (e: Exception) {
          Log.d(MainActivity.TAG, "ERROR " + e.message)
        }

        result.success("Midtrans successfully initialized")



      } else {
        result.success("Method not handled")
      }
    }

  }

  override fun onTransactionFinished(p0: TransactionResult?) {
  }
}
