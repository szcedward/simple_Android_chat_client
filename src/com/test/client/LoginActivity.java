package com.test.client;



import com.test.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Zhongchen Shen
 *
 */
public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button linkButton = (Button) this.findViewById(R.id.linkButton);
		final EditText ipAddr = (EditText) this.findViewById(R.id.ipAddr);
		final Intent intent = new Intent();
		linkButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				intent.setClass(LoginActivity.this, ClientActivity.class);
				intent.putExtra("com.test.client.IPaddr", ipAddr.getText().toString());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				LoginActivity.this.finish();
				startActivity(intent);
			}
		});
		
	}


}
