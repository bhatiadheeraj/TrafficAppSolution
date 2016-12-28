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
<<<<<<< HEAD

=======
>>>>>>> 1dc0bf4613f445b2cd32cfccf8a7b7748f8a0ad8
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String FireBaseSharedPref = "FireBaseSharedPref";
    public static final String FireBaseShared_KEY = "FireBaseShared_KEY";
<<<<<<< HEAD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

=======
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
>>>>>>> 1dc0bf4613f445b2cd32cfccf8a7b7748f8a0ad8
        firebaseAuth = FirebaseAuth.getInstance();
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
<<<<<<< HEAD
            }

            public void onFinish() {
                /*
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Intent i = new Intent(getApplicationContext(), UserProfile.class);
                            startActivity(i);
                            finish();
                        } else {
                            // User is signed out
                            Intent i = new Intent(Splash.this, LoginRegisterChoose.class);
                            startActivity(i);
                            finish();
                        }
                        // ...
                    }
                };*/
                SharedPreferences sharedpreferences = getSharedPreferences(FireBaseSharedPref, Context.MODE_PRIVATE);
                Boolean ab = sharedpreferences.getBoolean(FireBaseShared_KEY, false);

                //User logged in or not
                if (ab == false) {
                    startActivity(new Intent(SplashActivity.this, LoginRegisterChoose.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    finish();
                }
            }
        }.start();
    }

    void init() {
        setContentView(R.layout.activity_splash);
    }
}
=======

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
>>>>>>> 1dc0bf4613f445b2cd32cfccf8a7b7748f8a0ad8

