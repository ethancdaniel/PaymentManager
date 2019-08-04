package com.daniel.ethan.paymentmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.daniel.ethan.paymentmanager.Utils.formatMoney;

public class EnvelopesAdapter extends RecyclerSwipeAdapter<EnvelopesAdapter.ViewHolder> {

    private ArrayList<String> envelopeNames;
    private ArrayList<Double> envelopeCurrentAmounts;
    private ArrayList<Double> envelopeAutoUpdateAmounts;
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public EnvelopesAdapter(ArrayList<String> envelopeNames, ArrayList<Double> envelopeCurrentAmounts, ArrayList<Double> envelopeAutoUpdateAmounts, Context mContext) {
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
        final Double amount = envelopeCurrentAmounts.get(i);
        final Double autoUpdate = envelopeAutoUpdateAmounts.get(i);
        viewHolder.envelopeName.setText(name);
        viewHolder.currentAmount.setText(formatMoney(amount));
        viewHolder.autoUpdateAmount.setText(formatMoney(autoUpdate));

        viewHolder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEnvelopeDialog dialog = new EditEnvelopeDialog(name, amount.toString(), autoUpdate.toString(), new EditEnvelopeDialog.EditEnvelopeDialogListener() {
                    @Override
                    public void onEditEnvelope(String name, Double amount, Double autoUpdate) {
                        db.collection("Envelopes").document(mAuth.getUid()).collection("User Envelopes").document("" + i).update(
                                "name", name,
                                "amount", amount,
                                "autoUpdate", autoUpdate
                        );
                    }
                });
                dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "edit dialog");
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            envelopeName = itemView.findViewById(R.id.text_envelope_name);
            currentAmount = itemView.findViewById(R.id.text_current_amount);
            autoUpdateAmount = itemView.findViewById(R.id.text_auto_update_amount);
            swipeLayout = itemView.findViewById(R.id.swipe);
            editImageView = itemView.findViewById(R.id.edit_envelope);
            deleteImageView = itemView.findViewById(R.id.delete_envelope);
        }
    }
}
