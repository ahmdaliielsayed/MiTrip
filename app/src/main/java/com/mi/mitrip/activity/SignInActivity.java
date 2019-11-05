package com.mi.mitrip.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mi.mitrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    ImageView ivTwitter, ivLinkedIn, ivFacebook, ivGooglePlus;
    TextView txtViewDonNotHaveAccount;
    EditText editTxtEmailSignIn, editTxtPasswordSignIn;
    ProgressBar progressBar;
    Button btnSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTxtEmailSignIn = findViewById(R.id.editTxtEmailSignIn);
        editTxtPasswordSignIn = findViewById(R.id.editTxtPasswordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        ivTwitter = findViewById(R.id.ivTwitter);
        ivLinkedIn = findViewById(R.id.ivLinkedin);
        ivFacebook = findViewById(R.id.ivFacebook);
        ivGooglePlus = findViewById(R.id.ivGooglePlus);

        txtViewDonNotHaveAccount = findViewById(R.id.txtViewDonNotHaveAccount);

        ivTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivTwitter.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, R.anim.rotate));
            }
        });
        ivLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivLinkedIn.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, R.anim.rotate));
            }
        });
        ivFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivFacebook.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, R.anim.rotate));
            }
        });
        ivGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivGooglePlus.startAnimation(AnimationUtils.loadAnimation(SignInActivity.this, R.anim.rotate));
            }
        });

        txtViewDonNotHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void loginUser() {
        if (editTxtEmailSignIn.getText().toString().isEmpty()) {
            editTxtEmailSignIn.setError(getText(R.string.typeYourEmail));
            editTxtEmailSignIn.requestFocus();
            return;
        } else if (editTxtPasswordSignIn.getText().toString().isEmpty()) {
            editTxtPasswordSignIn.setError(getText(R.string.typeYourPassword));
            editTxtPasswordSignIn.requestFocus();
            return;
        } else {
            if (isNetworkConnected()){
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(editTxtEmailSignIn.getText().toString(), editTxtPasswordSignIn.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    if (mAuth.getCurrentUser().isEmailVerified()) {
                                        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignInActivity.this, R.string.verifyEmail, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    new AlertDialog.Builder(SignInActivity.this)
                                            .setTitle(R.string.error)
                                            .setMessage(R.string.errorMsgSignInFailed)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .setIcon(R.drawable.cancel)
                                            .show();
                                }
                            }
                        });
            } else {
                new AlertDialog.Builder(SignInActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.errorMsgNetworkConnection)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setIcon(R.drawable.cancel)
                        .show();
            }
        }
    }
}
