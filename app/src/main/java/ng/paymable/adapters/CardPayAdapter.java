package ng.paymable.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ng.paymable.R;
import ng.paymable.models.Cards;

public class CardPayAdapter extends RecyclerView.Adapter<CardPayAdapter.MyViewHolder> {
    List<Cards> cardList;
    Context context;


    public CardPayAdapter(List<Cards> cardList, Context context){
        super();

        this.cardList = cardList;
        this.context = context;
    }
    @NonNull
    @Override
    public CardPayAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_cards, parent, false);

        return new CardPayAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardPayAdapter.MyViewHolder holder, int position) {

        Cards card = cardList.get(position);
        holder.cardno.setText(" **** **** **** " + card.getCard_number());

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cardname, cardno, cardtype, date;

        public MyViewHolder(View itemView) {
            super(itemView);

//            cardname = itemView.findViewById(R.id.cardHolderName);
//            cardtype = itemView.findViewById(R.id.card);
            cardno = itemView.findViewById(R.id.cardNo);

        }


    }
}
