package com.daniel.ethan.paymentmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

import static com.daniel.ethan.paymentmanager.Utils.formatMoney;

public class EnvelopesAdapter extends RecyclerSwipeAdapter<EnvelopesAdapter.ViewHolder> {

    private ArrayList<String> envelopeNames;
    private ArrayList<Double> envelopeCurrentAmounts;
    private ArrayList<Double> envelopeAutoUpdateAmounts;
    private Context mContext;

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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.envelopeName.setText(envelopeNames.get(i));
        viewHolder.currentAmount.setText(formatMoney(envelopeCurrentAmounts.get(i)));
        viewHolder.autoUpdateAmount.setText(formatMoney(envelopeAutoUpdateAmounts.get(i)));

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
