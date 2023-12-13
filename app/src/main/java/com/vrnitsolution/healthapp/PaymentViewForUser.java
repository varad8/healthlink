package com.vrnitsolution.healthapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vrnitsolution.healthapp.adapter.PaymentIntentAdapter;
import com.vrnitsolution.healthapp.model.PaymentIntentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentViewForUser extends AppCompatActivity implements PaymentIntentAdapter.OnItemClickListener {

    FirebaseAuth mauth;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    PaymentIntentAdapter adapter;
    List<PaymentIntentModel> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_view_for_user);

        mauth = FirebaseAuth.getInstance();
        firebaseUser = mauth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        paymentList = new ArrayList<>();
        adapter = new PaymentIntentAdapter(paymentList, this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            fetchUserPayments(uid);
        }
    }

    private void fetchUserPayments(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Payment").whereEqualTo("cust_id", uid);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                paymentList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    PaymentIntentModel payment = document.toObject(PaymentIntentModel.class);
                    paymentList.add(payment);
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle errors
            }
        });
    }

    public void backPressed(View view) {
        onBackPressed();
    }
    public void onItemClick(int position, PaymentIntentModel paymentIntentModel) {
        // Show all data in a default AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment Details")
                .setMessage(getPaymentDetailsMessage(paymentIntentModel))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private String getPaymentDetailsMessage(PaymentIntentModel paymentIntentModel) {
        // Customize the message based on your data
        return "Payment Type: " + paymentIntentModel.getPay_type() + "\n" +
                "Payment Date: " + formatTimestamp(paymentIntentModel.getPay_date()) + "\n" +
                "Payment Amount: " + paymentIntentModel.getPay_amount() + "\n" +
                "Payment Status: " + paymentIntentModel.getPay_status()+"\n"+
                "Customer Id: "+ paymentIntentModel.getCust_id()+"\n"+
                "Doctor Id: "+paymentIntentModel.getDoc_id();
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }
}
