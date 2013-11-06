package de.example.androidlab;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends CommonActivity {
	Authentication authentication;
	EditText textEditUser;
	EditText textEditPass;
	public  final CommonActivity mcontext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = new Intent(getBaseContext(),DBRoulette.class);
        startActivity(i);
        finish();
		
		
		setContentView(R.layout.activity_login);
//		Button login = (Button) findViewById(R.id.btn_login);
//		login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            	
////            	authentication = new Authentication(mcontext);
////            	authentication.prepareAccessToken();
////            	
////            	String token=authentication.getAccessToken();
////  
////            	
////            	if(true) //TODO: check if token is valid
////            	{
////            		Intent i = new Intent(getBaseContext(),DBRoulette.class);
////                    startActivity(i);
////                    finish();
////            	}
//            	
//            }
//        });
	}


}
