package com.vigil.security.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vigil.security.R;
import com.vigil.security.models.SmsMessage;

import java.util.ArrayList;
import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder>{
    private List<SmsMessage> smsList;
    private Context context;

    private int expandedPosition = RecyclerView.NO_POSITION;

    public static class SmsViewHolder extends RecyclerView.ViewHolder {

        // Always visible (collapsed state)
        CardView cardSms;
        TextView tvSender;
        TextView tvBodyPreview;
        TextView tvTimestamp;
        TextView tvRiskBadge;
        TextView tvExpandArrow;  // "▼" or "▲"

        // Only visible when expanded
        LinearLayout layoutExpanded;
        TextView tvFullBody;
        TextView tvKeywords;
        TextView tvExplanation;
        TextView tvRiskScore;

        public SmsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSms         = itemView.findViewById(R.id.card_sms);
            tvSender        = itemView.findViewById(R.id.tv_sms_sender);
            tvBodyPreview   = itemView.findViewById(R.id.tv_sms_body_preview);
            tvTimestamp     = itemView.findViewById(R.id.tv_sms_timestamp);
            tvRiskBadge     = itemView.findViewById(R.id.tv_sms_risk_badge);
            tvExpandArrow   = itemView.findViewById(R.id.tv_expand_arrow);
            layoutExpanded  = itemView.findViewById(R.id.layout_sms_expanded);
            tvFullBody      = itemView.findViewById(R.id.tv_sms_full_body);
            tvKeywords      = itemView.findViewById(R.id.tv_sms_keywords);
            tvExplanation   = itemView.findViewById(R.id.tv_sms_explanation);
            tvRiskScore     = itemView.findViewById(R.id.tv_sms_risk_score);
        }
    }

    public SmsAdapter(Context context) {
        this.context = context;
        this.smsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sms, parent, false);
        return new SmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        SmsMessage sms = smsList.get(position);

        // --- Collapsed state (always visible) ---
        holder.tvSender.setText(sms.getSender());
        holder.tvBodyPreview.setText(sms.getBodyPreview());
        holder.tvTimestamp.setText(sms.getFormattedTimestamp());
        holder.tvRiskBadge.setText(sms.getRiskLabel());

        // Color the risk badge
        int color = Color.parseColor(sms.getColorHex());
        holder.tvRiskBadge.setTextColor(color);

        // Left border color on the card — red for phishing, green for safe
        // We simulate this with the card background tint
        if (sms.isPhishing()) {
            holder.cardSms.setCardBackgroundColor(Color.parseColor("#FFF8F8")); // light red tint
        } else {
            holder.cardSms.setCardBackgroundColor(Color.WHITE);
        }

        boolean isExpanded = (position == expandedPosition);

        if (isExpanded) {
            // Show expanded section
            holder.layoutExpanded.setVisibility(View.VISIBLE);
            holder.tvExpandArrow.setText("▲");

            // Fill expanded fields
            holder.tvFullBody.setText(sms.getBody());
            holder.tvKeywords.setText("Matched: " + sms.getMatchedKeywordsDisplay());
            holder.tvExplanation.setText(sms.getExplanation());
            holder.tvRiskScore.setText("Risk Score: " + sms.getRiskScore() + " / 100");
            holder.tvRiskScore.setTextColor(color);
        } else {
            // Hide expanded section
            holder.layoutExpanded.setVisibility(View.GONE);
            holder.tvExpandArrow.setText("▼");
        }

        // --- Click listener: toggle expand/collapse ---
        holder.cardSms.setOnClickListener(v -> {

            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return;

            int previousExpanded = expandedPosition;

            if (expandedPosition == clickedPosition) {
                // This card is already expanded → collapse it
                expandedPosition = RecyclerView.NO_POSITION;
            } else {
                // A different card was tapped → expand it (previous auto-collapses)
                expandedPosition = clickedPosition;
            }

            // Update only the items that changed for better performance
            if (previousExpanded != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousExpanded);
            }
            notifyItemChanged(expandedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public void addMessage(SmsMessage sms) {
        smsList.add(0, sms);
        notifyItemInserted(0);
    }

    public void setMessages(List<SmsMessage> messages) {
        smsList.clear();
        smsList.addAll(messages);
        notifyDataSetChanged();
    }

    /** Returns count of messages currently displayed */
    public int getMessageCount() { return smsList.size(); }

    public int getPhishingCount() {
        int count = 0;
        for (SmsMessage sms : smsList) {
            if (sms.isPhishing()) count++;
        }
        return count;
    }

    /** Clears all messages from the list */
    public void clearMessages() {
        smsList.clear();
        expandedPosition = RecyclerView.NO_POSITION; // reset expansion too
        notifyDataSetChanged();
    }
}
