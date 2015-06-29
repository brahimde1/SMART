package ie.teamchile.smartapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ie.teamchile.smartapp.R;
import ie.teamchile.smartapp.model.BaseModel;
import ie.teamchile.smartapp.model.PostingData;
import ie.teamchile.smartapp.util.SmartApi;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {
    private String username, password;
    private Button btnLogin;
    private TextView tvUsername, tvPassword, tvAbout;
    private Intent intent;
    private ProgressDialog pd;
    private SmartApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new ButtonClick());
        tvUsername = (TextView) findViewById(R.id.et_username);
        tvPassword = (TextView) findViewById(R.id.et_password);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvAbout.setOnClickListener(new ButtonClick());

        initRetrofit();
    }

    private void initRetrofit() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SmartApi.BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient())
                .build();

        api = restAdapter.create(SmartApi.class);
    }

    private void doRetrofit() {
        PostingData login = new PostingData();
        login.postLogin(username, password);

        api.postLogin(login, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {
                BaseModel.getInstance().setLogin(baseModel.getLogin());
                BaseModel.getInstance().setLoginStatus(true);
                pd.dismiss();
                intent = new Intent(LoginActivity.this, QuickMenuActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null) {
                    BaseModel body = (BaseModel) error.getBodyAs(BaseModel.class);
                    Log.d("Retrofit", "retro error = " + body.getError().getError());
                    Toast.makeText(LoginActivity.this, body.getError().getError(), Toast.LENGTH_LONG).show();
                }
                pd.dismiss();
            }
        });
    }

    private void getCredentials() {
        username = tvUsername.getText().toString();
        password = tvPassword.getText().toString();
    }

    private class ButtonClick implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Logging In");
                    pd.setCanceledOnTouchOutside(false);
                    pd.setCancelable(false);
                    pd.show();
                    getCredentials();
                    doRetrofit();
                    break;
                case R.id.tv_about:
                    Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
