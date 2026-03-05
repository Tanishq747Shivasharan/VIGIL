package com.vigil.security.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vigil.security.R;
import com.vigil.security.adapters.LanDeviceAdapter;
import com.vigil.security.models.LanDevice;
import com.vigil.security.security.LanScanner;

import java.util.List;

public class LanFragment extends Fragment {
    // -------------------------------------------------------------------------
    // VIEW REFERENCES — populated in onCreateView via findViewById()
    // -------------------------------------------------------------------------
    private RecyclerView recyclerDevices;
    private Button btnScan;
    private ProgressBar progressScan;
    private TextView tvScanStatus;
    private TextView tvDeviceCount;
    private TextView tvProgressLabel;

    // -------------------------------------------------------------------------
    // NON-VIEW FIELDS
    // -------------------------------------------------------------------------
    private LanDeviceAdapter adapter;  // Connects data to RecyclerView
    private LanScanner scanner;        // Does the actual network scanning
    private boolean isScanning = false; // Tracks current scan state

    // -------------------------------------------------------------------------
    // onCreateView()
    //
    // This is the Fragment's equivalent of Activity.onCreate().
    // It's called when the fragment's UI is being created.
    //
    // @param inflater  → used to build views from XML
    // @param container → the parent ViewGroup this fragment will be placed into
    // @param savedInstanceState → any previously saved state (can be null)
    // -------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Step 1: Inflate the layout file into a View object
        View view = inflater.inflate(R.layout.fragment_lan, container, false);

        // Step 2: Get references to all UI elements using their XML IDs
        recyclerDevices = view.findViewById(R.id.recycler_devices);
        btnScan         = view.findViewById(R.id.btn_scan_lan);
        progressScan    = view.findViewById(R.id.progress_scan);
        tvScanStatus    = view.findViewById(R.id.tv_scan_status);
        tvDeviceCount   = view.findViewById(R.id.tv_device_count);
        tvProgressLabel = view.findViewById(R.id.tv_progress_label);

        // Step 3: Set up the RecyclerView
        setupRecyclerView();

        // Step 4: Initialize the scanner
        scanner = new LanScanner(requireContext());

        // Step 5: Wire up the scan button
        btnScan.setOnClickListener(v -> {
            if (isScanning) {
                stopScan(); // If currently scanning, button becomes "Stop"
            } else {
                startScan(); // Otherwise, start a new scan
            }
        });

