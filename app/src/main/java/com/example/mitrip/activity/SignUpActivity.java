package com.example.mitrip.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    CircleImageView profile_image;
    private static int RESULT_LOAD_PROFILEIMAGE = 101;
    Uri uriProfileImage;
    String profileImageURL;
    StorageReference storageReference;

    TextView txtViewAlreadyRegistered;

    EditText editTxtEmailSignUp, editTxtUserName, editTxtPasswordSignUp, editTxtRePasswordSignUp, editTxtPhone;

    Button btnSignUp;

    ProgressBar progressBarImg, progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        profile_image = findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfileImage();
            }
        });

        txtViewAlreadyRegistered = findViewById(R.id.txtViewAlreadyRegistered);

        txtViewAlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        editTxtEmailSignUp = findViewById(R.id.editTxtEmailSignUp);
        editTxtUserName = findViewById(R.id.editTxtUserName);
        editTxtPasswordSignUp = findViewById(R.id.editTxtPasswordSignUp);
        editTxtRePasswordSignUp = findViewById(R.id.editTxtRePasswordSignUp);
        editTxtPhone = findViewById(R.id.editTxtPhone);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBarImg = findViewById(R.id.progressBarImg);
        progressBarImg.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void getProfileImage() {
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_LOAD_PROFILEIMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_PROFILEIMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            if (isNetworkConnected()) {
                profile_image.setImageURI(uriProfileImage);
                uploadImageToFirebaseStorage();
            } else {
                new AlertDialog.Builder(SignUpActivity.this)
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

    private void uploadImageToFirebaseStorage() {
        storageReference = FirebaseStorage.getInstance().getReference("profilePics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            progressBarImg.setVisibility(View.VISIBLE);
            storageReference.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            progressBarImg.setVisibility(View.GONE);
                            Task<Uri> downloadURL = storageReference.getDownloadUrl();
                            downloadURL.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageURL = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarImg.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            // display error toast
            Toast.makeText(SignUpActivity.this, R.string.noFileSelected, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void registerUser() {
        if (editTxtEmailSignUp.getText().toString().trim().isEmpty()) {
            editTxtEmailSignUp.setError(getText(R.string.typeYourEmail));
            editTxtEmailSignUp.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(editTxtEmailSignUp.getText().toString().trim()).matches()) {
            editTxtEmailSignUp.setError(getText(R.string.typeEmailAddressCorrectly));
            editTxtEmailSignUp.requestFocus();
            return;
        } else if (editTxtUserName.getText().toString().trim().isEmpty()) {
            editTxtUserName.setError(getText(R.string.typeYourUserName));
            editTxtUserName.requestFocus();
            return;
        } else if (editTxtPasswordSignUp.getText().toString().trim().isEmpty()) {
            editTxtPasswordSignUp.setError(getText(R.string.typeYourPassword));
            editTxtPasswordSignUp.requestFocus();
            return;
        } else if (editTxtPasswordSignUp.getText().toString().trim().length() < 6) {
            editTxtPasswordSignUp.setError(getText(R.string.passwordLength));
            editTxtPasswordSignUp.requestFocus();
            return;
        } else if (editTxtRePasswordSignUp.getText().toString().trim().isEmpty()) {
            editTxtRePasswordSignUp.setError(getText(R.string.confirmYourPassword));
            editTxtRePasswordSignUp.requestFocus();
            return;
        } else if (editTxtPhone.getText().toString().trim().isEmpty()) {
            editTxtPhone.setError(getText(R.string.typeYourPhoneNumber));
            editTxtPhone.requestFocus();
            return;
        } else if (editTxtPhone.getText().toString().trim().length() < 11 || editTxtPhone.getText().toString().trim().length() == 12 || editTxtPhone.getText().toString().trim().length() > 13) {
            editTxtPhone.setError(getText(R.string.enterValidPhoneNumber));
            editTxtPhone.requestFocus();
            return;
        } else if (!editTxtPasswordSignUp.getText().toString().trim().equals(editTxtRePasswordSignUp.getText().toString().trim())) {
            new AlertDialog.Builder(SignUpActivity.this)
                    .setTitle(R.string.error)
                    .setMessage(R.string.errorMsgPasswordIncompatible)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setIcon(R.drawable.cancel)
                    .show();
            return;
        } else {
            if (isNetworkConnected()) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(editTxtEmailSignUp.getText().toString().trim(), editTxtPasswordSignUp.getText().toString().trim())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null && profileImageURL != null) {
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(editTxtUserName.getText().toString().trim())
                                                .setPhotoUri(Uri.parse(profileImageURL))
                                                .build();

                                        currentUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(SignUpActivity.this, "Profile photo updated successfully", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }

                                    mAuth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpActivity.this, R.string.registration_success, Toast.LENGTH_LONG).show();
                                                        editTxtEmailSignUp.setText("");
                                                        editTxtUserName.setText("");
                                                        editTxtPasswordSignUp.setText("");
                                                        editTxtRePasswordSignUp.setText("");
                                                        editTxtPhone.setText("");

                                                        // go to login activity
                                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    // If sign in fails, display a message to the user.
                                    new AlertDialog.Builder(SignUpActivity.this)
                                            .setTitle(R.string.error)
                                            .setMessage(R.string.errorMsgAuthenticationFailed)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .setIcon(R.drawable.cancel)
                                            .show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    new AlertDialog.Builder(SignUpActivity.this)
                                            .setTitle(R.string.error)
                                            .setMessage(R.string.commentOnPlayStore)
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
                new AlertDialog.Builder(SignUpActivity.this)
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
