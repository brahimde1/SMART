
package ie.teamchile.smartapp;

import java.util.Calendar;

import utility.AppointmentSingleton;
import utility.ClinicSingleton;
import utility.ServiceOptionSingleton;
import utility.ServiceProviderSingleton;
import utility.ToastAlert;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import connecttodb.GetToken;

public class LoginActivity extends Activity {
	private String username, password;
	private Button loginButton;
	private TextView usernameTextView, passwordTextView, about;
	private GetToken getToken = new GetToken();
	private Intent intent;
	private ProgressDialog pd;
	private ToastAlert ta;
	private Calendar cal = Calendar.getInstance();
	//private ConnectivityTester testConn = new ConnectivityTester(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		//testConn.testTheNetworkConnection();
		//Log.d("MYLOG", "is 3g connected: " + testConn.is3GConnected());
		//Log.d("MYLOG", "is WiFi connected: " + testConn.isWifiConnected());
		
		Log.d("MYLOG", "Time is: " + cal.getTime());

		loginButton = (Button) findViewById(R.id.login);
		loginButton.setOnClickListener(new ButtonClick());
		usernameTextView = (TextView) findViewById(R.id.username);
		passwordTextView = (TextView) findViewById(R.id.password);
		
		about = (TextView) findViewById(R.id.about);
	    about.setMovementMethod(LinkMovementMethod.getInstance());
	    
	}
    private class ButtonClick implements View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login:
				getCredentials();

				new LongOperation().execute((String[]) null);
				Log.d("MYLOG", "Button Clicked");
			}
		}
	}
	private class LongOperation extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			ta = new ToastAlert(getBaseContext(), "Loading data. . . ", false);
		}
		protected String doInBackground(String... params) {
			getToken.getToken(username, password);            
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			Log.d("MYLOG", "On progress update");
		}
		@Override
        protected void onPostExecute(String result) {
            checkCredentials();
        }
	}
	private void getCredentials() {
		username = usernameTextView.getText().toString();
		password = passwordTextView.getText().toString();
	}
    private void checkCredentials(){    
    	if(getToken.getResponseCode() != null && !getToken.getResponseCode().isEmpty()) {
    		if (getToken.getResponseCode().equals("201")){
    			
    			pd = new ProgressDialog(LoginActivity.this);
    			pd.setMessage("Updating Information");
    			pd.show();
    			
        		// update Singleton
        		AppointmentSingleton.getInstance().updateLocal(this);
    			ClinicSingleton.getInstance().updateLocal(this);
    			ServiceOptionSingleton.getInstance().updateLocal(this, pd);
    			
        		ServiceProviderSingleton.getInstance().setLoggedIn(true);
        		ServiceProviderSingleton.getInstance().setUsername(username);
        		ServiceProviderSingleton.getInstance().setPassword(password); 
        		
        		Toast.makeText(this, "Welcome " + username, Toast.LENGTH_LONG).show();
        		
        		intent = new Intent(LoginActivity.this, QuickMenuActivity.class);
				startActivity(intent);
        	} else
        		Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
    	}else {
    		ta = new ToastAlert(LoginActivity.this, "Poor Internet Activity \nPlease check your settings", true);
    	}
	}
    @Override
    public void onBackPressed() {
    	if(ServiceProviderSingleton.getInstance().isLoggedIn()) {
    		ta = new ToastAlert(getBaseContext(), "Already logged in, \n  logout?", true);
    	}else { 
			finish();   		
    	}    	
    }
}