package com.vrnitsolution.healthapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vrnitsolution.healthapp.Admin.AdminLogin;
import com.vrnitsolution.healthapp.DoctorUI.DoctorDashboard;
import com.vrnitsolution.healthapp.DoctorUI.IntroActivity;
import com.vrnitsolution.healthapp.model.Coordinates;


public class LoginActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {


    FirebaseAuth firebaseAuth;

    ImageView menu1;
    Coordinates coordinates;
    private boolean toggle = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String token = "";
    private LocationManager locationManager;
    ProgressDialog progressDialog;
    ImageView togglebutton;
    EditText userPassword, email;

    private static final String LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String LOCATION_COARSE = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSION_REQ_CODE = 1005;
    private CollectionReference chatCollection = db.collection("chatusers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        coordinates=new Coordinates("18.4630400","73.83800040");

        togglebutton = findViewById(R.id.togglepassword);
        userPassword = findViewById(R.id.userPassword);
        email = findViewById(R.id.email);

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();


        menu1 = findViewById(R.id.menu_1);
        registerForContextMenu(menu1);

        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // Set your message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by pressing back button


        togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle == false) {
                    toggle = true;
                    togglebutton.setImageResource(R.drawable.baseline_visibility_on);
                    userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    toggle = false;
                    togglebutton.setImageResource(R.drawable.baseline_visibility_off);
                    userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.menu1, popup.getMenu());
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menuForAdminLogin:
                startActivity(new Intent(this, AdminLogin.class));
                return true;

            case R.id.menuForDoctorLogin:
                finish();
                startActivity(new Intent(LoginActivity.this, IntroActivity.class));
                return true;
        }

        return false;
    }



    /**
     * Update the coordinates of that user according to userid if match found in collection
     *
     * @param uid
     * @param coordinates
     */

    private void updateTheCoordinates(String uid, Coordinates coordinates) {
        //Update Logic here
        db.collection("users")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Update the coordinates field for the matching user
                                document.getReference().update("currentCoordinates", coordinates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                displayToast("Location updated successfully");
                                                startActivity(new Intent(LoginActivity.this, DashboardHome.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Failed to update coordinates", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            progressDialog.dismiss();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        SharedPreferences preferences = getSharedPreferences("DoctorPrefs", MODE_PRIVATE);

        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            finish();
            finishAffinity();
            startActivity(new Intent(LoginActivity.this, DashboardHome.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (preferences != null) {
            // Retrieve doctor data from SharedPreferences
            String doctorId = preferences.getString("doctorId", "");
            String email = preferences.getString("email", "");
            if (!doctorId.isEmpty() && !email.isEmpty()) {
                finish();
                finishAffinity();
                startActivity(new Intent(LoginActivity.this, DoctorDashboard.class));
            }
        }


    }


    public void loginUser(View view) {
        String useremail = email.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (useremail.isEmpty() && password.isEmpty()) {
            displayToast("credentials can't be blank");
        }  else {
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(useremail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    progressDialog.dismiss();
                    displayToast("Login Success !");
                    FirebaseUser loggeduser = firebaseAuth.getCurrentUser();
                    updateTheCoordinates(loggeduser.getUid(), coordinates);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    displayToast("" + e.getMessage().toString());
                }
            });
        }
    }

    public void registerForUser(View view) {
        startActivity(new Intent(LoginActivity.this, UserRegistration.class));
    }


}