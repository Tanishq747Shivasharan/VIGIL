package com.vigil.security.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vigil.security.R;
import com.vigil.security.models.LanDevice;

import java.util.ArrayList;
import java.util.List;

public class LanDeviceAdapter extends RecyclerView.Adapter<LanDeviceAdapter.DeviceViewHolder> {

    private List<LanDevice> deviceList;

    private Context context;

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        CardView cardDevice;
        TextView tvIpAddress, tvMacAddress, tvVendor;
        TextView tvHostname, tvStatusBadge, tvOnlineIndicator;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDevice        = itemView.findViewById(R.id.card_device);
            tvIpAddress       = itemView.findViewById(R.id.tv_device_ip);
            tvMacAddress      = itemView.findViewById(R.id.tv_device_mac);
            tvVendor          = itemView.findViewById(R.id.tv_device_vendor);
            tvHostname        = itemView.findViewById(R.id.tv_device_hostname);
            tvStatusBadge     = itemView.findViewById(R.id.tv_device_status_badge);
            tvOnlineIndicator = itemView.findViewById(R.id.tv_online_indicator);
        }
    }

    public LanDeviceAdapter(Context context) {
        this.context = context;
        // Initialize with an empty list — we'll add devices as they're found
        this.deviceList = new ArrayList<>();
    }

    // =========================================================================
    // THE THREE REQUIRED OVERRIDES
    // =========================================================================

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lan_device, parent, false);

        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        LanDevice device = deviceList.get(position);

        holder.tvIpAddress.setText(device.getIpAddress());
        holder.tvMacAddress.setText(device.getMacAddress());
        holder.tvVendor.setText(device.getVendor());

        String hostname = device.getHostname();
        if (hostname != null && !hostname.isEmpty()) {
            holder.tvHostname.setText(hostname);
            holder.tvHostname.setVisibility(View.VISIBLE);
        } else {
            holder.tvHostname.setVisibility(View.GONE);
        }

        holder.tvStatusBadge.setText(device.getStatusLabel());

        if (device.isCurrentDevice()) {
            // Blue for "This Device"
            holder.tvStatusBadge.setTextColor(Color.parseColor("#2980B9"));
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#1A2980B9"));
            holder.cardDevice.setCardBackgroundColor(Color.parseColor("#F0F8FF"));
        } else if (device.isGateway()) {
            // Gold for "Router"
            holder.tvStatusBadge.setTextColor(Color.parseColor("#8B6914"));
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#1AF39C12"));
            holder.cardDevice.setCardBackgroundColor(Color.parseColor("#FFFDF5"));
        } else {
            // Default white card for regular devices
            holder.tvStatusBadge.setTextColor(Color.parseColor("#543310"));
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#1A543310"));
            holder.cardDevice.setCardBackgroundColor(Color.WHITE);
        }

        if (device.isOnline()) {
            holder.tvOnlineIndicator.setText("●");
            holder.tvOnlineIndicator.setTextColor(Color.parseColor("#27AE60")); // Green
        } else {
            holder.tvOnlineIndicator.setText("●");
            holder.tvOnlineIndicator.setTextColor(Color.parseColor("#BDC3C7")); // Gray
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void addDevice(LanDevice device) {
        deviceList.add(device);
        // notifyItemInserted triggers the insert animation for the new card
        notifyItemInserted(deviceList.size() - 1);
    }

    public void clearDevices() {
        deviceList.clear();
        notifyDataSetChanged(); // Refresh everything
    }

    public int getDeviceCount() {
        return deviceList.size();
    }
}
