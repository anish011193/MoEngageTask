package com.example.moengagetask.utils;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.moengagetask.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;


public class MyTextWatcher implements TextWatcher {

    private View view;
    private EditText editText, editText1;
    private Activity activity;
    private TextInputLayout textInputLayout, textInputLayout1;
    private static final char space = ' ';
    private int len = 0;
    private String pwdRegx = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[?!';/{(}=@%^|):_&*-,]).{8,15}$";
    private ArrayList<String> listOfPattern = new ArrayList<String>();
    private String type;

    public MyTextWatcher(EditText editText, TextInputLayout textInputLayout, Activity activity, String type) {

        this.editText = editText;
        this.activity = activity;
        this.textInputLayout = textInputLayout;
        this.type = type;
    }

    public MyTextWatcher(EditText editText, EditText editText1, TextInputLayout textInputLayout, TextInputLayout textInputLayout1, Activity activity) {

        this.editText = editText;
        this.editText1 = editText1;
        this.activity = activity;
        this.textInputLayout = textInputLayout;
        this.textInputLayout1 = textInputLayout1;
    }

    public MyTextWatcher() {

    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    public void afterTextChanged(Editable editable) {
        switch (editText.getId()) {

            case R.id.input_loginUserName:
                validateUsername(editText, textInputLayout, activity);
                break;
            case R.id.input_loginemail:
                validateEmail(editText, textInputLayout, activity);
                break;
            case R.id.input_loginpassword:
                if (type.equalsIgnoreCase(activity.getString(R.string.signup)))
                    validatePassword(editText, textInputLayout, activity);
                else if (type.equalsIgnoreCase(activity.getString(R.string.login)))
                    validateLoginPassword(editText, textInputLayout, activity);
                break;
            case R.id.input_loginconfpassword:
                validateConfirmPassword(editText, editText1, textInputLayout, textInputLayout1, activity);
                break;
        }
    }

    private void requestFocus(View view, Activity activity) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean validateUsername(EditText editText, TextInputLayout TLayout, Activity activity) {
        String name = editText.getText().toString().trim();

        if (name.length() < 3 || name.length() == 0) {
            TLayout.setError("Enter valid Username");
            requestFocus(editText, activity);
            return false;
        } else if (!name.matches("[a-zA-Z.? ]*")) {
            TLayout.setError("No special characters are allowed");
            requestFocus(editText, activity);
            return false;
        } else {
            TLayout.setErrorEnabled(false);
        }
        return true;
    }


    public boolean validatePassword(EditText editText, TextInputLayout TLayout, Activity activity) {
        String name = editText.getText().toString().trim();
        if (!name.matches(pwdRegx)) {
            TLayout.setError("Invalid Password");
            requestFocus(editText, activity);
            return false;
        }
        else {
            TLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateLoginPassword(EditText editText, TextInputLayout TLayout, Activity activity) {
        String name = editText.getText().toString().trim();
        int c = name.length();
        if (!((c > 7) && (c < 16))) {
            TLayout.setError("Password must be 8 to 15 letters");
            requestFocus(editText, activity);
            return false;
        } else {
            TLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateConfirmPassword(EditText editText, EditText editText1, TextInputLayout TLayout,
                                           TextInputLayout TLayout1, Activity activity) {
        String password = editText.getText().toString().trim();
        String password1 = editText1.getText().toString().trim();
        if (!password.matches(password1)) {
            TLayout.setError("Password Mis-Match");
            requestFocus(editText, activity);
            return false;
        }
        else {
            TLayout.setErrorEnabled(false);
        }

        return true;
    }


    public boolean validateMobileNumber(EditText editText, TextInputLayout TLayout, Activity activity) {
        String mnumber = editText.getText().toString().trim();

        if (mnumber.length() == 0 || mnumber.length() != 8) {
            TLayout.setError("Enter valid Mobile Number");
            requestFocus(editText, activity);
            return false;
        } else {

            TLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validateEmail(EditText editText, TextInputLayout textInputLayout, Activity activity) {
        String emailvalidation = editText.getText().toString().trim();

        if (emailvalidation.isEmpty() || !isValidEmail(emailvalidation)) {

            textInputLayout.setError("Enter valid Email Address");
            requestFocus(editText, activity);
            return false;
        } else {

            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean isPasswordValid(EditText password, TextInputLayout textInputLayout, Activity activity) {
        String pa = password.getText().toString();

        boolean isValid = false;
        if (pa.length() >= 4) {
            isValid = true;
            textInputLayout.setErrorEnabled(false);
        } else {
            textInputLayout.setError("please give correct password");
            requestFocus(password, activity);
        }
        return isValid;
    }
}