package com.example.moengagetask.activity;

import static android.text.TextUtils.isEmpty;

import static com.example.moengagetask.utils.Constant.RegisterEvent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.moengagetask.R;
import com.example.moengagetask.model.User;
import com.example.moengagetask.utils.Constant;
import com.example.moengagetask.utils.MyTextWatcher;
import com.example.moengagetask.utils.NetworkUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moengage.core.Properties;
import com.moengage.core.analytics.MoEAnalyticsHelper;
import com.moengage.firebase.MoEFireBaseHelper;

import java.util.Date;

public class EntryActivity extends AppCompatActivity {

    private static final String TAG = "EntryActivity";
    private TextInputLayout TLemailID, TLpassword, TLconfPassword, TLuserName;
    private EditText ETemailID, ETpassword, ETconfPassword, ETuserName;
    private String type, emailID, password, confirmPassword, userName;
    private MyTextWatcher myTextWatcher = new MyTextWatcher();
    private Button BTentry;
    private TextView TVtoLogin;
    protected boolean doubleBackToExitPressedOnce = false;
    private boolean isTextWatcherAdded = true;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String firebaseToken = "";
    private ProgressBar mProgressBar;
    //Firebase Auth
    private FirebaseFirestore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        type = getString(R.string.signup);
        EntryActivity.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        hideSoftKeyboard();
        getSupportActionBar().hide();
        mDb = FirebaseFirestore.getInstance();
        getFirebaseToken();

        TLemailID = (TextInputLayout) findViewById(R.id.layoutLoginEmail);
        TLpassword = (TextInputLayout) findViewById(R.id.layouLoginPwd);
        TLconfPassword = (TextInputLayout) findViewById(R.id.layouLoginConfPwd);
        TLuserName = (TextInputLayout) findViewById(R.id.layoutLoginUserName);
        ETuserName = (EditText) findViewById(R.id.input_loginUserName);
        ETemailID = (EditText) findViewById(R.id.input_loginemail);
        ETpassword = (EditText) findViewById(R.id.input_loginpassword);
        ETconfPassword = (EditText) findViewById(R.id.input_loginconfpassword);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        BTentry = (Button) findViewById(R.id.btn_entry);
        TVtoLogin = findViewById(R.id.txt_toLogin);
        setupFirebaseAuth();
//        type = getString(R.string.signup);
//        changeUI(type);

        TVtoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equalsIgnoreCase(getString(R.string.signup)))
                    type = getString(R.string.login);
                else
                    type = getString(R.string.signup);
                changeUI(type);
            }
        });
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(EntryActivity.this, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String newToken) {
                Log.e("FirebaseToken", newToken);
                firebaseToken = newToken;
            }
        });
    }

    private void changeUI(String type) {
        if (type.equalsIgnoreCase(getString(R.string.signup))) {
            BTentry.setText(getString(R.string.signup));
            TVtoLogin.setText(getString(R.string.toLogin));
            TLconfPassword.setVisibility(View.VISIBLE);
            ETconfPassword.setVisibility(View.VISIBLE);
            TLuserName.setVisibility(View.VISIBLE);
            ETuserName.setVisibility(View.VISIBLE);

        } else if (type.equalsIgnoreCase(getString(R.string.login))) {
            BTentry.setText(getString(R.string.login));
            TVtoLogin.setText(getString(R.string.toSignUp));
            TLconfPassword.setVisibility(View.GONE);
            ETconfPassword.setVisibility(View.GONE);
            TLuserName.setVisibility(View.GONE);
            ETuserName.setVisibility(View.GONE);

        }
    }

    private void addTextWatcher() {
        if (!isTextWatcherAdded) {
            ETemailID.addTextChangedListener(new MyTextWatcher(ETemailID, TLemailID, EntryActivity.this, type));
            ETpassword.addTextChangedListener(new MyTextWatcher(ETpassword, TLpassword, EntryActivity.this, type));
            ETconfPassword.addTextChangedListener(new MyTextWatcher(ETconfPassword, ETpassword, TLconfPassword, TLpassword, EntryActivity.this));
            ETuserName.addTextChangedListener(new MyTextWatcher(ETuserName, TLuserName, EntryActivity.this, type));
            isTextWatcherAdded = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        //snackBarClass.alertBoxMessage(Message.exitMessage);
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void onClickEntry(View view) {
        int id = view.getId();
        if (NetworkUtil.getConnectivityStatusString(getApplicationContext())) {

            if (id == R.id.btn_entry) {
                addTextWatcher();
                if (type.equalsIgnoreCase(getString(R.string.signup))) {
                    if (myTextWatcher.validateUsername(ETuserName, TLuserName, EntryActivity.this)) {

                        if (myTextWatcher.validateEmail(ETemailID, TLemailID, EntryActivity.this)) {
                            if (myTextWatcher.validatePassword(ETpassword, TLpassword, EntryActivity.this)) {
                                if (myTextWatcher.validateConfirmPassword(ETconfPassword, ETpassword, TLconfPassword, TLpassword, EntryActivity.this)) {

                                    emailID = ETemailID.getText().toString().trim();
                                    password = ETpassword.getText().toString().trim();
                                    confirmPassword = ETconfPassword.getText().toString().trim();
                                    userName = ETuserName.getText().toString().trim();

                                    if (password.equals(confirmPassword)) {
                                        registerNewEmail(emailID, password);
                                    } else {
                                        Toast.makeText(EntryActivity.this, "Password Mismatch", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                }  else if (type.equalsIgnoreCase(getString(R.string.login))) {
                    if (myTextWatcher.validateEmail(ETemailID, TLemailID, EntryActivity.this)) {
                        if (myTextWatcher.validateLoginPassword(ETpassword, TLpassword, EntryActivity.this)) {
                            emailID = ETemailID.getText().toString().trim();
                            password = ETpassword.getText().toString().trim();
                            signIn();
                        }
                    }
                }
            }

        } else {
            Toast.makeText(this, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerNewEmail(final String email, String password) {

        showDialog();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //insert some default data
                            User user = new User();
                            user.setEmail(email);
                            user.setUsername(userName);
                            user.setUser_id(FirebaseAuth.getInstance().getUid());

                            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                    .build();
                            mDb.setFirestoreSettings(settings);

                            DocumentReference newUserRef = mDb
                                    .collection(getString(R.string.collection_users))
                                    .document(FirebaseAuth.getInstance().getUid());

                            newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideDialog();

                                    if (task.isSuccessful()) {
                                        setMoEnagageTrackRegisterEvent((FirebaseAuth.getInstance().getUid()));
                                        type = getString(R.string.login);
                                        changeUI(type);
                                    } else {
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }
                });
    }

    private void setMoEnagageTrackRegisterEvent(String userId) {
        Properties properties = new Properties();
        properties.addAttribute("Register", userId)
                .addAttribute("Date", new Date())
                .addDateIso("attributeDateIso", "2022-02-10T21:12:00Z");
        MoEAnalyticsHelper.INSTANCE.trackEvent(getApplicationContext(), RegisterEvent, properties);
    }

    private void signIn() {
        Log.d(TAG, "onClick: attempting to authenticate.");
        showDialog();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(ETemailID.getText().toString(),
                ETpassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EntryActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(EntryActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    //Informing MoEngage About the Login
                    MoEAnalyticsHelper.INSTANCE.setUniqueId(getApplicationContext(), user.getUid());
                    Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    type = getString(R.string.signup);
                    changeUI(type);
                }
            }
        };
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
