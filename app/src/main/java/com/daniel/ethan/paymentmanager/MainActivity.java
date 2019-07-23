package com.daniel.ethan.paymentmanager;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button btnMoneyInBank;
    private Button btnMoneyChecksNotCashed;

    private TextView textMoneyInEnvelopes;
    private TextView textMoneyRemaining;
    private RecyclerView envelopesRecycler;
    private double totalEnvelopesAmount;

    //test data
    private ArrayList<String> envelopeNames = new ArrayList<>(Arrays.asList("Electric Bill", "Phone", "Savings for a Car", "Cable Bill"));
    private ArrayList<Integer> currentAmounts = new ArrayList<>(Arrays.asList(100, 200, 25, 100));
    private ArrayList<Integer> autoUpdateAmounts = new ArrayList<>(Arrays.asList(25, 10, 5, 25));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.dialog_add_envelope, null);
                final EditText name = view.findViewById(R.id.dialog_add_envelope_name);
                final EditText totalAmount = view.findViewById(R.id.dialog_add_total_amount);
                final EditText autoUpdate = view.findViewById(R.id.dialog_add_auto_update_amount);
                Button createButton = view.findViewById(R.id.dialog_button_create);

                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFilled(name) && isFilled(totalAmount) && isFilled(autoUpdate)) {
                            Toast.makeText(MainActivity.this, "envelope created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isFilled(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    private void initComponents() {
        btnMoneyInBank = findViewById(R.id.btn_money_in_bank);
        btnMoneyChecksNotCashed = findViewById(R.id.btn_checks_not_cashed);
        textMoneyInEnvelopes = findViewById(R.id.text_money_in_envelopes);
        textMoneyRemaining = findViewById(R.id.text_money_remaining);
        envelopesRecycler = findViewById(R.id.envelopes_recycler);
        envelopesRecycler.setHasFixedSize(true);
        textMoneyInEnvelopes.setText("$" + totalEnvelopesAmount);

        EnvelopesAdapter adapter = new EnvelopesAdapter(envelopeNames, currentAmounts, autoUpdateAmounts, this);
        envelopesRecycler.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(envelopesRecycler.getContext(), llm.getOrientation());
        envelopesRecycler.addItemDecoration(dividerItemDecoration);
        envelopesRecycler.setLayoutManager(llm);
        updateMoneyOwed();
        initializeOnClicks();
    }

    private void initializeOnClicks() {
        btnMoneyInBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setTitle(R.string.bank_acc_balance);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnMoneyInBank.setText("$" + input.getText().toString());
                        updateMoneyOwed();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        btnMoneyChecksNotCashed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setTitle(R.string.checks_not_cashed);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnMoneyChecksNotCashed.setText("$" + input.getText().toString());
                        updateMoneyOwed();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void updateMoneyOwed() {
        totalEnvelopesAmount = 0;
        double moneyOwed = Double.parseDouble(btnMoneyInBank.getText().toString().substring(1));
        moneyOwed -= Double.parseDouble(btnMoneyChecksNotCashed.getText().toString().substring(1));

        for (int i = 0; i < currentAmounts.size(); i++) {
            totalEnvelopesAmount += currentAmounts.get(i);
        }

        textMoneyInEnvelopes.setText("$" + totalEnvelopesAmount);
        moneyOwed -= totalEnvelopesAmount;
        if (moneyOwed < 0) {
            moneyOwed = -moneyOwed;
            textMoneyRemaining.setTextColor(getResources().getColor(R.color.red));
        }
        textMoneyRemaining.setText("" + moneyOwed);
    }
}
