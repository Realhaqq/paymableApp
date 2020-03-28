package ng.paymable;

public class Config {
    public static String url = "https://paymable.ng/app_api/v1/";
    public static  String user_login = url + "user_login.php";
    public static String user_register = url + "user_register.php";
    public static String user_wallet = url + "user_wallet.php";


//    Payment - paystack
    public static String paystack_public_key = "pk_live_719bd18bae324b364c19afacf02d30683370ee81";
    public static String paystack_saver_url = url + "pay/charge.php";
}
