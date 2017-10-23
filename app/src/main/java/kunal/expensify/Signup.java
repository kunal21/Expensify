package kunal.expensify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {


    private EditText email , username , password , cpassword;
    private Button register;
    private FirebaseAuth auth;
    private DatabaseReference df;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Register");
        pd = new ProgressDialog(this);

        email = (EditText)findViewById(R.id.email);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        cpassword = (EditText)findViewById(R.id.cpassword);
        register = (Button) findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();
        df = FirebaseDatabase.getInstance().getReference().child("Users");

        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user,0,0,0);
                email.setCompoundDrawables(null,null,null,null);
                return false;
            }
        });
        username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_outline_black_24dp,0,0,0);
                username.setCompoundDrawables(null,null,null,null);
                return false;
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user,0,0,0);
                cpassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_outline_black_24dp,0,0,0);
                password.setCompoundDrawables(null,null,null,null);
                return false;
            }
        });
        cpassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pswd,0,0,0);
                username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user,0,0,0);
                email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_outline_black_24dp,0,0,0);
                cpassword.setCompoundDrawables(null,null,null,null);
                return false;
            }
        });




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

               // boolean isvalid = true;
               // String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String email_text = email.getText().toString().trim();
                final String username_text = email.getText().toString().trim();
                String password_text = password.getText().toString().trim();
                String cpassword_text = cpassword.getText().toString().trim();

                if(TextUtils.isEmpty(email_text) || TextUtils.isEmpty(username_text) || TextUtils.isEmpty(password_text))
                {
                    Toast.makeText(Signup.this,"Please Input All The Fields",Toast.LENGTH_SHORT).show();
                    email.setError("Input Email");
                    username.setError("Input Username");
                    password.setError("Input Password");
                    cpassword.setError("Input Password");

                }
                else if(password_text.length() < 6)
                {
                    Toast.makeText(Signup.this,"Password Should Be Atleast 6 Characters Long",Toast.LENGTH_SHORT).show();
                    password.setError("Password length minimum 6");
                }

                else if(!password_text.equals(cpassword_text))
                {
                    Toast.makeText(Signup.this,"Passwords Dont Match",Toast.LENGTH_SHORT).show();
                }

                else{
                    pd.setMessage("Signing Up ...");
                    pd.show();
                    auth.createUserWithEmailAndPassword(email_text,password_text).addOnCompleteListener(Signup.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

                                String user_id = auth.getCurrentUser().getUid();
                                df.child(user_id).child("username").setValue(username_text);
                                //current_user_db.child("username").setValue(username_text);

                                Toast.makeText(Signup.this,"SuccessfullY Registered",Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                                Intent i = new Intent(Signup.this,Login.class);
                                startActivity(i);
                            }
                            else
                            {
                                Toast.makeText(Signup.this,"Not Successfull",Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                        }
                    });
                }



            }
        });





    }
}
