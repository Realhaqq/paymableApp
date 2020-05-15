package ng.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import ng.paymable.models.User;

public class SessionHandlerUser {

    private static final String PREF_NAME = "ApplicantSession";
    private static final String KEY_EMPTY = "";
    private static final String KEY_EXPIRES = "expires";


    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandlerUser(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    /**
     * Logs in the user by saving user details and setting session
     * @param email
     * @param fullname
     * @param phone
     * @param userid
     */

    public void loginUser(String email, String fullname, String phone, String userid) {
        mEditor.putString("email", email);
        mEditor.putString("fullname", fullname);
        mEditor.putString("phone", phone);
        mEditor.putString("userid", userid);


        Date date = new Date();

        //Set user session for next 7 days
        long millis = date.getTime() + (7 * 24 * 60 * 60 * 1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }


//    public void EditRef(String app_status){
//        mEditor.putString(KEY_APP_STATUS, app_status);
//        mEditor.commit();
//    }
//
//    public void EditPaymentStatus(String payment_status){
//        mEditor.putString(KEY_PAYMENT_STATUS, payment_status);
//        mEditor.commit();
//    }

    /**
     * Checks whether user is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        /* If shared preferences does not have a value
         then user is not logged in
         */
        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);

        /* Check if session is expired by comparing
        current date and Session expiry date
        */
        return currentDate.before(expiryDate);
    }

    /**
     * Fetches and returns user details
     *
     * @return user details
     */
    public User getUserDetail() {
        //Check if user is logged in first
        if (!isLoggedIn()) {
            return null;
        }
        User student = new User();

        student.setEmail(mPreferences.getString("email", KEY_EMPTY));
        student.setFullname(mPreferences.getString("fullname", KEY_EMPTY));
        student.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));
        student.setPhone(mPreferences.getString("phone", KEY_EMPTY));
        student.setUserid(Integer.parseInt(mPreferences.getString("userid", KEY_EMPTY)));
//        student.setUserid(Integer.parseInt(mPreferences.getString("userid", KEY_EMPTY)));
//        student.setVerify_status(Integer.parseInt(mPreferences.getString("verify_status", KEY_EMPTY)));

        return student;
    }

    /**
     * Logs out user by clearing the session
     */
    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }


}
