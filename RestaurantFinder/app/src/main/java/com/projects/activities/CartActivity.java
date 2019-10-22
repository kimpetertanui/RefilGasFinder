package com.projects.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.apps.gasfinder.R;

public class CartActivity extends AppCompatActivity {


    private Button cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        cartButton = (Button) findViewById(R.id.pay);



        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this,MpesaActivity.class);
                startActivity(intent);
            }
        });
    }


    // .......start of backpess............
    @Override
    public void onResume() {
        super.onResume();


        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.cart);


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
