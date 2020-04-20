package ng.paymable.models;

public class Transactions {
    String biller, phone, payment_type, amount, orderid, ref, ondate, transaction_data, status;

    public String getBiller() {
        return biller;
    }

    public String getAmount() {
        return amount;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setBiller(String biller) {
        this.biller = biller;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOndate() {
        return ondate;
    }

    public String getOrderid() {
        return orderid;
    }

    public String getRef() {
        return ref;
    }

    public String getStatus() {
        return status;
    }

    public String getTransaction_data() {
        return transaction_data;
    }

    public void setOndate(String ondate) {
        this.ondate = ondate;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransaction_data(String transaction_data) {
        this.transaction_data = transaction_data;
    }

}
