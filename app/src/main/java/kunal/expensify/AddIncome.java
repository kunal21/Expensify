package kunal.expensify;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddIncome extends AppCompatActivity {

    private Button save,cancel;
    private DatabaseReference dref;
    private FirebaseAuth auth;
    private DatePickerDialog date;
    private Calendar cal;
    private TextView date_view,tv;
    int year_x , month_x , day_x;
    static final int DIALOG_ID = 1;
    private EditText add_amount,desc;
    private String date_display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        getSupportActionBar().setTitle("Add Income");

        date_view = (TextView) findViewById(R.id.date_view);
        desc = (EditText) findViewById(R.id.desc);
        dref = FirebaseDatabase.getInstance().getReference().child("Income");
        auth = FirebaseAuth.getInstance();
        add_amount = (EditText) findViewById(R.id.add_amount);

        cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        date_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = add_amount.getText().toString();
                String description = desc.getText().toString();

                if(TextUtils.isEmpty(amount))
                {
                    Toast.makeText(AddIncome.this,"Enter Value",Toast.LENGTH_SHORT).show();
                }

                if(description.length() > 30)
                {
                    Toast.makeText(AddIncome.this,"Not More the 30 Words",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    String user_id = auth.getCurrentUser().getUid();
                    DatabaseReference random_id = dref.child(user_id).push();
                    random_id.child("amount").setValue(amount);
                    random_id.child("date").setValue(date_display);
                    random_id.child("description").setValue(description);

                    Intent back_to_main_expense = new Intent(AddIncome.this,MainExpense.class);
                    startActivity(back_to_main_expense);
                    Toast.makeText(AddIncome.this,"Data Saved",Toast.LENGTH_SHORT).show();


                }


            }
        });


    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if(id == DIALOG_ID)
        {
            return new DatePickerDialog(AddIncome.this,dpickerListener,year_x,month_x,day_x);
        }
        else {
            return null;
        }
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            date_view.setText(day_x+"/"+month_x+"/"+year_x);
            date_display = date_view.getText().toString();
        }
    };
}
