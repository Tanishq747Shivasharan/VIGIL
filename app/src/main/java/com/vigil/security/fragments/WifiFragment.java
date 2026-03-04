package com.vigil.security.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.vigil.security.R;
import com.vigil.security.security.WifiSecurityAnalyzer;

public class WifiFragment extends Fragment {

    private TextView tvSSID, tvSecurity, tvRisk, tvEncryption, tvSignal, tvFrequency, tvRiskBadge, tvOpenBadge, tvWarningDesc;
    private ProgressBar progressRisk;
    private CardView cardWarning;
    private Button btnScan;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wifi, container, false);

        // Initialize all views from the attractive fragment_wifi.xml
        tvSSID = view.findViewById(R.id.tv_ssid);
        tvSecurity = view.findViewById(R.id.tv_security);
        tvRisk = view.findViewById(R.id.tv_risk);
        tvEncryption = view.findViewById(R.id.tv_encryption);
        tvSignal = view.findViewById(R.id.tv_signal);
        tvFrequency = view.findViewById(R.id.tv_frequency);
        tvRiskBadge = view.findViewById(R.id.tv_risk_badge);
        tvOpenBadge = view.findViewById(R.id.tv_open_badge);
        tvWarningDesc = view.findViewById(R.id.tv_warning_desc);
        progressRisk = view.findViewById(R.id.progress_risk);
        cardWarning = view.findViewById(R.id.card_warning);
        btnScan = view.findViewById(R.id.btn_scan_wifi);

        btnScan.setOnClickListener(v -> performWifiScan());

        // Perform an initial scan to populate data immediately
        performWifiScan();

        return view;
    }

    private void performWifiScan() {
        WifiSecurityAnalyzer analyzer = new WifiSecurityAnalyzer(requireContext());

        // Get all dynamic values from analyzer
        String ssid = analyzer.getCurrentSSID();
        String security = analyzer.getSecurityType();
        String riskLevel = analyzer.getRiskLevel(security);
        int riskScore = analyzer.getRiskScore(riskLevel);
        String encryption = analyzer.getEncryption(security);
        String signal = analyzer.getSignalStrength();
        String frequency = analyzer.getFrequency();

        // 1. Update SSID (Removing "SSID: " prefix as requested)
        tvSSID.setText(ssid);

        // 2. Update all middle and bottom card values
        tvSecurity.setText(security);
        tvEncryption.setText(encryption);
        tvSignal.setText(signal);
        tvFrequency.setText(frequency);

        // 3. Update Risk Level Text and Progress Bar
        tvRisk.setText(riskLevel + " — " + riskScore + "%");
        progressRisk.setProgress(riskScore);

        // 4. Update UI context (colors, badges, warnings)
        updateRiskUI(riskLevel, security);
    }

    private void updateRiskUI(String riskLevel, String security) {
        int color;
        String badgeText;

        if ("HIGH".equals(riskLevel)) {
            color = Color.parseColor("#C0392B"); // Strong Red
            badgeText = "⚠  High Risk";
            cardWarning.setVisibility(View.VISIBLE);
            tvWarningDesc.setText("This network has no password protection or uses weak encryption. Avoid sensitive transactions.");
        } else if ("MEDIUM".equals(riskLevel)) {
            color = Color.parseColor("#F39C12"); // Warning Orange
            badgeText = "⚠  Medium Risk";
            cardWarning.setVisibility(View.VISIBLE);
            tvWarningDesc.setText("This network uses older security protocols. It is safer than an open network but still vulnerable.");
        } else {
            color = Color.parseColor("#27AE60"); // Safe Green
            badgeText = "✓  Safe";
            cardWarning.setVisibility(View.GONE);
        }

        // Apply color to risk text and badge
        tvRisk.setTextColor(color);
        tvRiskBadge.setText(badgeText);
        
        // Handle the "Open Network" badge visibility
        if ("OPEN".equals(security)) {
            tvOpenBadge.setVisibility(View.VISIBLE);
            tvSecurity.setTextColor(Color.parseColor("#C0392B"));
        } else {
            tvOpenBadge.setVisibility(View.GONE);
            tvSecurity.setTextColor(Color.parseColor("#543310"));
        }
    }
}