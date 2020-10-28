package com.sunilkumar.omlchattask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sunilkumar.omlchattask.databinding.ActivityRegisterBinding;
import com.sunilkumar.omlchattask.form_verification.VerifyUserDetails;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ActivityRegisterBinding registerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        // getting layout views
        final TextInputLayout email = registerBinding.editTextRegisterEmail;
        final TextInputLayout username = registerBinding.editTextLoginUsername;
        final TextInputLayout pass = registerBinding.editTextLoginPassword;
        TextInputLayout cnfPass = registerBinding.textRcnfPass;
        TextInputLayout phone = registerBinding.textRPhone;
        MaterialButton buttonRegister = registerBinding.button;


         // TextWatcher to check new user signup form details
        email.getEditText().addTextChangedListener(new VerifyUserDetails(registerBinding, email));
        username.getEditText().addTextChangedListener(new VerifyUserDetails(registerBinding, username));
        pass.getEditText().addTextChangedListener(new VerifyUserDetails(registerBinding, pass));
        cnfPass.getEditText().addTextChangedListener(new VerifyUserDetails(registerBinding, cnfPass));
        phone.getEditText().addTextChangedListener(new VerifyUserDetails(registerBinding, phone));


        registerBinding.textViewLoginIfReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uname = username.getEditText().getText().toString();
                String password = pass.getEditText().getText().toString();
                String emailid = email.getEditText().getText().toString();
                if(TextUtils.isEmpty(uname) || TextUtils.isEmpty(password) || TextUtils.isEmpty(emailid))
                {
                    Toast.makeText(RegisterActivity.this, "Can't leave blanks!", Toast.LENGTH_SHORT).show();
                } else
                {
                    registerNewUser(uname, emailid, password);
                }
            }
        });

    }

    private void registerNewUser(final String username, String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", username);
                            hashMap.put("typingStatus", "noOne");
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "Offline");
                            // after successful user registeration
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else
                        {
                            Toast.makeText(RegisterActivity.this, "Invalid Details!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}