        return view;
    }

    // -------------------------------------------------------------------------
    // setupRecyclerView()
    //
    // Configures the RecyclerView:
    //   1. LinearLayoutManager → stacks cards vertically (like a simple list)
    //   2. Attach our adapter so RecyclerView knows where to get card data
    // -------------------------------------------------------------------------
    private void setupRecyclerView() {
        // LinearLayoutManager = cards stacked top-to-bottom
        // Other options: GridLayoutManager (grid), StaggeredGridLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerDevices.setLayoutManager(layoutManager);

        // Create the adapter and attach it
        adapter = new LanDeviceAdapter(requireContext());
        recyclerDevices.setAdapter(adapter);
    }

    // -------------------------------------------------------------------------
    // startScan()
    //
    // 1. Updates UI to "scanning" state
    // 2. Clears old results
    // 3. Starts the scanner with a callback implementation
    // -------------------------------------------------------------------------
    private void startScan() {
        isScanning = true;

        // Update UI state
        adapter.clearDevices();
        btnScan.setText("Stop Scan");
        progressScan.setVisibility(View.VISIBLE);
        progressScan.setProgress(0);
        tvScanStatus.setText("Scanning network...");
        tvScanStatus.setTextColor(Color.parseColor("#F39C12")); // Orange = in progress
        tvDeviceCount.setText("0 devices found");
        tvProgressLabel.setVisibility(View.VISIBLE);
        tvProgressLabel.setText("0 / 254");

        // -----------------------------------------------------------------------
        // Start scan with an anonymous class implementing ScanCallback.
        //
        // "Anonymous class" = a class with no name, defined and instantiated
        // in one shot. It's like a one-time-use implementation of the interface.
        //
        // new LanScanner.ScanCallback() { @Override ... @Override ... }
        //   creates an object that implements all 4 interface methods.
        // -----------------------------------------------------------------------
        scanner.startScan(new LanScanner.ScanCallback() {

            /**
             * onDeviceFound() — called from a BACKGROUND thread each time a
             * device responds. We must switch to the main thread to update UI.
             */
            @Override
            public void onDeviceFound(LanDevice device) {
                // requireActivity().runOnUiThread() → schedule this Runnable
                // to execute on the main thread ASAP
                requireActivity().runOnUiThread(() -> {
                    adapter.addDevice(device); // Update RecyclerView
                    tvDeviceCount.setText(adapter.getDeviceCount() + " devices found");
                });
            }

            /**
             * onScanComplete() — called from background thread when all 254
             * IPs have been checked. Update UI to "complete" state.
             */
            @Override
            public void onScanComplete(List<LanDevice> allDevices) {
                requireActivity().runOnUiThread(() -> {
                    isScanning = false;
                    btnScan.setText("Scan Again");
                    progressScan.setVisibility(View.GONE);
                    tvProgressLabel.setVisibility(View.GONE);
                    tvScanStatus.setText("Scan complete");
                    tvScanStatus.setTextColor(Color.parseColor("#27AE60")); // Green = done

                    int count = allDevices.size();
                    tvDeviceCount.setText(count + (count == 1 ? " device found" : " devices found"));
                });
            }

            /**
             * onProgress() — called from background thread after each IP is scanned.
             * Updates the progress bar and the "X / 254" label.
             *
             * @param scanned how many IPs checked so far
             * @param total   total IPs to check (254)
             */
            @Override
            public void onProgress(int scanned, int total) {
                requireActivity().runOnUiThread(() -> {
                    // Calculate percentage: (scanned / total) * 100
                    // Note: we cast to float first to avoid integer division
                    // e.g. 100/254 in integers = 0 (wrong!)
                    //      (float)100/254 = 0.394... → * 100 = 39 (correct)
                    int percent = (int) ((float) scanned / total * 100);
                    progressScan.setProgress(percent);
                    tvProgressLabel.setText(scanned + " / " + total);
                });
            }

            /**
             * onError() — called if something goes wrong (no WiFi, permission denied, etc.)
             */
            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    isScanning = false;
                    btnScan.setText("Scan Network");
                    progressScan.setVisibility(View.GONE);
                    tvProgressLabel.setVisibility(View.GONE);
                    tvScanStatus.setText("Error: " + errorMessage);
                    tvScanStatus.setTextColor(Color.parseColor("#C0392B")); // Red = error
                });
            }
        });
    }

    // -------------------------------------------------------------------------
    // stopScan()
    //
    // Cancels an in-progress scan and resets the UI.
    // -------------------------------------------------------------------------
    private void stopScan() {
        scanner.cancelScan();
        isScanning = false;
        btnScan.setText("Scan Network");
        progressScan.setVisibility(View.GONE);
        tvProgressLabel.setVisibility(View.GONE);
        tvScanStatus.setText("Scan cancelled");
        tvScanStatus.setTextColor(Color.parseColor("#7F8C8D")); // Gray = cancelled
    }

    // -------------------------------------------------------------------------
    // onDestroyView()
    //
    // Called when this Fragment's UI is being destroyed (e.g. user navigates away).
    // We cancel any running scan to prevent background threads from trying to
    // update UI that no longer exists (which would crash the app).
    //
    // @Override = we're replacing the parent Fragment's version of this method
    // -------------------------------------------------------------------------
    @Override
    public void onDestroyView() {
        super.onDestroyView(); // Always call super first
        if (scanner != null) {
            scanner.cancelScan();
        }
    }
}