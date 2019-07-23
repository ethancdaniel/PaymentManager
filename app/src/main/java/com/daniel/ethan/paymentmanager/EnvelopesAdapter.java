package com.daniel.ethan.paymentmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EnvelopesAdapter extends RecyclerView.Adapter<EnvelopesAdapter.ViewHolder> {

    private ArrayList<String> envelopeNames;
    private ArrayList<Integer> envelopeCurrentAmounts;
    private ArrayList<Integer> envelopeAutoUpdateAmounts;
    private Context mContext;

    public EnvelopesAdapter(ArrayList<String> envelopeNames, ArrayList<Integer> envelopeCurrentAmounts, ArrayList<Integer> envelopeAutoUpdateAmounts, Context mContext) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.envelopeName.setText(envelopeNames.get(i));
        viewHolder.currentAmount.setText("$" + envelopeCurrentAmounts.get(i));
        viewHolder.autoUpdateAmount.setText("$" + envelopeAutoUpdateAmounts.get(i));
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, envelopeNames.get(i) + " clicked.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return envelopeNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView envelopeName;
        TextView currentAmount;
        TextView autoUpdateAmount;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            envelopeName = itemView.findViewById(R.id.text_envelope_name);
            currentAmount = itemView.findViewById(R.id.text_current_amount);
            autoUpdateAmount = itemView.findViewById(R.id.text_auto_update_amount);
            layout = itemView.findViewById(R.id.layout_envelope_item);
        }
    }
}
