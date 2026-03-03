package com.vigil.security.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.vigil.security.R;
import com.vigil.security.security.WifiSecurityAnalyzer;

public class WifiFragment extends Fragment {

    private TextView tvSSID, tvSecurity, tvRisk;
    private Button btnScan;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wifi, container, false);

        tvSSID = view.findViewById(R.id.tv_ssid);
        tvSecurity = view.findViewById(R.id.tv_security);
        tvRisk = view.findViewById(R.id.tv_risk);
        btnScan = view.findViewById(R.id.btn_scan_wifi);

        btnScan.setOnClickListener(v -> performWifiScan());

        return view;
    }

    private void performWifiScan() {

        WifiSecurityAnalyzer analyzer =
                new WifiSecurityAnalyzer(requireContext());

        String ssid = analyzer.getCurrentSSID();
        String security = analyzer.getSecurityType();
        String risk = analyzer.getRiskLevel(security);

        tvSSID.setText("SSID: " + ssid);
        tvSecurity.setText("Security Type: " + security);
        tvRisk.setText("Risk Level: " + risk);
    }
}