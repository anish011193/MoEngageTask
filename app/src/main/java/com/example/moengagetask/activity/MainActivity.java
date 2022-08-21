package com.example.moengagetask.activity;

import static com.example.moengagetask.utils.Constant.LoginEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moengagetask.R;
import com.example.moengagetask.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moengage.core.MoECoreHelper;
import com.moengage.core.Properties;
import com.moengage.core.analytics.MoEAnalyticsHelper;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore mDb;
    private User user;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = FirebaseFirestore.getInstance();
        //Setting TaskBar Title
        setTitle("Welcome to MoEngage");
        getUserDetails();
    }

    private void setMoEngageTrackUserAttributes() {
        MoEAnalyticsHelper.INSTANCE.setAlias(getApplicationContext(),user.getUser_id());
        MoEAnalyticsHelper.INSTANCE.setUserName(getApplicationContext(),user.getUsername());
        MoEAnalyticsHelper.INSTANCE.setEmailId(getApplicationContext(), user.getEmail());
    }

    private void setMoEnagageTrackLoginEvent(){
        Properties properties = new Properties();
        properties.addAttribute("Login", user.getUser_id())
                .addAttribute("Date", new Date())
                .addDateIso("attributeDateIso", "2022-02-10T21:12:00Z");
        MoEAnalyticsHelper.INSTANCE.trackEvent(getApplicationContext(),LoginEvent, properties);
    }

    private void getUserDetails(){
        DocumentReference userReference = mDb.collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getUid());
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "onComplete: successfully got the user object");
                    user = task.getResult().toObject(User.class);
                    if(user!=null) {
                        setTitle("Welcome to MoEngage, "+user.getUsername());
                        TextView tvUserName = findViewById(R.id.txtUserName);
                        tvUserName.setText("Hello "+user.getUsername()+"\n" + user.getEmail());
                        //MoEngage Track User Attributes
                        setMoEngageTrackUserAttributes();
                        //MoEnagage Track Events
                        setMoEnagageTrackLoginEvent();
                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        //Informing MoEngage About the Logout
        MoECoreHelper.INSTANCE.logoutUser(getApplicationContext());
        Intent intent = new Intent(this, EntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}