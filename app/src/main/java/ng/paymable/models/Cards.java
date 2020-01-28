package ng.paymable.models;

public class Cards {
    int card_number;

    String authorization_key, channel, set_default,  card_full_name, card_type;

    public int getCard_number() {
        return card_number;
    }

    public String getAuthorization_key() {
        return authorization_key;
    }

    public String getCard_full_name() {
        return card_full_name;
    }

    public String getChannel() {
        return channel;
    }

    public String getCard_type() {
        return card_type;
    }

    public String getSet_default() {
        return set_default;
    }

    public void setAuthorization_key(String authorization_key) {
        this.authorization_key = authorization_key;
    }

    public void setCard_full_name(String card_full_name) {
        this.card_full_name = card_full_name;
    }


    public void setCard_number(int card_number) {
        this.card_number = card_number;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setSet_default(String set_default) {
        this.set_default = set_default;
    }


}
