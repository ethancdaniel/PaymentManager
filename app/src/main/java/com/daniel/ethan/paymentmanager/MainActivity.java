package com.daniel.ethan.paymentmanager;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.daniel.ethan.paymentmanager.Utils.formatMoney;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button btnMoneyInBank;
    private Button btnMoneyChecksNotCashed;

    private TextView textMoneyInEnvelopes;
    private TextView textMoneyRemaining;
    private RecyclerView envelopesRecycler;
    private double totalEnvelopesAmount;
    private EnvelopesAdapter envelopesAdapter;

    //test data
    private ArrayList<String> envelopeNames;
    private ArrayList<Double> currentAmounts;
    private ArrayList<Double> autoUpdateAmounts;
    private Double moneyInbank;
    private Double moneyChecksNotCashed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        getUserData();
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
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("amount", amount);
                        map.put("autoUpdate", autoUpdate);
                        db.collection("Envelopes").document(mAuth.getUid()).set(map);
                    }
                });
                dialog.setRetainInstance(true);
                dialog.show(getSupportFragmentManager(), "new envelope dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getUserData() {
        mAuth = FirebaseAuth.getInstance();
        DocumentReference ref = db.collection("Money").document(mAuth.getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        moneyInbank = ((Long) document.get("moneyInBank")).doubleValue();
                        moneyChecksNotCashed = ((Long) document.get("moneyNotCashed")).doubleValue();

                        btnMoneyInBank.setText(formatMoney(moneyInbank));
                        btnMoneyChecksNotCashed.setText(formatMoney(moneyChecksNotCashed));
                        updateMoneyOwed();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        ref = db.collection("Envelopes").document(mAuth.getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        updateMoneyOwed();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
    private void initComponents() {
        btnMoneyInBank = findViewById(R.id.btn_money_in_bank);
        btnMoneyChecksNotCashed = findViewById(R.id.btn_checks_not_cashed);
        textMoneyInEnvelopes = findViewById(R.id.text_money_in_envelopes);
        textMoneyRemaining = findViewById(R.id.text_money_remaining);
        envelopesRecycler = findViewById(R.id.envelopes_recycler);
        envelopesRecycler.setHasFixedSize(true);
        textMoneyInEnvelopes.setText(formatMoney(totalEnvelopesAmount));

        envelopesAdapter = new EnvelopesAdapter(envelopeNames, currentAmounts, autoUpdateAmounts, this);
        envelopesRecycler.setAdapter(envelopesAdapter);
        envelopesRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(envelopesRecycler.getContext(), llm.getOrientation());
        envelopesRecycler.addItemDecoration(dividerItemDecoration);
        envelopesRecycler.setLayoutManager(llm);

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
                        db.collection("Money").document(mAuth.getUid()).update("moneyInBank", amount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Money in bank updated successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating money in bank");
                            }
                        });
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
                        db.collection("Money").document(mAuth.getUid()).update("moneyNotCashed", amount)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Money in bank updated successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating money in bank");
                                    }
                                });
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
            textMoneyRemaining.setTextColor(ContextCompat.getColor(this, R.color.red));
            textMoneyRemaining.setAlpha(1.0f);
        } else {
            textMoneyRemaining.setTextColor(Color.parseColor("#000000"));
            textMoneyRemaining.setAlpha(0.54f);
        }
        textMoneyRemaining.setText(formatMoney(moneyOwed));
    }
}
