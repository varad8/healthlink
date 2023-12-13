package com.vrnitsolution.healthapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.vrnitsolution.healthapp.R;
import com.vrnitsolution.healthapp.model.PaymentIntentModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PaymentIntentAdapter extends RecyclerView.Adapter<PaymentIntentAdapter.ViewHolder> {

    private List<PaymentIntentModel> paymentList;
    private Context context;
    private OnItemClickListener onItemClickListener; // Define listener member variable

    public interface OnItemClickListener {
        void onItemClick(int position,PaymentIntentModel paymentIntentModel);
    }

    public PaymentIntentAdapter(List<PaymentIntentModel> paymentList, Context context, OnItemClickListener listener) {
        this.paymentList = paymentList;
        this.context = context;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentIntentModel payment = paymentList.get(position);

        // Bind data to the ViewHolder
        holder.payTypeTextView.setText("Payment Type: " + payment.getPay_type());
        holder.payDateTextView.setText("Payment Date: " + formatTimestamp(payment.getPay_date()));
        holder.payAmountTextView.setText("Payment Amount: " + String.valueOf(payment.getPay_amount()));
        holder.payStatusTextView.setText("Payment Status: " + payment.getPay_status());

        // Set click listener on the entire item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position,payment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView payTypeTextView, payDateTextView, payAmountTextView, payStatusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            payTypeTextView = itemView.findViewById(R.id.payTypeTextView);
            payDateTextView = itemView.findViewById(R.id.payDateTextView);
            payAmountTextView = itemView.findViewById(R.id.payAmountTextView);
            payStatusTextView = itemView.findViewById(R.id.payStatusTextView);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }
}
