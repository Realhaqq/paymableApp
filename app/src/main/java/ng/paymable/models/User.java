package ng.paymable.models;


import java.util.Date;

public class User {
    public String fullname, phone, email;
    public int userid, verify_status;

    Date sessionExpiryDate;

    public Date getSessionExpiryDate() {
        return sessionExpiryDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUserid() {
        return userid;
    }

    public int getVerify_status() {
        return verify_status;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setVerify_status(int verify_status) {
        this.verify_status = verify_status;
    }

    public void setSessionExpiryDate(Date sessionExpiryDate) {
        this.sessionExpiryDate = sessionExpiryDate;
    }

}
