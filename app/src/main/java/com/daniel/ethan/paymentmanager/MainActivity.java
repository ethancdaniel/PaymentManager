package com.daniel.ethan.paymentmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static com.daniel.ethan.paymentmanager.Utils.formatMoney;

public class MainActivity extends AppCompatActivity {

    private Button btnMoneyInBank;
    private Button btnMoneyChecksNotCashed;

    private TextView textMoneyInEnvelopes;
    private TextView textMoneyRemaining;
    private RecyclerView envelopesRecycler;
    private double totalEnvelopesAmount;
    private EnvelopesAdapter envelopesAdapter;

    //test data
    private ArrayList<String> envelopeNames = new ArrayList<>(Arrays.asList("Electric Bill", "Phone", "Savings for a Car", "Cable Bill"));
    private ArrayList<Double> currentAmounts = new ArrayList<>(Arrays.asList(100.00, 200.00, 25.00, 100.00));
    private ArrayList<Double> autoUpdateAmounts = new ArrayList<>(Arrays.asList(25.00, 10.00, 5.00, 25.00));
    private Double moneyInbank = 1000.00;
    private Double moneyChecksNotCashed = 1000.00;

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
                NewEnvelopeDialog dialog = new NewEnvelopeDialog(new NewEnvelopeDialog.NewEnvelopeDialogListener() {
                    @Override
                    public void onCreateEnvelope(String name, Double amount, Double autoUpdate) {
                        envelopeNames.add(name);
                        currentAmounts.add(amount);
                        autoUpdateAmounts.add(autoUpdate);
                        envelopesAdapter.notifyDataSetChanged();
                    }
                });
                dialog.setRetainInstance(true);
                dialog.show(getSupportFragmentManager(), "new envelope dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void initComponents() {
        btnMoneyInBank = findViewById(R.id.btn_money_in_bank);
        btnMoneyChecksNotCashed = findViewById(R.id.btn_checks_not_cashed);
        textMoneyInEnvelopes = findViewById(R.id.text_money_in_envelopes);
        textMoneyRemaining = findViewById(R.id.text_money_remaining);
        envelopesRecycler = findViewById(R.id.envelopes_recycler);
        envelopesRecycler.setHasFixedSize(true);
        textMoneyInEnvelopes.setText(formatMoney(totalEnvelopesAmount));

        btnMoneyInBank.setText(formatMoney(moneyInbank));
        btnMoneyChecksNotCashed.setText(formatMoney(moneyChecksNotCashed));

        envelopesAdapter = new EnvelopesAdapter(envelopeNames, currentAmounts, autoUpdateAmounts, this);
        envelopesRecycler.setAdapter(envelopesAdapter);
        envelopesRecycler.setNestedScrollingEnabled(false);
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

                BankAmountDialog dialog = new BankAmountDialog(new BankAmountDialog.BankAmountListener() {
                    @Override
                    public void applyBankAmount(Double amount) {
                        moneyInbank = amount;
                        btnMoneyInBank.setText(formatMoney(amount));
                        updateMoneyOwed();
                    }
                });
                dialog.setRetainInstance(true);
                dialog.show(getSupportFragmentManager(), "bank amount dialog");
            }
        });

        btnMoneyChecksNotCashed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChecksNotCashedDialog dialog = new ChecksNotCashedDialog(new ChecksNotCashedDialog.ChecksNotCashedListener() {
                    @Override
                    public void applyChecks(Double amount) {
                        moneyChecksNotCashed = amount;
                        btnMoneyChecksNotCashed.setText(formatMoney(amount));
                        updateMoneyOwed();
                    }
                });
                dialog.setRetainInstance(true);
                dialog.show(getSupportFragmentManager(), "checks not cashed dialog");
            }
        });
    }

    private void updateMoneyOwed() {
        totalEnvelopesAmount = 0;
        double moneyOwed = moneyInbank;
        moneyOwed -= moneyChecksNotCashed;

        for (int i = 0; i < currentAmounts.size(); i++) {
            totalEnvelopesAmount += currentAmounts.get(i);
        }

        textMoneyInEnvelopes.setText(formatMoney(totalEnvelopesAmount));
        moneyOwed -= totalEnvelopesAmount;
        if (moneyOwed < 0) {
            moneyOwed = -moneyOwed;
            textMoneyRemaining.setTextColor(getResources().getColor(R.color.red));
        }
        textMoneyRemaining.setText(formatMoney(moneyOwed));
    }
}
