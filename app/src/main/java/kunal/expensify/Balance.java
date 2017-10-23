package kunal.expensify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Balance extends AppCompatActivity {

    TextView balance,balance_text,income_text,expense_text;
    private String expense,income;
    private Integer income_amount;
    private FirebaseAuth auth;
    private DatabaseReference get_income,get_expense;
    private int income_count = 0,expense_count=0,Balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        getSupportActionBar().setTitle("Balance");


        Intent intent = getIntent();
        final List<String> income_from_database = new ArrayList<>();
        final List<String> expense_from_database = new ArrayList<>();
        final List<String> food = new ArrayList<>();

        expense_text = (TextView) findViewById(R.id.expense_text);
        income_text = (TextView) findViewById(R.id.income_text);
        balance = (TextView) findViewById(R.id.balance);

        auth = FirebaseAuth.getInstance();

        income_text = (TextView) findViewById(R.id.income_text);
        expense_text = (TextView) findViewById(R.id.expense_text);
        balance_text = (TextView) findViewById(R.id.balance_text);

        String user_id = auth.getCurrentUser().getUid();
        get_expense = FirebaseDatabase.getInstance().getReference().child("Expenses").child(user_id);
        get_income = FirebaseDatabase.getInstance().getReference().child("Income").child(user_id);


        get_income.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    GetIncomeFromDatabase getIncomeFromDatabase = i.getValue(GetIncomeFromDatabase.class);
                    income_from_database.add(String.valueOf(getIncomeFromDatabase.getAmount()));
                }
                System.out.println(income_from_database);
                for(int i = 0 ; i < income_from_database.size();i++)
                {
                    income_count = income_count + Integer.parseInt(income_from_database.get(i));
                }
                income_text.setText(String.valueOf(income_count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        get_expense.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i : dataSnapshot.getChildren()) {
                    GetExpenseFromDatabase getExpenseFromDatabase = i.getValue(GetExpenseFromDatabase.class);
                    expense_from_database.add(String.valueOf(getExpenseFromDatabase.getValue()));
                }
                for(int i = 0 ; i < expense_from_database.size();i++)
                {
                    expense_count = expense_count + Integer.parseInt(expense_from_database.get(i));
                }
                System.out.println(expense_from_database);
                expense_text.setText(String.valueOf(expense_count));
              /*  String value_from_income_text = income_text.getText().toString();
                String value_from_expense_text = expense_text.getText().toString();
                Integer value_from_income_text_int = Integer.parseInt(value_from_income_text);
                Integer value_from_expense_text_int = Integer.parseInt(value_from_expense_text);
                Balance = value_from_income_text_int - value_from_expense_text_int;

                balance.setText(String.valueOf(Balance)+" Rs.");*/
                calculateBalance(String.valueOf(expense_count),String.valueOf(income_count));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

        public void calculateBalance(String ex , String in)
        {
            Integer value_from_income_text_int = Integer.parseInt(in);
            Integer value_from_expense_text_int = Integer.parseInt(ex);
            Balance = value_from_income_text_int - value_from_expense_text_int;
            balance.setText(String.valueOf(Balance)+" Rs.");
        }

/*
System.out.println("In Get Income");
               GetIncomeFromDatabase getIncomeFromDatabase =  dataSnapshot.getValue(GetIncomeFromDatabase.class);
               income_from_database.add(String.valueOf(getIncomeFromDatabase.getAmount()));
               income_count = income_count + Integer.parseInt(income_from_database.get(count_from_the_array_list_income));
               count_from_the_array_list_income++;
               income_text.setText(String.valueOf(income_count));
 */

}
