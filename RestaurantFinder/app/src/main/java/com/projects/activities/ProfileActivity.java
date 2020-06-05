package com.projects.activities;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.mobitechadslib.MobiAdBanner;
import com.ads.mobitechadslib.MobitechAds;
import com.apps.restaurantfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {


    TextView userInfoEmail;
    Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userInfoEmail = (TextView) findViewById(R.id.userNmaeTextView);

        signOutBtn = (Button) findViewById(R.id.signOutButton);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                Toast.makeText(getApplicationContext(), "Signing Out", Toast.LENGTH_SHORT).show();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            }
        });
        accessUserInformation();

        //showAds();
        MobiAdBanner bannerAd = findViewById(R.id.bannerAd);
        bannerAd.getBannerAds(this,"681530", "5");
        MobitechAds.getIntertistialAd(ProfileActivity.this,"681530", "1");
    }




    public void accessUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();


            userInfoEmail.setText(email);

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }


    }




    // .......start of backpess............
    @Override
    public void onResume() {
        super.onResume();


        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.profile);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //..............end of backpress....................


}