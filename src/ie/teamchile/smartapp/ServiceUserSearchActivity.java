package ie.teamchile.smartapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import connecttodb.AccessDBTable;

public class ServiceUserSearchActivity extends MenuInheritActivity {

	private EditText searchParams;
	private Button search, searchResult1, searchResult2,searchResult3;
	private String hospitalNumber, name, dob, email, mobileNumber, road,
            county, postCode, nextOfKinName, nextOfKinContactNumber, gestation, parity,deliveryDate, bloodGroup, rhesus,
            obstetricHistory;
	private String enteredSearch, first;
	private int arrayPos;
    private String token;

	Connection c;
	Statement stmt;
	ResultSet rs;
	JSONObject json;
	JSONArray query, query2, query3;
	AccessDBTable dbTable = new AccessDBTable();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_user_search);

		searchParams = (EditText) findViewById(R.id.search_params);
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new ButtonClick());
		searchResult1 = (Button) findViewById(R.id.search_result_1);
		searchResult1.setOnClickListener(new ButtonClick());
		searchResult2 = (Button) findViewById(R.id.search_result_2);
		searchResult2.setOnClickListener(new ButtonClick());
		searchResult3 = (Button) findViewById(R.id.search_result_3);
		searchResult3.setOnClickListener(new ButtonClick());
	}
	private class ButtonClick implements View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.search:
				Log.d("MYLOG", "Search Button Pressed");
				enteredSearch = searchParams.getText().toString();
				new LongOperation().execute((String[]) null);
				break;
			case R.id.search_result_1:
				Log.d("MYLOG", "First Result Button Pressed");
				Intent intent = new Intent(ServiceUserSearchActivity.this, ServiceUserActivity.class);
				intent.putExtra("hospital_number", hospitalNumber);
                intent.putExtra("name", name);
                intent.putExtra("dob", dob);
                intent.putExtra("gestation", gestation);
                intent.putExtra("parity", parity);
                intent.putExtra("email", email);
				intent.putExtra("mobile_number", mobileNumber);
				intent.putExtra("road", road);
				intent.putExtra("county", county);
				intent.putExtra("post_code", postCode);
				intent.putExtra("next_of_kin_name", nextOfKinName);
				intent.putExtra("next_of_kin_phone", nextOfKinContactNumber);
				intent.putExtra("deliveryDate", deliveryDate);
				intent.putExtra("bloodGroup", bloodGroup);
				intent.putExtra("rhesus", rhesus);
                intent.putExtra("obstetricHistory", obstetricHistory);
		        startActivity(intent);
				break;
			case R.id.search_result_2:
				break;
			case R.id.search_result_3:
				break;
			}
		}
	}
	public int getObjects(JSONArray obj, String key, String val) {
		for(int i = 0; i < obj.length(); i++){
			try {
				if(((JSONObject) ((JSONObject) obj.get(i)).get("personal_fields")).get(key).equals(val)){
					return i;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	    return 0;
	}
	public class LongOperation extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
		}
		protected String doInBackground(String... params) {
			Log.d("MYLOG", "ServiceUserSearch DoInBackground");
			String dbQuery = dbTable.accessDB("service_users");
			try {
				json = new JSONObject(dbQuery);
				query = json.getJSONArray("service_users");
				query2 = json.getJSONArray("pregnancies");
				
				arrayPos = getObjects(query, "name", enteredSearch);

				first = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("name")).toString();
				hospitalNumber = (((JSONObject) query.get(arrayPos)).get("hospital_number")).toString();
                name = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("name")).toString();
                dob = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("dob")).toString();
				email = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("email")).toString();
				mobileNumber = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("mobile_phone")).toString();
				road = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("home_address")).toString();
				county = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("home_county")).toString();
				postCode = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("home_post_code")).toString();
				nextOfKinName = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("next_of_kin_name")).toString();
				nextOfKinContactNumber = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("personal_fields")).get("next_of_kin_phone")).toString();

				gestation = ((JSONObject) query2.get(0)).get("gestation").toString();
				deliveryDate = ((JSONObject) query2.get(0)).get("estimated_delivery_date").toString();
				
				
				parity = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("clinical_fields")).get("parity")).toString();
				bloodGroup = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("clinical_fields")).get("blood_group")).toString();
				rhesus = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("clinical_fields")).get("rhesus")).toString();
				obstetricHistory = (((JSONObject) ((JSONObject) query.get(arrayPos)).get("clinical_fields")).get("previous_obstetric_history")).toString();


			

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			Log.d("MYLOG", "On progress update");
		}
		@Override
		protected void onPostExecute(String result) {
            Log.d("MYLOG", "onPostExecute");
            searchResult1.setText(first);
            ServiceUserSearchActivity.this.hospitalNumber = hospitalNumber.toString();
            ServiceUserSearchActivity.this.name = name.toString();
            ServiceUserSearchActivity.this.dob = dob.toString();
            ServiceUserSearchActivity.this.email = email.toString();
            ServiceUserSearchActivity.this.mobileNumber = mobileNumber.toString();
            ServiceUserSearchActivity.this.road = road.toString();
            ServiceUserSearchActivity.this.county = county.toString();
            ServiceUserSearchActivity.this.postCode = postCode.toString();
            ServiceUserSearchActivity.this.nextOfKinName = nextOfKinName.toString();
            ServiceUserSearchActivity.this.nextOfKinContactNumber = nextOfKinContactNumber.toString();
            ServiceUserSearchActivity.this.gestation = gestation.toString();
            ServiceUserSearchActivity.this.parity=parity.toString();
            ServiceUserSearchActivity.this.deliveryDate=deliveryDate.toString();
            ServiceUserSearchActivity.this.bloodGroup=bloodGroup.toString();
            ServiceUserSearchActivity.this.rhesus=rhesus.toString();
            ServiceUserSearchActivity.this.obstetricHistory=obstetricHistory.toString();


            
		}
	}
}