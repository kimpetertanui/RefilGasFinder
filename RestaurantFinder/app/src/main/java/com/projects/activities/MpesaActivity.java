package com.projects.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.apps.gasfinder.R;


//import butterknife.ButterKnife;


public class MpesaActivity extends AppCompatActivity {


    //@BindView(R.id.editTextPhoneNumber)
    EditText editTextPhoneNumber;
    //@BindView(R.id.sendButton)
    Button mpesasend;


    //Declare Daraja :: Global Variable
    Daraja daraja;


    //String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mpesa );
        //editTextPhoneNumber = findViewById( R.id.editTextPhoneNumber );
        mpesasend = findViewById( R.id.mpesasend );
        //ButterKnife.bind( this );



        //Init Daraja
        //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
        daraja = Daraja.with( "KVaXJB9tUyg5ZDRrxYyW6uwuafXAhFZb", "8EBglFFrTQZayCQd", new DarajaListener<AccessToken>() {
            //oHHh8Ua6iyi4sVduay0saOjWkHebAJmY////VbJsaZDVT9bA5EiW
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i( MpesaActivity.this.getClass().getSimpleName(), accessToken.getAccess_token() );
                Toast.makeText( MpesaActivity.this, "TOKEN : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT ).show();
            }


            @Override
            public void onError(String error) {
                Log.e( MpesaActivity.this.getClass().getSimpleName(), error );
            }
        } );


        //TODO :: THIS IS A SIMPLE WAY TO DO ALL THINGS AT ONCE!!! DON'T DO THIS :)
        mpesasend.setOnClickListener( v -> {


            //Get Phone Number from User Input
            //  phoneNumber = editTextPhoneNum
            //  ber.getText().toString().trim();


//            if (TextUtils.isEmpty( phoneNumber )) {
//                editTextPhoneNumber.setError( "Please Provide a Phone Number" );
//                return;
//            //}


            //TODO :: REPLACE WITH YOUR OWN CREDENTIALS  :: THIS IS SANDBOX DEMO
            LNMExpress lnmExpress = new LNMExpress( "174379", "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                    "850", "254722648834", "174379", "254708995320", "http://meal.shulemall.com/api", "GetGas", "Pay" );

            //This is the
            daraja.requestMPESAExpress( lnmExpress, new DarajaListener<LNMResult>() {
                @Override
                public void onResult(@NonNull LNMResult lnmResult) {
                    Log.i( MpesaActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription );
                }


                @Override
                public void onError(String error) {
                    Log.i( MpesaActivity.this.getClass().getSimpleName(), error );
                }
            } );
        } );
    }

    // .......start of backpess............
    @Override
    public void onResume() {
        super.onResume();


        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle(R.string.confirm);


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
