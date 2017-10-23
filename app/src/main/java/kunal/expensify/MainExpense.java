package kunal.expensify;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainExpense extends AppCompatActivity {

    private DatabaseReference fdb,retrieve_data;
    private FirebaseAuth auth;
    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private TextView value;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_expense);
        getSupportActionBar().setTitle("Expenses");

        pd = new ProgressDialog(MainExpense.this);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainExpense.this));
        value = (TextView) findViewById(R.id.value);

        auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();
        add = (FloatingActionButton) findViewById(R.id.add);
        retrieve_data = FirebaseDatabase.getInstance().getReference().child("Expenses").child(user_id);
        fdb = FirebaseDatabase.getInstance().getReference().child("Expenses");

            add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainExpense.this);
                builder.setMessage("Select Any One").setPositiveButton("Add Expense", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent add_expense = new Intent(MainExpense.this,AddExpense.class);
                        add_expense.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(add_expense);
                    }
                }).setNegativeButton("Add Income", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent add_income = new Intent(MainExpense.this,AddIncome.class);
                        add_income.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(add_income);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.balance)
        {
            Intent to_balance = new Intent(MainExpense.this,Balance.class);
            startActivity(to_balance);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Expense,ExpenseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Expense, ExpenseViewHolder>(
                Expense.class,R.layout.expense_row,ExpenseViewHolder.class,retrieve_data

        ) {
            @Override
            protected void populateViewHolder(final ExpenseViewHolder viewHolder, final Expense model, int position) {

                final String expense_key = getRef(position).getKey();
                viewHolder.setValue(model.getValue());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());

                viewHolder.mview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainExpense.this);
                        builder.setMessage("Select Any One").setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pd.setMessage("Deleting Data...");
                                pd.show();
                                retrieve_data.child(expense_key).removeValue();
                                pd.dismiss();
                                Toast.makeText(MainExpense.this,"Data Deleted",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent to_edit_expense = new Intent(MainExpense.this,EditExpense.class);
                                to_edit_expense.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                to_edit_expense.putExtra("key",expense_key);
                                to_edit_expense.putExtra("value",model.getValue());
                                to_edit_expense.putExtra("category",model.getCategory());
                                to_edit_expense.putExtra("date",model.getDate());
                                to_edit_expense.putExtra("desc",model.getDescription());
                                startActivity(to_edit_expense);
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        return false;
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }


        public void setValue(String value)
        {
            TextView expense_value = (TextView) mview.findViewById(R.id.value);
            expense_value.setText("Rs. "+value);
        }

        public void setCategory(String category)
        {
            TextView expense_category = (TextView) mview.findViewById(R.id.category);
            expense_category.setText("Category : "+category);
        }
        public void setDate(String date)
        {
            TextView expense_date = (TextView) mview.findViewById(R.id.date);
            expense_date.setText("Date : "+date);

        }
        public void setDescription(String description)
        {
            TextView expense_description = (TextView) mview.findViewById(R.id.description);
            expense_description.setText("Description : "+description);
        }

    }



}
//    String value_amount = value.getText().toString();
//    String user_id = auth.getCurrentUser().getUid();
//    DatabaseReference random_id = fdb.child(user_id).push();
//random_id.child("value").setValue(value_amount);
//        random_id.child("Category").setValue("Food");
//        random_id.child("Location").setValue("Pizza Hut");