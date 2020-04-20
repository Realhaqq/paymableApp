package ng.paymable.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ng.paymable.R;
import ng.paymable.models.Transactions;
import ng.paymable.models.WalletTransactions;

public class TransactionWalletAdapter extends RecyclerView.Adapter<TransactionWalletAdapter.MyViewHolder> {
    Context context;
    public List<WalletTransactions> transactionsList;


    public TransactionWalletAdapter(List<WalletTransactions> transactionsList, Context context) {
        super();
        this.transactionsList = transactionsList;
        this.context = context;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView transaction_type, amount, ondate, data, type;

        public ImageView poster;
        public MyViewHolder(View itemView) {
            super(itemView);

            transaction_type = itemView.findViewById(R.id.transaction_type);
            amount = itemView.findViewById(R.id.amount);
            ondate = itemView.findViewById(R.id.ondate);
            type = itemView.findViewById(R.id.type);

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_transaction, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WalletTransactions transactions = transactionsList.get(position);
        holder.transaction_type.setText(transactions.getTransactio_type());
        holder.ondate.setText(transactions.getOndate());
        holder.type.setText(transactions.getPayment_type());
        holder.amount.setText("₦" + transactions.getAmount());

    }


    @Override
    public int getItemCount() {
        return transactionsList.size();
    }


}
