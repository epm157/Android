package de.example.androidlab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class LoginActivity extends CommonActivity {
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

	}


}
