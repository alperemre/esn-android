package esn.activities;


import esn.classes.LoginThread;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{

	Intent intent;

	Resources res;

	private Context context;

	public SharedPreferences pref;



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().hide();
		 

		intent = this.getIntent();
		
		context = this;
		
		res = getResources();
		/*TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassgord);

		tvForgotPassword
				.setText(Html
						.fromHtml("<a href=\"http://www.esn.com/forgotpassword\">Forgot your password?</a> "));

		tvForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());*/
		if(intent!=null){
			String loginResult = intent.getStringExtra("loginResult");
			if(loginResult!=null && loginResult.length() > 0){
				Toast.makeText(this, loginResult, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        intent = new Intent(context,WelcomeActivity.class);
	        startActivity(intent);
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void LoginClicked(View view) {

		EditText txtEmail = (EditText) findViewById(R.id.esn_login_Email);
		
		EditText txtPass = (EditText) findViewById(R.id.esn_login_pass);
		String email = txtEmail.getText().toString();
		
		if(email.isEmpty())
		{
			Toast.makeText(context, res.getString(R.string.esn_login_enteremail), Toast.LENGTH_SHORT).show();
			return;
		}
		String password = txtPass.getText().toString();
		
		if(password.isEmpty())
		{
			Toast.makeText(context, res.getString(R.string.esn_login_enterpassword), Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle(res.getString(R.string.esn_global_loading));
		dialog.setMessage(res.getString(R.string.esn_global_pleaseWait));
		dialog.setCancelable(false);
		dialog.show();
		LoginThread loginThread  = new LoginThread(this, email, password, dialog);
	    Intent home = new Intent(this, HomeActivity.class);
		loginThread.setSuccessIntent(home);
		loginThread.start();
	}
}
