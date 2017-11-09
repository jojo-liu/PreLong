package com.longlongago.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

	private static final String TAG = "RegisterActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Button registerButton = (Button) this.findViewById(R.id.register_button);
		TextView loginLink = (TextView) this.findViewById(R.id.login_link);
		ClickListener cl = new ClickListener();
		registerButton.setOnClickListener(cl);
		loginLink.setOnClickListener(cl);

		//add current activity into the container of LLAApplication
		LLAApplication.getInstance().addActivity(this);
	}
	
	class ClickListener implements OnClickListener {
	    @Override
	    public void onClick(View v) {
	      switch (v.getId()) {
	      	case R.id.register_button:
	      		processRegister();
	      		break;
	      	case R.id.login_link:
	      		Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
	      		startActivity(intent);
	      		break;
	      	default:
	      		break;
	      }
	    }
	 }

	public void processRegister() {
		EditText usernameText = (EditText) this.findViewById(R.id.input_name);
		EditText passwordText = (EditText) this.findViewById(R.id.input_password);
		RadioGroup sexGroup = (RadioGroup) this.findViewById(R.id.radiogroup_sex);
//		EditText repasswordText = (EditText) this.findViewById(R.id.password_reinput_edit);
//		EditText mailText = (EditText) this.findViewById(R.id.mail_edit);
//		EditText phoneNumberText = (EditText) this.findViewById(R.id.phone_edit);
		boolean flag = true;
		
		String username = usernameText.getText().toString();
		flag = checkIsInput(username, String.valueOf(R.string.no_username));
		if (!flag) return;
		
		String password = passwordText.getText().toString();
		flag = checkIsInput(password,String.valueOf(R.string.no_password));
		if (!flag) return;
		
//		String repassword = repasswordText.getText().toString();
//		flag = checkIsInput(repassword,"请确认您的密码");
//		if (!flag) return;
//		if (!password.equals(repassword)) {
//			Toast.makeText(RegisterActivity.this, "两次输入密码不一样!", Toast.LENGTH_LONG ).show();
//			return;
//		}
		
//		String mail = mailText.getText().toString();
//		flag = checkIsInput(mail,"��������������");
//		if (!flag) return;
//
//		String firstName = firstNameText.getText().toString();
//		flag = checkIsInput(firstName,"������������");
//		if (!flag) return;
//
//		String lastName = lastNameText.getText().toString();
//		flag = checkIsInput(lastName,"������������");
//		if (!flag) return;
		
		String sex = "";
		RadioButton checkedRadio = (RadioButton) this.findViewById(sexGroup.getCheckedRadioButtonId());
        if (checkedRadio==null) {
        	Toast.makeText(RegisterActivity.this, "请选择性别", Toast.LENGTH_LONG ).show();
        	return;
        }
		sex = checkedRadio.getText().toString();

		flag = checkIsInput(sex,"请输入您的性别");
		if (!flag) return;

//		final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
//				R.style.AppTheme);
//		progressDialog.setIndeterminate(true);
//		progressDialog.setMessage("Creating Account...");
//		progressDialog.show();
//		String phoneNumber = phoneNumberText.getText().toString();
//		flag = checkIsInput(phoneNumber,"请输入手机号码");
//		if (!flag) return;
		
		HashMap<String,String> params = new HashMap<String,String>();
//		params.put("userid", "1");
		params.put("username", username);
		params.put("password", password);
//		params.put("mail", mail);
//		params.put("firstName", firstName);
//		params.put("lastName", lastName);
		if(sex.equals("男") || sex.equals("male")) {
			sex = "male";
		} else if(sex.equals("女") || sex.equals("female")) {
			sex = "female";
		}
		params.put("sex", sex);
//		params.put("phoneNumber", phoneNumber);
		
		String url = Api.SERVER_ROOT + "/api/user/register";
		run(params,url);
		
	}
	//检查输入是否有效
	private boolean checkIsInput(String value, String warning) {
		if (value==null||value.equals("")) {
			Toast.makeText(RegisterActivity.this, warning, Toast.LENGTH_SHORT ).show();
			return false;
		}
		return true;
	}

	//上传数据到服务器匹配
	public void run(HashMap<String, String> params, String url) {
		final NetworkUtils networkUtils = new NetworkUtils(LLAApplication.getInstance());
		if (!networkUtils.isNetworkConnected()) {
			Log.d("flag", "网络未连接");
			Toast.makeText(RegisterActivity.this, "网络未连接!", Toast.LENGTH_LONG ).show();
		}

		RequestQueue requestQueue = Volley.newRequestQueue(this);
		
		JsonObjectRequest jsonRequest = new JsonObjectRequest(
			    Request.Method.POST, url,
			    new JSONObject(params),
				new Response.Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						Log.d("flag", "------->" + response.toString());
						try {
//							Response result = new Gson().fromJson(response.toString(), CustomClass.class);
							String code = response.getString("code");
							if (code.equals("200")) {
//				        		Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG ).show();
								Gson gson = new Gson();
								UserBaseInfo userBaseInfo = new UserBaseInfo();
								userBaseInfo = gson.fromJson(response.getString("content").toString(), UserBaseInfo.class);
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

								Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
								startActivity(intent);
							} else if(code.equals("10011")) {
								Toast.makeText(RegisterActivity.this, "用户名已存在，请重新选择", Toast.LENGTH_LONG ).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
			    },
				new Response.ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						Log.d("flag", "请求数据失败");
						Toast.makeText(RegisterActivity.this, "服务器无响应!", Toast.LENGTH_LONG ).show();
					}
			    }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
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
		
}
