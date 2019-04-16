import UIKit
import Flutter
import MidtransKit

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    
    private let CHANNEL = "alfianlosari.com/payment"
    private  let clientKey = "INSERT_CLIENT_KEY"
    private  let merchantServerUrl = "INSERT_URL"
    private var flutterVC: FlutterViewController?
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?
        ) -> Bool {
        let vc = self.window!.rootViewController as! FlutterViewController
        self.flutterVC = vc
        
        let paymentChannel = FlutterMethodChannel(name: CHANNEL, binaryMessenger: vc)
        paymentChannel.setMethodCallHandler { (call, result) in
            if call.method == "charge", let orderDict = call.arguments as? [String: Any] {
                self.pay(dict: orderDict, result: result)
            } else {
                result(nil)
            }
        }
        
        GeneratedPluginRegistrant.register(with: self)
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    func pay(dict: [String: Any], result: @escaping FlutterResult) {
        guard
            let total = dict["total"] as? NSNumber,
            let orderId = dict["orderId"] as? String,
            let items = dict["items"] as? [[String: Any]]
            else {
                result(nil)
                return
        }
        
        let itemDetails = items.map { (dict) -> MidtransItemDetail in
            return MidtransItemDetail(itemID: dict["id"] as? String, name: dict["name"] as? String, price: dict["price"] as? NSNumber, quantity: 1)
        }
        
        
        MidtransConfig.shared().setClientKey(clientKey, environment: .sandbox, merchantServerURL: merchantServerUrl)
        
        
        let address = MidtransAddress(firstName: "Alfian", lastName: "Losari", phone: "62812394594", address: "JL PRX", city: "Jakarta", postalCode: "60189", countryCode: nil)
        
        let customerDetail = MidtransCustomerDetails(firstName: "Alfian", lastName: "Losari", email: "alfianlosari@gmail.com", phone: "+628123232323", shippingAddress: address, billingAddress: address)
        let transactionDetail = MidtransTransactionDetails(orderID: orderId, andGrossAmount: total)
        
        
        MidtransMerchantClient.shared().requestTransactionToken(with: transactionDetail!, itemDetails: itemDetails, customerDetails: customerDetail) { (token, error) in
            if let token = token {
                let vc = MidtransUIPaymentViewController(token: token)
                //                vc?.paymentDelegate = self
                
                self.flutterVC?.present(vc!, animated: true, completion: nil)
                
                result("OK")
                
            } else {
                result("Error requesting token \(error?.localizedDescription ?? "fdf")")
            }
            
        }
    }
    
    
    
}

