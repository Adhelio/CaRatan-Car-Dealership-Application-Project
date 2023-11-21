package com.team4.caratan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPw;
    private Button masuk, daftar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        edtEmail = findViewById(R.id.login_edtEmail);
        edtPw = findViewById(R.id.login_edtPw);
        progressBar = findViewById(R.id.login_progressBar);

        masuk = findViewById(R.id.login_btnLogin);
        masuk.setOnClickListener(view -> userLogin());

        daftar = findViewById(R.id.login_btnDaftar);
        daftar.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });
    }

    private void userLogin() {

        final String email = edtEmail.getText().toString().trim();
        final String pw = edtPw.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Username perlu diisi!");
            edtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email tidak sesuai!");
            edtEmail.requestFocus();
            return;
        }

        if (pw.isEmpty()) {
            edtPw.setError("Password perlu diisi!");
            edtPw.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constant.URL_LOGIN,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), "Berhasil masuk!",
                                    Toast.LENGTH_LONG).show();

                            final mDBhandler dbHandler = new mDBhandler(this);

                            try {
                                dbHandler.open();
                            } catch (SQLException e) {
                                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                            }

                            dbHandler.createUsers(obj.getString("user_id"), obj.getString("fullname"),
                                    obj.getString("email"), obj.getString("phone"), obj.getString("profile_pic"), obj.getString("admin"));

                            SharedPrefManager.getInstance(getApplicationContext())
                                    .loginUser(obj.getString("email"));

                            dbHandler.close();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Silahkan cek kembali internet anda!",
                            Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", pw);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}