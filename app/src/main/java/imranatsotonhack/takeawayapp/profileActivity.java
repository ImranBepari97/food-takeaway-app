package imranatsotonhack.takeawayapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private String uid;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences("mPref", MODE_PRIVATE);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/" + mPref.getString("userID", "can't find it"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });

        TextView displayName = (TextView) findViewById(R.id.displayName);
        TextView email = (TextView) findViewById(R.id.emailField);

//        displayName.setText(mPref.getString("name","name"));
        email.setText(mPref.getString("email", "email not found somehow"));
        uid = mPref.getString("userID", "null");
//        Log.d("CHECKINGFORUSERNAME", myRef.get);

    }

}
