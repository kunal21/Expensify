package kunal.expensify;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddExpense extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText value,description;
    private Button save,cancel;
    private DatabaseReference dref;
    private FirebaseAuth auth;
    private DatePickerDialog date;
    private Calendar cal;
    private TextView date_view,tv;
    int year_x , month_x , day_x;
    static final int DIALOG_ID = 1;
    private Spinner spinner;
    private String category_text;
    private String date_display;
    Balance b = new Balance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        getSupportActionBar().setTitle("Add Expense");


        date_view = (TextView) findViewById(R.id.date_view);
        description = (EditText) findViewById(R.id.description);
        dref = FirebaseDatabase.getInstance().getReference().child("Expenses");
        auth = FirebaseAuth.getInstance();
        value = (EditText) findViewById(R.id.value);
        spinner = (Spinner) findViewById(R.id.category);
        save = (Button) findViewById(R.id.save);


        spinner.setOnItemSelectedListener(AddExpense.this);
        cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddExpense.this,R.array.categories_array,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        date_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showDialog(DIALOG_ID);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = value.getText().toString();
                String description_text = description.getText().toString();


                if(TextUtils.isEmpty(amount))
                {
                    Toast.makeText(AddExpense.this,"Enter Value",Toast.LENGTH_SHORT).show();
                    value.setError("Enter Expense");
                }
                else if (amount.length() > 8)
                {
                    Toast.makeText(AddExpense.this,"Amount too Large",Toast.LENGTH_SHORT).show();
                }

                else if(description_text.length() > 30)
                {
                    Toast.makeText(AddExpense.this,"Not More the 30 Words",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String user_id = auth.getCurrentUser().getUid();
                    DatabaseReference random_id = dref.child(user_id).push();
                    random_id.child("value").setValue(amount);
                    random_id.child("category").setValue(category_text);
                    random_id.child("date").setValue(date_display);
                    random_id.child("description").setValue(description_text);

                    Intent back_to_main_expense = new Intent(AddExpense.this,MainExpense.class);
                    startActivity(back_to_main_expense);
                    Toast.makeText(AddExpense.this,"Data Saved",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel = (Button) findViewById(R.id.cancel);
    cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            value.setText("");
            description.setText("");
        }
    });
}

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if(id == DIALOG_ID)
        {
            return new DatePickerDialog(AddExpense.this,dpickerListener,year_x,month_x,day_x);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category_text = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}
