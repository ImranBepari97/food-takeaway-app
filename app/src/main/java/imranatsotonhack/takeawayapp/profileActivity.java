package imranatsotonhack.takeawayapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences mPref;
    private String uid;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences("mPref", MODE_PRIVATE);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users").child(mPref.getString("userID", ""));
        Log.d("ReferenceURL", myRef.toString());

        final TextView displayNameField = (TextView) findViewById(R.id.displayNameField);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName = (String) dataSnapshot.child("displayName").getValue();
                displayNameField.setText(displayName);
                Log.d("dbRead", "Value is: " + displayName);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("readError", "Failed to read value.", error.toException());
            }
        });




        TextView email = (TextView) findViewById(R.id.emailField);
        email.setText(mPref.getString("email", "email not found somehow"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });



    }

}
