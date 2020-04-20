package ng.paymable.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

import ng.paymable.R;
import ng.paymable.models.Transactions;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    Context context;
    public List<Transactions> transactionsList;


    public TransactionAdapter(List<Transactions> transactionsList, Context context) {
        super();
        this.transactionsList = transactionsList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView biller, amount, ondate, data, type;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            biller = itemView.findViewById(R.id.billName);
            amount = itemView.findViewById(R.id.amount);
            data = itemView.findViewById(R.id.phnNo);
            type = itemView.findViewById(R.id.type);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_tarnsaction, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Transactions transactions = transactionsList.get(position);
        holder.biller.setText(transactions.getBiller());
        holder.data.setText(transactions.getTransaction_data());
        holder.type.setText(transactions.getPayment_type());
        holder.amount.setText("₦" + transactions.getAmount());

    }


    @Override
    public int getItemCount() {
        return transactionsList.size();
    }


}
