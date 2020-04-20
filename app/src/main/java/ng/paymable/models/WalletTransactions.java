package ng.paymable.models;

public class WalletTransactions {
    String transactio_type, payment_type, amount, ondate;


    public void setOndate(String ondate) {
        this.ondate = ondate;
    }

    public String getOndate() {
        return ondate;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactio_type() {
        return transactio_type;
    }

    public void setTransactio_type(String transactio_type) {
        this.transactio_type = transactio_type;
    }
}

