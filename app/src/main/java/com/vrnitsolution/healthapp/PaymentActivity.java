package com.vrnitsolution.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.vrnitsolution.healthapp.bookappointment.BookAppointment;
import com.vrnitsolution.healthapp.model.PaymentIntentModel;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    AlertDialog alertDialog,makePayment;
    ProgressDialog progressDialog;
    String pay_id;
    Double pay_amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);



        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("UPI Validating");
        Intent intent=getIntent();

        if (intent!=null)
        {
            pay_id=intent.getStringExtra("pay_id");
            pay_amount= intent.getDoubleExtra("amount",1000.00);
        }
    }

    public void paymentPay(View view) {
            progressDialog.show();
            // Create an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_upi, null);
            builder.setView(dialogView);

            final EditText upiEditText = dialogView.findViewById(R.id.editTextUPI);

            builder.setPositiveButton("Validate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String upiId = upiEditText.getText().toString().trim();
                    if (isValidUpiId(upiId)) {
                        progressDialog.setMessage("Payment Processing...");
                        alertDialog.dismiss();
                        openAnotherDialog();
                    } else {
                        showToast("Invalid UPI ID. Please try again.");
                        progressDialog.dismiss();
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Show the AlertDialog
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();


    }

     private void openAnotherDialog() {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Total Payment");
        builder.setMessage("Total payable Booking amount:"+pay_amount); // You can customize this message

        final TextView totalPaymentEditText = new TextView(this);
        builder.setView(totalPaymentEditText);


        builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Payment Saving");
                progressDialog.show();
                makePayment.dismiss();
                updatePaymentCollection(pay_amount);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        makePayment = builder.create();
        makePayment.setCanceledOnTouchOutside(false);
         makePayment.show();
    }


    private void updatePaymentCollection(Double totalPayment) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference paymentRef = db.collection("Payment").document(pay_id);

        Map<String, Object> updates = new HashMap<>();
        updates.put("pay_status", "paid");
        updates.put("pay_date", FieldValue.serverTimestamp());
        updates.put("pay_type","upi");
        paymentRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        showToast("Payment successful!");
                        showToast("Payment status updated successfully!");
                        startActivity(new Intent(PaymentActivity.this,DashboardHome.class));
                        finish();
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showToast("Error updating payment status: " + e.getMessage());
                    }
                });
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private boolean isValidUpiId(String upiId) {
        String[] validSuffixes = {"@ibl", "@ybl", "@okaxis", "@oksbi", "@okhdfcbank"};

        for (String suffix : validSuffixes) {
            if (upiId.endsWith(suffix)) {
                return true;
            }
        }

        return false;
    }


}