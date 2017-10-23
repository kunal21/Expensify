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
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EditExpense extends AppCompatActivity {

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
    private String key,value_from_mainexpense,date_from_mainexpense,description_from_mainexpense,category_from_mainexpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);
        getSupportActionBar().setTitle("Edit Expense");

        Intent i = getIntent();
        key = i.getStringExtra("key");
        value_from_mainexpense = i.getStringExtra("value");
        System.out.println("hhgh "+value_from_mainexpense);
        date_from_mainexpense = i.getStringExtra("date");
        description_from_mainexpense = i.getStringExtra("desc");
        category_from_mainexpense = i.getStringExtra("category");



        value = (EditText) findViewById(R.id.value);
        date_view = (TextView) findViewById(R.id.date_view);
        description = (EditText) findViewById(R.id.description);

           value.setText(String.valueOf(value_from_mainexpense), TextView.BufferType.EDITABLE);
           date_view.setText(date_from_mainexpense);
           description.setText(description_from_mainexpense);

        auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();
        dref = FirebaseDatabase.getInstance().getReference().child("Expenses").child(user_id).child(key);


        spinner = (Spinner) findViewById(R.id.category2);
        save = (Button) findViewById(R.id.save);


        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category_text = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditExpense.this,R.array.categories2_array,android.R.layout.simple_spinner_dropdown_item);
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


                if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(EditExpense.this, "Enter Value", Toast.LENGTH_SHORT).show();
                    value.setError("Enter Expense");
                } else if (amount.length() > 8) {
                    Toast.makeText(EditExpense.this, "Amount too Large", Toast.LENGTH_SHORT).show();
                } else if (description_text.length() > 30) {
                    Toast.makeText(EditExpense.this, "Not More the 30 Words", Toast.LENGTH_SHORT).show();
                } else {
                    dref.child("value").setValue(amount);
                    dref.child("category").setValue(category_text);
                    dref.child("date").setValue(date_display);
                    dref.child("description").setValue(description_text);

                    Toast.makeText(EditExpense.this, "Data Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if(id == DIALOG_ID)
        {
            return new DatePickerDialog(EditExpense.this,dpickerListener,year_x,month_x,day_x);
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
