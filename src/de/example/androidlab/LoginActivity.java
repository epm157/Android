package de.example.androidlab;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	Authentication authentication;
	EditText textEditUser;
	EditText textEditPass;
	public  final Activity mcontext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button login = (Button) findViewById(R.id.btn_login);
		login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	authentication = new Authentication(mcontext);
            	authentication.prepareAccessToken();
            	/*Intent i = new Intent(getBaseContext(),DBRoulette.class);
                startActivity(i);
                finish();*/
            }
        });
	}


}
