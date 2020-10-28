package com.sunilkumar.omlchattask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sunilkumar.omlchattask.databinding.ActivityLoginBinding;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ActivityLoginBinding loginBinding;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // keep use logged in if already exists(already registered)
        if(firebaseUser!=null)
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        TextView buttonRegisterLogin = loginBinding.buttonRegisterLogin;
        MaterialButton buttonLogin = loginBinding.button;
        final TextInputLayout editTextLoginUsername = loginBinding.editTextLoginUsername;
        final TextInputLayout editTextLoginPassword = loginBinding.editTextLoginPassword;

        firebaseAuth = FirebaseAuth.getInstance();

        buttonRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_text = editTextLoginUsername.getEditText().getText().toString();
                String pass_text = editTextLoginPassword.getEditText().getText().toString();

                if(TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text))
                {
                    Toast.makeText(LoginActivity.this, "Can't be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loginTheUser(email_text, pass_text);
                }
            }
        });

        editTextLoginUsername.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE)
                {
                    if(TextUtils.isEmpty(editTextLoginPassword.getEditText().getText().toString()))
                    {
                        editTextLoginPassword.requestFocus();
                    }
                }
                return false;
            }
        });

        editTextLoginPassword.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE)
                {
                    if((TextUtils.isEmpty(editTextLoginUsername.getEditText().getText().toString())))
                    {
                        editTextLoginUsername.requestFocus();
                    }
                    else if(TextUtils.isEmpty(editTextLoginPassword.getEditText().getText().toString()))
                    {
                        editTextLoginPassword.requestFocus();
                    }
                    else{
                        loginTheUser(editTextLoginUsername.getEditText().getText().toString(), editTextLoginPassword.getEditText().getText().toString());
                    }
                }
                return false;
            }
        });
    }

    private void loginTheUser(String email_text, String pass_text) {
        firebaseAuth.signInWithEmailAndPassword(email_text, pass_text)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            // user authenticated successfully
                            Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
                            intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentLogin);
                            finish();
                        } else
                        {
                            // if user not registered
                            Toast.makeText(LoginActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // handling keyboard done button feature
}