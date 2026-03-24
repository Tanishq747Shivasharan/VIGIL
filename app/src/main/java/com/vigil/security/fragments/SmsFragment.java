package com.vigil.security.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vigil.security.R;
import com.vigil.security.adapters.SmsAdapter;
import com.vigil.security.models.SmsMessage;
import com.vigil.security.receivers.SmsReceiver;
import com.vigil.security.security.PhishingAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SmsFragment extends Fragment {
    private RecyclerView recyclerSms;
    private Button btnScanInbox;
    private ProgressBar progressScan;
    private TextView tvScanStatus;
    private TextView tvTotalCount;
    private TextView tvPhishingCount;

    private SmsAdapter adapter;

    private final BroadcastReceiver liveSmsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String sender      = intent.getStringExtra(SmsReceiver.EXTRA_SENDER);
            String body        = intent.getStringExtra(SmsReceiver.EXTRA_BODY);
            long timestamp     = intent.getLongExtra(SmsReceiver.EXTRA_TIMESTAMP, 0L);
            int riskScore      = intent.getIntExtra(SmsReceiver.EXTRA_RISK_SCORE, 0);
            String riskLabel   = intent.getStringExtra(SmsReceiver.EXTRA_RISK_LABEL);
            String colorHex    = intent.getStringExtra(SmsReceiver.EXTRA_COLOR_HEX);
            String explanation = intent.getStringExtra(SmsReceiver.EXTRA_EXPLANATION);
            boolean isPhishing = intent.getBooleanExtra(SmsReceiver.EXTRA_IS_PHISHING, false);

            // Build the SmsMessage model from extracted data
            SmsMessage sms = new SmsMessage(sender, body, timestamp);
            sms.setRiskScore(riskScore);
            sms.setRiskLabel(riskLabel != null ? riskLabel : "Safe");
            sms.setColorHex(colorHex != null ? colorHex : "#27AE60");
            sms.setExplanation(explanation != null ? explanation : "");
            sms.setPhishing(isPhishing);

            // Add to top of list (it's a new message — most recent first)
            adapter.addMessage(sms);
            updateCounters();

            // Update status to show live detection is working
            tvScanStatus.setText("Live monitoring active — new message detected");
            tvScanStatus.setTextColor(isPhishing
                    ? Color.parseColor("#E74C3C")   // Red if phishing
                    : Color.parseColor("#27AE60"));  // Green if safe
        }
    };

    // =========================================================================
    // FRAGMENT LIFECYCLE
    // =========================================================================

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sms, container, false);

        // Find views
        recyclerSms      = view.findViewById(R.id.recycler_sms);
        btnScanInbox     = view.findViewById(R.id.btn_scan_inbox);
        progressScan     = view.findViewById(R.id.progress_sms_scan);
        tvScanStatus     = view.findViewById(R.id.tv_sms_scan_status);
        tvTotalCount     = view.findViewById(R.id.tv_sms_total_count);
        tvPhishingCount  = view.findViewById(R.id.tv_sms_phishing_count);

        setupRecyclerView();

        btnScanInbox.setOnClickListener(v -> scanInbox());

        registerLiveReceiver();

        return view;
    }

    /**
     * onDestroyView() — called when the Fragment's UI is being torn down.
     *
     * CRITICAL: We MUST unregister the BroadcastReceiver here.
     * If we don't, it keeps running after the Fragment is gone,
     * tries to update destroyed views, and CRASHES the app.
     *
     * This is the Fragment equivalent of "always close what you open".
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterLiveReceiver();
    }

    // =========================================================================
    // SETUP METHODS
    // =========================================================================

    private void setupRecyclerView() {
        recyclerSms.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SmsAdapter(requireContext());
        recyclerSms.setAdapter(adapter);
        recyclerSms.setNestedScrollingEnabled(false);
    }

    /**
     * registerLiveReceiver()
     *
     * Registers liveSmsReceiver with LocalBroadcastManager to listen for
     * ACTION_NEW_SMS broadcasts sent by SmsReceiver.
     *
     * IntentFilter = specifies WHICH broadcasts to listen for.
     *   new IntentFilter(action) = only receive broadcasts with this action string.
     *
     * LocalBroadcastManager.registerReceiver(receiver, filter):
     *   receiver = the BroadcastReceiver to call when a match is found
     *   filter   = the IntentFilter describing what to listen for
     */
    private void registerLiveReceiver() {
        IntentFilter filter = new IntentFilter(SmsReceiver.ACTION_NEW_SMS);
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(liveSmsReceiver, filter);
    }

    /**
     * unregisterLiveReceiver()
     *
     * Unregisters the receiver. Must be called in onDestroyView().
     * Failing to unregister causes memory leaks and crashes.
     */
    private void unregisterLiveReceiver() {
        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(liveSmsReceiver);
    }

    // =========================================================================
    // INBOX SCAN — ContentResolver + Cursor
    // =========================================================================

    /**
     * scanInbox()
     *
     * Reads SMS messages from the inbox using Android's ContentResolver.
     *
     * THREADING NOTE:
     *   ContentResolver.query() is a BLOCKING operation — it can take several
     *   seconds for large inboxes. We run it in a background thread to avoid
     *   freezing the UI, then post results back to the main thread.
     *
     * WHY ContentResolver?
     *   Android stores SMS in a system Content Provider — a shared database
     *   accessible via URIs like "content://sms/inbox".
     *   ContentResolver is the API to query Content Providers.
     */
    private void scanInbox() {

        // Update UI to show scanning has started
        btnScanInbox.setText("Scanning...");
        btnScanInbox.setEnabled(false);
        progressScan.setVisibility(View.VISIBLE);
        tvScanStatus.setText("Reading inbox...");
        tvScanStatus.setTextColor(Color.parseColor("#F39C12"));
        adapter.clearMessages();

        // Run the heavy ContentResolver query in a background thread
        new Thread(() -> {

            List<SmsMessage> analyzedMessages = new ArrayList<>();

            try {
                // ---------------------------------------------------------------
                // CONTENT RESOLVER QUERY
                //
                // Uri.parse("content://sms/inbox") = the SMS inbox "table"
                //
                // query(uri, projection, selection, selectionArgs, sortOrder):
                //   uri          = which table to query
                //   projection   = which columns to return (null = all columns)
                //   selection    = WHERE clause (null = no filter)
                //   selectionArgs= values for ? in the WHERE clause (null here)
                //   sortOrder    = ORDER BY clause
                //
                // "date DESC" = newest messages first
                // "LIMIT 100" limits to most recent 100 messages for performance
                // ---------------------------------------------------------------
                Cursor cursor = requireActivity().getContentResolver().query(
                        Uri.parse("content://sms/inbox"),
                        null,              // all columns
                        null,              // no WHERE filter
                        null,              // no WHERE arguments
                        "date DESC LIMIT 100" // newest first, max 100
                );

                if (cursor != null) {

                    // moveToNext() advances the cursor to the next row.
                    // Returns false when there are no more rows.
                    // This is the standard cursor iteration pattern.
                    while (cursor.moveToNext()) {

                        // getColumnIndexOrThrow(columnName):
                        //   Returns the column number (0, 1, 2...) for the
                        //   named column. Throws an exception if column doesn't
                        //   exist — better than silently returning -1.
                        //
                        // getString(columnIndex):
                        //   Returns the value at that column as a String.
                        //
                        // getLong(columnIndex):
                        //   Returns the value as a long (for the timestamp).

                        String address = cursor.getString(
                                cursor.getColumnIndexOrThrow("address"));
                        String body = cursor.getString(
                                cursor.getColumnIndexOrThrow("body"));
                        long date = cursor.getLong(
                                cursor.getColumnIndexOrThrow("date"));

                        // Skip empty messages
                        if (body == null || body.trim().isEmpty()) continue;

                        // Analyze this message for phishing
                        SmsMessage sms = PhishingAnalyzer.analyzeText(address, body, date);
                        analyzedMessages.add(sms);
                    }

                    // CRITICAL: Always close the cursor to release database resources.
                    // Unclosed cursors cause memory leaks and "Cursor window" errors.
                    cursor.close();
                }

            } catch (SecurityException e) {
                // READ_SMS permission not granted
                postToMainThread(() -> {
                    tvScanStatus.setText("Permission denied — grant SMS permission in settings");
                    tvScanStatus.setTextColor(Color.parseColor("#C0392B"));
                    resetScanButton();
                });
                return;
            } catch (Exception e) {
                postToMainThread(() -> {
                    tvScanStatus.setText("Error reading inbox: " + e.getMessage());
                    tvScanStatus.setTextColor(Color.parseColor("#C0392B"));
                    resetScanButton();
                });
                return;
            }

            // ---------------------------------------------------------------
            // POST RESULTS TO MAIN THREAD
            //
            // We're on a background thread — can't touch UI directly.
            // requireActivity().runOnUiThread() queues the lambda to run
            // on the main thread.
            //
            // final keyword on analyzedMessages: lambdas in Java can only
            // capture variables that are "effectively final" (never reassigned).
            // We declare finalMessages to hold the reference for the lambda.
            // ---------------------------------------------------------------
            final List<SmsMessage> finalMessages = analyzedMessages;

            postToMainThread(() -> {
                adapter.setMessages(finalMessages);
                updateCounters();

                int total    = adapter.getMessageCount();
                int phishing = adapter.getPhishingCount();

                if (phishing > 0) {
                    tvScanStatus.setText(phishing + " suspicious message"
                            + (phishing > 1 ? "s" : "") + " found!");
                    tvScanStatus.setTextColor(Color.parseColor("#E74C3C"));
                } else {
                    tvScanStatus.setText("Inbox scanned — no threats detected");
                    tvScanStatus.setTextColor(Color.parseColor("#27AE60"));
                }

                progressScan.setVisibility(View.GONE);
                resetScanButton();
            });

        }).start();
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /**
     * postToMainThread()
     *
     * Helper to run a Runnable on the main (UI) thread.
     * Wraps requireActivity().runOnUiThread() for brevity.
     *
     * Runnable = a functional interface with one method: run()
     * () -> { ... } is a lambda that implements Runnable.
     */
    private void postToMainThread(Runnable action) {
        if (getActivity() != null) {
            requireActivity().runOnUiThread(action);
        }
    }

    /**
     * updateCounters()
     * Updates the "X total" and "X suspicious" counter TextViews.
     */
    private void updateCounters() {
        int total    = adapter.getMessageCount();
        int phishing = adapter.getPhishingCount();
        tvTotalCount.setText(total + " messages");
        tvPhishingCount.setText(phishing + " suspicious");
        tvPhishingCount.setTextColor(phishing > 0
                ? Color.parseColor("#E74C3C")
                : Color.parseColor("#27AE60"));
    }

    /** Resets the scan button back to its default state */
    private void resetScanButton() {
        btnScanInbox.setText("Scan Inbox");
        btnScanInbox.setEnabled(true);
    }
}
