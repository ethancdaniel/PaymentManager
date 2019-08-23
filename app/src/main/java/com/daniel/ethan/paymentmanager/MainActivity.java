package com.daniel.ethan.paymentmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.daniel.ethan.paymentmanager.Utils.formatMoney;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final int ENVELOPE_SPACING = 10;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button btnMoneyInBank;
    private Button btnMoneyChecksNotCashed;

    private TextView textMoneyInEnvelopes;
    private TextView textMoneyRemaining;
    private TextView textNoEnvelopes;
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
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("amount", amount);
                        map.put("autoUpdate", autoUpdate);
                        map.put("creationDate", Calendar.getInstance().getTime());
                        db.collection("Envelopes").document(mAuth.getUid()).collection("User Envelopes").document( "" + envelopeNames.size()).set(map);

                        envelopeNames.add(name);
                        currentAmounts.add(amount);
                        autoUpdateAmounts.add(autoUpdate);
                        envelopesAdapter.notifyDataSetChanged();
                        textNoEnvelopes.setVisibility(View.GONE);
                        envelopesRecycler.setVisibility(View.VISIBLE);
                        updateMoneyOwed();
                    }
                });
                dialog.setRetainInstance(true);
                dialog.show(getSupportFragmentManager(), "new envelope dialog");
                return true;
            case R.id.action_sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
                        moneyInbank = (double) document.get("moneyInBank");
                        moneyChecksNotCashed = (double) document.get("moneyNotCashed");

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

        CollectionReference cRef = db.collection("Envelopes").document(mAuth.getUid()).collection("User Envelopes");
        cRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() == 0) {
                    textMoneyInEnvelopes.setVisibility(View.VISIBLE);
                    envelopesRecycler.setVisibility(View.GONE);
                }
                envelopeNames.clear();
                currentAmounts.clear();
                autoUpdateAmounts.clear();

                for (DocumentSnapshot child : queryDocumentSnapshots.getDocuments() ){
                    Log.d(TAG, "" + child.getData());
                    envelopeNames.add(((String) child.get("name")));
                    currentAmounts.add((double) child.get("amount"));
                    autoUpdateAmounts.add((double) child.get("autoUpdate"));
                }
                envelopesAdapter.notifyDataSetChanged();
            }
        });

    }
    private void initComponents() {
        btnMoneyInBank = findViewById(R.id.btn_money_in_bank);
        btnMoneyChecksNotCashed = findViewById(R.id.btn_checks_not_cashed);
        textMoneyInEnvelopes = findViewById(R.id.text_money_in_envelopes);
        textMoneyRemaining = findViewById(R.id.text_money_remaining);
        textNoEnvelopes = findViewById(R.id.text_no_envelopes);
        envelopesRecycler = findViewById(R.id.envelopes_recycler);
        envelopesRecycler.setHasFixedSize(true);
        textMoneyInEnvelopes.setText(formatMoney(totalEnvelopesAmount));

        envelopeNames = new ArrayList<>();
        currentAmounts = new ArrayList<>();
        autoUpdateAmounts = new ArrayList<>();

        envelopesAdapter = new EnvelopesAdapter(envelopeNames, currentAmounts, autoUpdateAmounts, this);
        envelopesAdapter.setEnvelopePaidListener(new EnvelopesAdapter.onEnvelopeActionListener() {
            @Override
            public void onEnvelopePaid(double newBankAmount) {
                btnMoneyInBank.setText(formatMoney(newBankAmount));
            }

            @Override
            public void onEnvelopeEdited() {
                updateMoneyOwed();
            }
        });
        envelopesRecycler.setAdapter(envelopesAdapter);
        envelopesRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        SpacingItemDecoration decoration = new SpacingItemDecoration(ENVELOPE_SPACING);
        envelopesRecycler.addItemDecoration(decoration);
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

        if (envelopeNames.size() > 0) {
            textNoEnvelopes.setVisibility(View.GONE);
        }

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
