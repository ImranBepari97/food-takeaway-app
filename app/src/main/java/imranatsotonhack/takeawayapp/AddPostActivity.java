package imranatsotonhack.takeawayapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class AddPostActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    // UI references.
    private EditText mFoodDesc;
    private EditText mFoodName;
    private EditText mFoodPrice;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private LocationManager locationManager;

    private double lat;
    private double lng;

    private SharedPreferences mPref;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_post);
        // Set up the login form.
        mFoodPrice = (EditText) findViewById(R.id.foodPrice);
        mFoodName = (EditText) findViewById(R.id.foodName);
        mFoodDesc = (EditText) findViewById(R.id.foodDesc);

        mFoodDesc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        database = FirebaseDatabase.getInstance();
        mPref = getSharedPreferences("mPref", MODE_PRIVATE);

//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                displayName = (String) dataSnapshot.child("displayName").getValue();
////                Log.d("dbRead", "Value is: " + displayName);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.w("readError", "Failed to read value.", error.toException());
//            }
//        });
//
        Button mEmailSignInButton = (Button) findViewById(R.id.submit);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        initGPS();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void attemptLogin() {
        // Reset errors.
        mFoodName.setError(null);
        mFoodDesc.setError(null);
        mFoodPrice.setError(null);

        // Store values at the time of the login attempt.
        String foodName = mFoodName.getText().toString();
        String foodDesc = mFoodDesc.getText().toString();
        float foodPrice = Float.parseFloat(mFoodPrice.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(foodDesc)) {
            mFoodDesc.setError("Field required.");
            focusView = mFoodDesc;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(foodName)) {
            mFoodName.setError(getString(R.string.error_field_required));
            focusView = mFoodName;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //attempt login
            uploadPost();
            Log.d("LoginMessage", "We logging in");

        }
    }

    public void uploadPost() {



        //Post stuff
        myRef = database.getReference().child("posts");
        String postID = generatePostID();
        myRef = database.getReference().child("posts").child(postID).child("foodName");
        myRef.setValue(mFoodName.getText().toString());
        myRef = database.getReference().child("posts").child(postID).child("foodDesc");
        myRef.setValue(mFoodDesc.getText().toString());
        myRef = database.getReference().child("posts").child(postID).child("foodPrice");
        myRef.setValue(mFoodPrice.getText().toString());
        myRef = database.getReference().child("posts").child(postID).child("lat");
        myRef.setValue(lat);
        myRef = database.getReference().child("posts").child(postID).child("lng");
        myRef.setValue(lng);
        myRef = database.getReference().child("posts").child(postID).child("uid");
        myRef.setValue(mPref.getString("userID", "null"));
    }

    public String generatePostID() {
        return myRef.push().getKey();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFoodPrice.setVisibility(show ? View.GONE : View.VISIBLE);
            mFoodPrice.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFoodPrice.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mFoodName.setVisibility(show ? View.VISIBLE : View.GONE);
            mFoodName.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFoodName.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mFoodName.setVisibility(show ? View.VISIBLE : View.GONE);
            mFoodPrice.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AddPost Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void initGPS() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "Location check failed.");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        Log.d("LOCATION", "pls");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    @Override
                    public void onProviderEnabled(String provider) {}

                    @Override
                    public void onProviderDisabled(String provider) {}
                });


    }

}

