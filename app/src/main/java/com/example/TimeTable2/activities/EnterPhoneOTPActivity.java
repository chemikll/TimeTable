package com.example.TimeTable2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TimeTable2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class EnterPhoneOTPActivity extends AppCompatActivity {

    private static final String TAG = EnterPhoneOTPActivity.class.getName();
    private EditText otpcode;
    private Button btnVerify1;
    private TextView tvOTPsend;
    private FirebaseAuth mAuth;

    private String mPhone;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mtoken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_otpactivity);
        otpcode = findViewById(R.id.otpcode);
        btnVerify1 = findViewById(R.id.btnverify1);
        tvOTPsend = findViewById(R.id.tv_send_otp_again);

        mAuth = FirebaseAuth.getInstance();
        getDataIntent();

        btnVerify1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strOTP = otpcode.getText().toString().trim();
                onClickVerify(strOTP);
            }

        });

        tvOTPsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSendOTPAgain();
            }
        });
    }

    private void getDataIntent()
    {
        mPhone = getIntent().getStringExtra("phone_number");
        mVerificationId = getIntent().getStringExtra("verification_id");
    }

    private void onClickVerify(String strOTP)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, strOTP);
        signInWithPhoneAuthCredential(credential);
    }

    private void onClickSendOTPAgain()
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setForceResendingToken(mtoken)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                            {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e)
                            {
                                Toast.makeText(EnterPhoneOTPActivity.this,"Lỗi xác thực",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token)
                            {
                                super.onCodeSent(verificationId,token);
                                mVerificationId = verificationId;
                                mtoken = token;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            gotoMain(user.getPhoneNumber());
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(EnterPhoneOTPActivity.this,"Mã không đúng",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void gotoMain(String phone)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("phone_number", phone);
        startActivity(intent);
    }



}