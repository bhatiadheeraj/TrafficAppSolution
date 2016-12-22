package dheeraj.com.trafficsolution;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String FireBaseSharedPref = "FireBaseSharedPref";
    public static final String FireBaseShared_KEY = "FireBaseShared_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {




//                mAuthListener = new FirebaseAuth.AuthStateListener() {
//                    @Override
//                    public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                        if (user != null) {
//                            // User is signed in
//                            Intent i = new Intent(getApplicationContext(), UserProfile.class);
//                            startActivity(i);
//                            finish();
//                        } else {
//                            // User is signed out
//                            Intent i = new Intent(Splash.this, LoginRegisterChoose.class);
//                            startActivity(i);
//                            finish();
//                        }
//                        // ...
//                    }
//                };
                SharedPreferences sharedpreferences = getSharedPreferences(FireBaseSharedPref, Context.MODE_PRIVATE);
                Boolean ab = sharedpreferences.getBoolean(FireBaseShared_KEY, false);

                if (ab == false) {

                    Intent i = new Intent(SplashActivity.this, LoginRegisterChoose.class);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }.start();
    }
    }

