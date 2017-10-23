package kunal.expensify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.data;

public class Login extends AppCompatActivity {


    private Button login;
    private TextView signup_hyperlink;
    private EditText email, password;
    private FirebaseAuth auth;
    private DatabaseReference checkuser_indb;
    private SignInButton google_signin;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener authListener;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";
    private ProgressDialog pd;

    // private FirebaseAuth.AuthStateListener authListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(Login.this);
        google_signin = (SignInButton) findViewById(R.id.googleLogin);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                email.setCompoundDrawables(null,null,null,null);
                return false;
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user,0,0,0);
                password.setCompoundDrawables(null,null,null,null);
                return false;
            }
        });


        // inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayoutUsername);
        //inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);

        signup_hyperlink = (TextView) findViewById(R.id.signup);
        auth = FirebaseAuth.getInstance();
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//            }
//        };
        checkuser_indb = FirebaseDatabase.getInstance().getReference().child("Users");
        login = (Button) findViewById(R.id.login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(Login.this)
                .enableAutoManage(Login.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(Login.this,"Error Signing In",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = email.getText().toString().trim();
                String password_text = password.getText().toString().trim();
                if(TextUtils.isEmpty(email_text))
                {
                    Toast.makeText(Login.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                    email.setError("Enter Email");
                }
                else if(TextUtils.isEmpty(password_text))
                {
                    Toast.makeText(Login.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                    password.setError("Enter Password");
                }
                else
                {
                    pd.setMessage("Logging In...");
                    pd.show();
                    auth.signInWithEmailAndPassword(email_text, password_text).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkUserExists();
                            } else {
                                pd.dismiss();
                                Toast.makeText(Login.this, "Sorry! Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
                });


        signup_hyperlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Signup.class);
                startActivity(i);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            pd.setMessage("Signin in...");
            pd.show();
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                pd.dismiss();
            } else {
                pd.dismiss();
                Toast.makeText(Login.this,"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            pd.dismiss();
                            Intent i = new Intent(Login.this,MainExpense.class);
                            startActivity(i);
                        }
                        else
                        {
                            pd.dismiss();
                        }

                    }
                });
    }

    private void checkUserExists() {
        if (auth.getCurrentUser() != null) {
            final String user_id = auth.getCurrentUser().getUid();
            checkuser_indb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        pd.dismiss();
                        Intent to_main_expense = new Intent(Login.this, MainExpense.class);
                        //Intent to_balance = new Intent(Login.this,Balance.class);
                        to_main_expense.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //to_balance.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(to_main_expense);
                        //to_balance.putExtra("current_user_id",user_id);

                       // startActivity(to_balance);
                        Toast.makeText(Login.this,"Succesfully Logged In",Toast.LENGTH_SHORT).show();
                    } else {
                        pd.dismiss();
                        Toast.makeText(Login.this, "Error Logging In", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                        pd.dismiss();
                }
            });
        }
    }
}



//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (firebaseAuth.getCurrentUser() == null)
//                {
//                    Intent i = new Intent(Login.this,Signup.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                }
//            }
//        };