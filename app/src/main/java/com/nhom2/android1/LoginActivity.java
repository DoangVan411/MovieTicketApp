package com.nhom2.android1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private EditText editEmail;
    private EditText editPassword;
    private TextView textAuthStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseMovieRepository.seedSampleData(firestore);
        FirebaseMovieRepository.ensureNotificationChannel(this);

        bindViews();

        findViewById(R.id.buttonLogin).setOnClickListener(v -> login());
        findViewById(R.id.buttonRegister).setOnClickListener(v -> register());

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            updateUiForSignedIn(user);
            navigateToMovieList();
        } else {
            updateUiForSignedOut();
        }
    }

    private void bindViews() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        textAuthStatus = findViewById(R.id.textAuthStatus);
    }

    private void login() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.enter_email_password, Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    updateUiForSignedIn(result.getUser());
                    navigateToMovieList();
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void register() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.enter_email_password, Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user != null) {
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("email", user.getEmail());
                        payload.put("createdAt", System.currentTimeMillis());
                        firestore.collection("users").document(user.getUid()).set(payload);
                        updateUiForSignedIn(user);
                        navigateToMovieList();
                    }
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void navigateToMovieList() {
        Intent intent = new Intent(this, MovieListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void updateUiForSignedIn(@NonNull FirebaseUser user) {
        textAuthStatus.setText(getString(R.string.signed_in_as, user.getEmail()));
        pushFcmTokenToUser(user.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", user.getEmail());
        updates.put("lastLoginAt", System.currentTimeMillis());
        firestore.collection("users").document(user.getUid()).set(updates);
    }

    private void updateUiForSignedOut() {
        textAuthStatus.setText(R.string.not_signed_in);
    }

    private void pushFcmTokenToUser(String uid) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            Map<String, Object> update = new HashMap<>();
            update.put("fcmToken", token);
            firestore.collection("users").document(uid).update(update);
        });
    }
}
