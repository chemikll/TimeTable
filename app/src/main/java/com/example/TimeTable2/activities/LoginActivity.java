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
                    mUsername.setError("C???n nh???p Email.");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("C???n nh???p M???t kh???u.");
                    return;
                }
                if(password.length()<6)
                {
                    mPassword.setError("M???t kh???u ph???i >= 6 k?? t???");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //Duyet voi CSDL
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"????ng nh???p th??nh c??ng.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else
                        {
                            Toast.makeText(LoginActivity.this,"T??i kho???n ho???c m???t kh???u kh??ng ????ng",Toast.LENGTH_SHORT).show();
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
                passwordResetDialog.setTitle("Qu??n m???t kh???u ?");
                passwordResetDialog.setMessage("Nh???p Email c???a b???n ????? nh???n link t???o m???i");
                passwordResetDialog.setView(resetMail);
                //Nhap mail va gui link
                passwordResetDialog.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            String mail = resetMail.getText().toString();
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(LoginActivity.this,"Link ???? g???i v??o Email c???a b???n.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"L???i ! Link ch??a ???????c g???i"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                    }
                });
                //dong dialog
                passwordResetDialog.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
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
