package com.sunilkumar.omlchattask.form_verification;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.sunilkumar.omlchattask.R;
import com.sunilkumar.omlchattask.databinding.ActivityRegisterBinding;
import com.sunilkumar.omlchattask.util.PreferencesController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUserDetails implements TextWatcher {

    ActivityRegisterBinding registerBinding;
    private View view;
    String pass;


    public VerifyUserDetails(ActivityRegisterBinding registerBinding, View view) {
        this.registerBinding = registerBinding;
        this.view = view;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        ((TextInputLayout)view.findViewById(R.id.editTextLoginPassword)).setError("Hey!");
    }

    @Override
    public void afterTextChanged(Editable editable) {

        switch (view.getId())
        {
            case R.id.editTextLoginUsername:
                checkUsername(editable);
                return;
            case R.id.editTextLoginPassword:
                checkPass(editable);
                return;
            case R.id.textRcnfPass:
                checkCnfPass(editable);
                return;
            case R.id.textRPhone:
                checkPhoneNo(editable);
                return;
            case R.id.editTextRegisterEmail:
                checkEmailId(editable);
        }
    }

    private void checkEmailId(Editable editable) {
        if(!(editable.toString().contains("@") && editable.toString().contains(".com"))) // lets support only .com for now
            registerBinding.editTextRegisterEmail.setError("Invalid email Id");
        else
            registerBinding.editTextRegisterEmail.setError(null);
    }

    private void checkPhoneNo(Editable editable) {

        // By using regex
        Pattern pattern = Pattern.compile("[6-9][0-9]{9}");  // let's support only number which start from 6/7/8/9
        Matcher matcher = pattern.matcher(editable.toString());
        if(!(matcher.find() && matcher.group().equals(editable.toString())))
            registerBinding.textRPhone.setError("should be 10 digits");
        else
            registerBinding.textRPhone.setError(null);
    }

    private void checkCnfPass(Editable editable) {
       // Toast.makeText(registerBinding.getRoot().getContext(), ""+PreferencesController.getPassField1(registerBinding.getRoot().getContext()), Toast.LENGTH_SHORT).show();

        if(!editable.toString().equals(PreferencesController.getPassField1(registerBinding.getRoot().getContext())))
            registerBinding.textRcnfPass.setError("Password doesn't match");
        else
            registerBinding.textRcnfPass.setError(null);
    }

    private void checkPass(Editable editable) {
        pass = editable.toString();
        PreferencesController.setPassField1(registerBinding.getRoot().getContext(), pass);

        Pattern pattern = Pattern.compile("[a-zA-Z][0-9]");
        Matcher matcher = pattern.matcher(editable.toString());

        if(editable.length() < 6 && !matcher.find())
            registerBinding.editTextLoginPassword.setError("Min length 6 & AlphaNumeric");
        else
            registerBinding.editTextLoginPassword.setError(null);
    }

    private void checkUsername(Editable editable) {

        Pattern pattern = Pattern.compile("[a-zA-Z][0-9]*");
        Matcher matcher = pattern.matcher(editable.toString());

        if(editable.length() < 8 && matcher.find())
            registerBinding.editTextLoginUsername.setError("Min 8 chars long");
        else
            registerBinding.editTextLoginUsername.setError(null);
    }
}
