package com.daniel.ethan.paymentmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.daniel.ethan.paymentmanager.Utils.formatMoney;

public class EnvelopesAdapter extends RecyclerSwipeAdapter<EnvelopesAdapter.ViewHolder> {

    private static final String TAG = "EnvelopesAdapter";
    private TextView noEnvelopesText;
    private ArrayList<String> envelopeNames;
    private ArrayList<Double> envelopeCurrentAmounts;
    private ArrayList<Double> envelopeAutoUpdateAmounts;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private onEnvelopeActionListener envelopeListener;

    public EnvelopesAdapter(ArrayList<String> envelopeNames, ArrayList<Double> envelopeCurrentAmounts, ArrayList<Double> envelopeAutoUpdateAmounts, Context mContext) {
        noEnvelopesText = ((AppCompatActivity) mContext).findViewById(R.id.text_no_envelopes);
        this.envelopeNames = envelopeNames;
        this.envelopeCurrentAmounts = envelopeCurrentAmounts;
        this.envelopeAutoUpdateAmounts = envelopeAutoUpdateAmounts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_envelope, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final String name = envelopeNames.get(i);
        final Double amountInEnvelope = envelopeCurrentAmounts.get(i);
        final Double autoUpdate = envelopeAutoUpdateAmounts.get(i);
        viewHolder.envelopeName.setText(name);
        viewHolder.currentAmount.setText(formatMoney(amountInEnvelope));
        viewHolder.autoUpdateAmount.setText(formatMoney(autoUpdate));

        viewHolder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEnvelopeDialog dialog = new EditEnvelopeDialog(name, amountInEnvelope.toString(), autoUpdate.toString(), new EditEnvelopeDialog.EditEnvelopeDialogListener() {
                    @Override
                    public void onEditEnvelope(String name, Double amount, Double autoUpdate) {
                        db.collection("Envelopes").document(mAuth.getUid()).collection("User Envelopes").document("" + i).update(
                                "name", name,
                                "amount", amount,
                                "autoUpdate", autoUpdate
                        );
                        envelopeListener.onEnvelopeEdited();
                    }
                });
                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "edit dialog");
            }
        });
        viewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteEnvelopeDialog dialog = new DeleteEnvelopeDialog(new DeleteEnvelopeDialog.DeleteEnvelopeDialogListener() {
                    @Override
                    public void onDeletePressed() {
                        db.collection("Envelopes").document(mAuth.getUid()).collection("User Envelopes").document("" + i).delete();
                        envelopeNames.remove(i);
                        envelopeCurrentAmounts.remove(i);
                        envelopeAutoUpdateAmounts.remove(i);

                        if (envelopeNames.isEmpty()) {
                            noEnvelopesText.setVisibility(View.VISIBLE);
                        }
                        envelopeListener.onEnvelopeEdited();
                    }
                });
                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "delete dialog");
            }
        });
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayEnvelopeDialog dialog = new PayEnvelopeDialog(name, formatMoney(amountInEnvelope), new PayEnvelopeDialog.PayEnvelopeDialogListener() {
                    @Override
                    public void onPayPressed(final double amount) {
                        envelopeCurrentAmounts.set(i, amountInEnvelope - amount);
                        db.collection("Envelopes").document(mAuth.getUid()).collection("User Envelopes").document("" + i).update(
                                "amount", envelopeCurrentAmounts.get(i));

                        DocumentReference ref = db.collection("Money").document(mAuth.getUid());
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        Double moneyInBank = (Double) document.get("moneyInBank");
                                        Double newBankAmount = moneyInBank - amount;
                                        db.collection("Money").document(mAuth.getUid()).update("moneyInBank", newBankAmount);
                                        envelopeListener.onEnvelopePaid(newBankAmount);
                                    }
                                }
                            }
                        });

                    }
                });
                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "pay dialog");
            }
        });
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
    }

    @Override
    public int getItemCount() {
        return envelopeNames.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView envelopeName;
        TextView currentAmount;
        TextView autoUpdateAmount;
        SwipeLayout swipeLayout;
        ImageView editImageView;
        ImageView deleteImageView;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            envelopeName = itemView.findViewById(R.id.text_envelope_name);
            currentAmount = itemView.findViewById(R.id.text_current_amount);
            autoUpdateAmount = itemView.findViewById(R.id.text_auto_update_amount);
            swipeLayout = itemView.findViewById(R.id.swipe);
            editImageView = itemView.findViewById(R.id.edit_envelope);
            deleteImageView = itemView.findViewById(R.id.delete_envelope);
            layout = itemView.findViewById(R.id.layout_envelope);
        }
    }

    public void setEnvelopePaidListener(onEnvelopeActionListener listener) {
        this.envelopeListener = listener;
    }

    public interface onEnvelopeActionListener {
        void onEnvelopePaid(double newBankAmount);
        void onEnvelopeEdited();
    }
}


