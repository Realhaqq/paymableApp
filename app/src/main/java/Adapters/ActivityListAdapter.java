package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import Models.ActivityListModel;
import ng.paymable.AddNewCardActivity;
import ng.paymable.AddWalletBalanceActivity;
import ng.paymable.FingerPrintActivity;
import ng.paymable.HomePay2Activity;
import ng.paymable.LoginActivity;
import ng.paymable.MobileVerificationActivity;
import ng.paymable.MyAccountActivity;
import ng.paymable.MyTransactionActivity;
import ng.paymable.OfferActivity;
import ng.paymable.OtpActivity;
import ng.paymable.R;
import ng.paymable.SaveCardsActivity;
import ng.paymable.ScanAndPayActivity;
import ng.paymable.SecureLoginActivity;
import ng.paymable.SignupActivity;
import ng.paymable.SplashActivity;
import ng.paymable.TransactionDetailsActivity;
import ng.paymable.WelcomeActivity;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ViewHolder>{

    private Context context;
    private List<ActivityListModel> activityListModels;

    public ActivityListAdapter(Context context, List<ActivityListModel> activityListModels) {
        this.context = context;
        this.activityListModels = activityListModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        holder.itemtxt.setText(activityListModels.get(position).getItemtxt());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position == 0) {
                    Intent i = new Intent(context, SplashActivity.class);
                    context.startActivity(i);
                }else if(position == 1){
                    Intent i = new Intent(context, WelcomeActivity.class);
                    context.startActivity(i);
                }else if(position == 2){
                    Intent i = new Intent(context, LoginActivity.class);
                    context.startActivity(i);
                }else if(position == 3){
                    Intent i = new Intent(context, SignupActivity.class);
                    context.startActivity(i);
                }else if(position == 4){
                    Intent i = new Intent(context, MobileVerificationActivity.class);
                    context.startActivity(i);
                }else if(position == 5){
                    Intent i = new Intent(context, FingerPrintActivity.class);
                    context.startActivity(i);
                }else if(position == 6){
                    Intent i = new Intent(context, OtpActivity.class);
                    context.startActivity(i);
                }else if(position == 7){
                    Intent i = new Intent(context, SecureLoginActivity.class);
                    context.startActivity(i);
                }else if(position == 8){
                    Intent i = new Intent(context, HomePay2Activity.class);
                    context.startActivity(i);
                }else if(position == 9) {
                    Intent i = new Intent(context, ScanAndPayActivity.class);
                    context.startActivity(i);
                }else if(position == 10) {
                    Intent i = new Intent(context, OfferActivity.class);
                    context.startActivity(i);
                }else if(position == 11) {
                    Intent i = new Intent(context, MyTransactionActivity.class);
                    context.startActivity(i);
                }else if(position == 12) {
                    Intent i = new Intent(context, AddNewCardActivity.class);
                    context.startActivity(i);
                }else if(position == 13) {
                    Intent i = new Intent(context, AddWalletBalanceActivity.class);
                    context.startActivity(i);
                }else if(position == 14) {
                    Intent i = new Intent(context, SaveCardsActivity.class);
                    context.startActivity(i);
                }else if(position == 15) {
                    Intent i = new Intent(context, MyAccountActivity.class);
                    context.startActivity(i);
                }else if(position == 16) {
                    Intent i = new Intent(context, TransactionDetailsActivity.class);
                    context.startActivity(i);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return activityListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemtxt;
        public ViewHolder(View itemView) {
            super(itemView);

            itemtxt = itemView.findViewById(R.id.itemtxt);


        }
    }
}
