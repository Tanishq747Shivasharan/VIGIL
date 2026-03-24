package com.vigil.security.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vigil.security.security.PhishingAnalyzer;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    public static final String ACTION_NEW_SMS    = "com.vigil.security.NEW_SMS";
    public static final String EXTRA_SENDER      = "extra_sender";
    public static final String EXTRA_BODY        = "extra_body";
    public static final String EXTRA_TIMESTAMP   = "extra_timestamp";
    public static final String EXTRA_RISK_SCORE  = "extra_risk_score";
    public static final String EXTRA_RISK_LABEL  = "extra_risk_label";
    public static final String EXTRA_COLOR_HEX   = "extra_color_hex";
    public static final String EXTRA_EXPLANATION = "extra_explanation";
    public static final String EXTRA_IS_PHISHING = "extra_is_phishing";

    // The system action Android broadcasts when SMS is received
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Safety check: only handle SMS_RECEIVED broadcasts
        if (!SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        try {
            // Get the "extras" bundle from the intent
            // Bundle = a key-value container Android uses to pass data around
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;

            // Extract the array of PDUs from the bundle
            // "pdus" is the standard key Android uses for SMS data
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null || pdus.length == 0) return;

            // Get the SMS format (needed for createFromPdu on Android 6+)
            // On older devices, format may be null — we handle that below
            String format = bundle.getString("format");

            StringBuilder fullBody = new StringBuilder();
            String sender = null;
            long timestamp = System.currentTimeMillis(); // fallback

            for (Object pdu : pdus) {
                // Cast the Object to byte[] — PDUs are raw byte arrays
                android.telephony.SmsMessage smsMessage;

                // format can be null on older API levels — handle both cases
                if (format != null) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    //noinspection deprecation — deprecated but needed for older APIs
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                }

                if (smsMessage == null) continue;

                // Get sender from first PDU (same for all parts)
                if (sender == null) {
                    sender = smsMessage.getDisplayOriginatingAddress();
                    timestamp = smsMessage.getTimestampMillis();
                }

                // Append this PDU's text to the full body
                // getDisplayMessageBody() returns the text portion of this PDU
                fullBody.append(smsMessage.getDisplayMessageBody());
            }

            // Safety: if we couldn't extract sender or body, stop here
            if (sender == null || fullBody.length() == 0) return;

            String bodyText = fullBody.toString();

            Log.d(TAG, "SMS received from: " + sender);
            Log.d(TAG, "SMS body: " + bodyText.substring(0, Math.min(50, bodyText.length())));

            com.vigil.security.models.SmsMessage analyzed =
                    PhishingAnalyzer.analyzeText(sender, bodyText, timestamp);

            Intent localIntent = new Intent(ACTION_NEW_SMS);
            localIntent.putExtra(EXTRA_SENDER,      analyzed.getSender());
            localIntent.putExtra(EXTRA_BODY,        analyzed.getBody());
            localIntent.putExtra(EXTRA_TIMESTAMP,   analyzed.getTimestamp());
            localIntent.putExtra(EXTRA_RISK_SCORE,  analyzed.getRiskScore());
            localIntent.putExtra(EXTRA_RISK_LABEL,  analyzed.getRiskLabel());
            localIntent.putExtra(EXTRA_COLOR_HEX,   analyzed.getColorHex());
            localIntent.putExtra(EXTRA_EXPLANATION, analyzed.getExplanation());
            localIntent.putExtra(EXTRA_IS_PHISHING, analyzed.isPhishing());

            // Send the local broadcast
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

            Log.d(TAG, "Analyzed SMS — Risk: " + analyzed.getRiskLabel()
                    + " Score: " + analyzed.getRiskScore());

        } catch (Exception e) {
            Log.e(TAG, "Error processing SMS: " + e.getMessage());
        }
    }

}
