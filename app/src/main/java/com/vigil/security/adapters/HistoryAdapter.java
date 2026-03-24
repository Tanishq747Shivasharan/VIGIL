package com.vigil.security.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vigil.security.R;
import com.vigil.security.models.ScanRecord;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnItemActionListener {
        void onItemClick(ScanRecord record);
        void onItemDelete(ScanRecord record, int position);
    }

    private List<ScanRecord>     records;
    private OnItemActionListener listener;

    public HistoryAdapter(OnItemActionListener listener) {
        this.records  = new ArrayList<>();
        this.listener = listener;
    }

    public void setRecords(List<ScanRecord> newRecords) {
        this.records = new ArrayList<>(newRecords);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        records.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, records.size());
    }

    public ScanRecord getAt(int position) {
        return records.get(position);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() { return records.size(); }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;
        TextView tvIcon, tvScanType, tvDate, tvSummary, tvRiskBadge;
        View     viewRiskStripe;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot       = itemView.findViewById(R.id.card_history_item);
            tvIcon         = itemView.findViewById(R.id.tv_history_icon);
            tvScanType     = itemView.findViewById(R.id.tv_history_scan_type);
            tvDate         = itemView.findViewById(R.id.tv_history_date);
            tvSummary      = itemView.findViewById(R.id.tv_history_summary);
            tvRiskBadge    = itemView.findViewById(R.id.tv_history_risk_badge);
            viewRiskStripe = itemView.findViewById(R.id.view_risk_stripe);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_ID && listener != null)
                    listener.onItemClick(records.get(pos));
            });
        }

        void bind(ScanRecord record) {
            tvIcon.setText(record.getScanTypeIcon());
            tvScanType.setText(record.getScanTypeLabel());
            tvDate.setText(record.getFormattedDate());
            tvSummary.setText(record.getSummary());
            tvRiskBadge.setText(record.getRiskLabel());

            int color = getRiskColor(record.getRiskLevel());
            tvRiskBadge.setBackgroundColor(color);
            viewRiskStripe.setBackgroundColor(color);
        }

        private int getRiskColor(String riskLevel) {
            if (riskLevel == null) return Color.parseColor("#95A5A6");
            switch (riskLevel) {
                case ScanRecord.RISK_CRITICAL: return Color.parseColor("#922B21");
                case ScanRecord.RISK_HIGH:     return Color.parseColor("#C0392B");
                case ScanRecord.RISK_MEDIUM:   return Color.parseColor("#E67E22");
                case ScanRecord.RISK_LOW:      return Color.parseColor("#F1C40F");
                case ScanRecord.RISK_SAFE:     return Color.parseColor("#27AE60");
                default:                       return Color.parseColor("#95A5A6");
            }
        }
    }
}
