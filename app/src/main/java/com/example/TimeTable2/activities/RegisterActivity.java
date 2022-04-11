package com.example.TimeTable2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TimeTable2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText mUsername, mPassword, mPhone;
    Button signup,signin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView tvSDT;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkUserState();

        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }

        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        signup = findViewById(R.id.btnsignup);
        signin = findViewById(R.id.btnsignin);
        tvSDT = findViewById(R.id.btnsdt);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);

        //Mo trang Home neu dang nhap thanh cong
        if(mAuth.getCurrentUser()!= null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        //Click dang ky
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //Bay loi
                if(TextUtils.isEmpty(email))
                {
                    mUsername.setError("Cần nhập Email.");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Cần nhập Mật khẩu.");
                    return;
                }
                if(password.length()<6)
                {
                    mPassword.setError("Mật khẩu phải >= 6 kí tự");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //dang ky user voi CSDL
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Tạo tài khoản thành công.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else
                        {
                            Toast.makeText(RegisterActivity.this,"Error"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                });
            }
        });

    //Click "Da co tai khoan, Dang nhap ngay"
    signin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    });



    }
    private void checkUserState(){
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

}