package dheeraj.com.trafficsolution;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    TextView create;

    EditText email;
    EditText password;
    FirebaseAuth firebaseAuth;

    String displayname;
    String photourl;

    public static final String FireBaseSharedPref = "FireBaseSharedPref";
    public static final String FireBaseShared_KEY = "FireBaseShared_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        Button singin = (Button) findViewById(R.id.signin1);

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailid = email.getText().toString();
                String passwordtext = password.getText().toString();


                if (emailid == null) {
                    email.setError("You can not leave it blank.");

                }
                if (passwordtext == null) {
                    password.setError("You can not leave it blank . ");
                }


                // Force user to fill up the form
                if (emailid.equals("") && passwordtext.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_LONG).show();
                    email.setError("You can not leave it blank.");
                    password.setError("You can not leave it blank.");
                }
                else {
                    final ProgressDialog rd = new ProgressDialog(SignIn.this);
                    rd.setTitle("Please Wait !");
                    rd.setMessage("We are setting everything .");
                    rd.setCancelable(false);
                    rd.show();

                    firebaseAuth.signInWithEmailAndPassword(emailid, passwordtext).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getApplicationContext(), "Incorrect passoword or email !", Toast.LENGTH_LONG).show();
                            firebaseAuth.signOut();
                            finish();
                        }
                    });
                    firebaseAuth.signInWithEmailAndPassword(emailid, passwordtext).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(final AuthResult authResult) {
                            SharedPreferences sharedpreferences = getSharedPreferences(FireBaseSharedPref, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(FireBaseShared_KEY,true);
                            editor.commit();

                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("people").child(authResult.getUser().getUid());
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    displayname = String.valueOf(dataSnapshot.child("displayNames").getValue());
                                    photourl = String.valueOf(dataSnapshot.child("photoUrls").getValue());
                                    Log.e("displayNames", "" + displayname);
                                    Log.e("photoUrls", "" + photourl);

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(displayname)
                                            .setPhotoUri(Uri.parse(photourl))
                                            .build();

                                    authResult.getUser().updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                               public void onComplete(Task<Void> task) {
                                     if (task.isSuccessful()) {
                                         Log.d("profileupdated", "User profile updated.");
                                     }
                                 }
                                            });

                                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);

                                    startActivity(i);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                        }
                    });
                }
                ;
            }
        });
    }
}