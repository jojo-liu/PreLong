package com.longlongago.login.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.longlongago.Api;
import com.longlongago.login.model.KeyConstance;
import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.login.model.UserPreference;
import com.longlongago.login.util.NetworkUtils;
import com.longlongago.openvcall.LLAApplication;
import com.longlongago.openvcall.R;
import com.longlongago.resideMenu.MenuActivity;
import com.longlongago.userInterface.RoomActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

//	private static final Logger logger = LoggerFactory.getLogger(LoginActivity.class);

	private static final String TAG = "LoginActivity";
//	@Bind(R.id.login_button) Button loginButton;
//	@Bind(R.id.register_link) TextView registerLink;
//	@Bind(R.id.login_username) EditText usernameText;
//	@Bind(R.id.login_password) EditText passwordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		ClickListener cl = new ClickListener();

		Button loginButton = (Button) this.findViewById(R.id.login_button);
		loginButton.setOnClickListener(cl);

		TextView registerLink = (TextView) this.findViewById(R.id.register_link);
		registerLink.setOnClickListener(cl);

		//add current activity into the container of LLAApplication
		LLAApplication.getInstance().addActivity(this);
	}

	class ClickListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	      switch (v.getId()) {
	      	case R.id.login_button:
	      		processLogin();
	      		break;
	      	case R.id.register_link:
	      		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
	      		startActivity(intent);
	      		break;
	      	default:
	      		break;
	      }
	    }
	  }

	//process activity_login
	public void processLogin() {
		Log.d(TAG, "Login");

//		loginButton.setEnabled(false);
//		final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//				R.style.AppTheme);
//		progressDialog.setIndeterminate(true);
//		progressDialog.setMessage("Authenticating...");
//		progressDialog.show();

		EditText usernameText = (EditText) this.findViewById(R.id.login_username);
		EditText passwordText = (EditText) this.findViewById(R.id.login_password);

		String username = usernameText.getText().toString();
		String password = passwordText.getText().toString();

		HashMap<String,String> params = new HashMap<>();
		params.put("username", username);
		params.put("password", password);

		String url = Api.SERVER_ROOT + "/api/user/signIn";
		this.run(params, url);
	}

	//上传数据到服务器匹配
	public void run(HashMap<String, String> params, String url) {
		final NetworkUtils networkUtils = new NetworkUtils(LLAApplication.getInstance());

		if (!networkUtils.isNetworkConnected()) {
			Log.d("flag", "网络未连接");
			Toast.makeText(LoginActivity.this, R.string.msg_no_network_connection, Toast.LENGTH_LONG ).show();
		}
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		JsonObjectRequest jsonRequest = new JsonObjectRequest(
			    Request.Method.POST, url,
				new JSONObject(params), new Response.Listener<JSONObject>() {
					@RequiresApi(api = Build.VERSION_CODES.N)
					@Override
					public void onResponse(JSONObject response) {
						Log.d("flag", "------->" + response.toString());
						try {
//							Response result = new Gson().fromJson(response.toString(), CustomClass.class);
							String code = response.getString("code");
				        	if (code.equals("200")) {
//				        		Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG ).show();
								Gson gson = new Gson();
								UserBaseInfo userBaseInfo = new UserBaseInfo();
								userBaseInfo = gson.fromJson(response.getString("content"), UserBaseInfo.class);
								//登录成功，保存登录信息
								LLAApplication.getInstance().setBaseUser(userBaseInfo);//保存到Application中
								String isLogin = UserPreference.read(KeyConstance.IS_LOGIN_KEY, null);
								if(TextUtils.isEmpty(isLogin) || "false".equals(isLogin)) {
									UserPreference.save(KeyConstance.IS_LOGIN_KEY, "true");
								}
								//保存到SP中
								UserPreference.save(KeyConstance.IS_USER_ID, userBaseInfo.getUserId());
								UserPreference.save(KeyConstance.IS_USER_ACCOUNT, userBaseInfo.getUsername());
								UserPreference.save(KeyConstance.IS_USER_PASSWORD, userBaseInfo.getPassword());

								Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
//								Intent intent = new Intent(LoginActivity.this, RoomActivity.class);
				        		startActivity(intent);
				        	} else {
				        		Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_LONG ).show();
				        	}
						} catch (JSONException e) {
							e.printStackTrace();
						}
			        }
			    }, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("flag", "请求数据失败");
						Toast.makeText(LoginActivity.this, R.string.no_response_from_server, Toast.LENGTH_LONG ).show();
					}
			    })
		{
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
		requestQueue.add(jsonRequest);
		requestQueue.start();
	}

	@Override
	public void onBackPressed() {
		LLAApplication.getInstance().exit();
	}

//	@Override
//	public void onBackPressed() {
//		// disable going back to the UserInterfaceActivity
//		moveTaskToBack(true);
//	}
}
