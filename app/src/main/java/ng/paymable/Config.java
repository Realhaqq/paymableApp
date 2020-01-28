package ng.paymable;

public class Config {
    public static String url = "https://paymable.ng/api/v1/";
    public static  String user_login = url + "user_login.php";
    public static String user_register = url + "user_register.php";
    public static String user_wallet = url + "user_wallet.php";


//    Payment - paystack
    public static String paystack_public_key = "pk_test_3295da775f8bc819ad35293b0b7bacc628e6d2bb";
    public static String paystack_saver_url = url + "pay/charge.php";
}
