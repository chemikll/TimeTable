package com.example.TimeTable2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mUsername, mPassword;
    Button signup,signin;
    TextView forgotpass,tvSDT;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkUserState();

        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }


        mUsername = findViewById(R.id.username1);
        mPassword = findViewById(R.id.password1);

        signup = findViewById(R.id.btnsignup1);
        signin = findViewById(R.id.btnsignin1);
        forgotpass = findViewById(R.id.forgotpass);
        tvSDT = findViewById(R.id.btnsdt);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        //Click dang nhap
        signin.setOnClickListener(new View.OnClickListener() {
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
                //Duyet voi CSDL
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Đăng nhập thành công.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else
                        {
                            Toast.makeText(LoginActivity.this,"Tài khoản hoặc mật khẩu không đúng",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
        //Click Dang ky
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        //Click quen mat khau
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Quên mật khẩu ?");
                passwordResetDialog.setMessage("Nhập Email của bạn để nhận link tạo mới");
                passwordResetDialog.setView(resetMail);
                //Nhap mail va gui link
                passwordResetDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            String mail = resetMail.getText().toString();
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(LoginActivity.this,"Link đã gửi vào Email của bạn.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"Lỗi ! Link chưa được gửi"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                    }
                });
                //dong dialog
                passwordResetDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                //hien thi hop thoai
                passwordResetDialog.create().show();
            }
        });

        tvSDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),VerifyPhoneActivity.class));
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
