package com.vigil.security.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.vigil.security.R;
import com.vigil.security.models.PasswordStrengthResult;
import com.vigil.security.security.PasswordAnalyzer;

public class PasswordFragment extends Fragment {
    private EditText etPassword;           // The text input field
    private ImageButton btnTogglePassword; // Eye icon to show/hide password
    private ProgressBar progressStrength;  // The strength bar (0–100)
    private TextView tvStrengthLabel;      // "Weak" / "Strong" etc.
    private TextView tvFeedbackMessage;    // "Good job! This is a secure password."
    private LinearLayout layoutTips;       // Container for improvement tips
    private TextView tvTipsHeader;         // "Improvement Tips:" header label

    private boolean isPasswordVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password, container, false);

        etPassword         = view.findViewById(R.id.et_password);
        btnTogglePassword  = view.findViewById(R.id.btn_toggle_password);
        progressStrength   = view.findViewById(R.id.progress_strength);
        tvStrengthLabel    = view.findViewById(R.id.tv_strength_label);
        tvFeedbackMessage  = view.findViewById(R.id.tv_feedback_message);
        layoutTips         = view.findViewById(R.id.layout_tips);
        tvTipsHeader       = view.findViewById(R.id.tv_tips_header);

        setupPasswordToggle();

        setupTextWatcher();

        updateUI(PasswordAnalyzer.analyze(""));

        return view;
    }

    private void setupPasswordToggle() {
        btnTogglePassword.setOnClickListener(v -> {

            int cursorPosition = etPassword.getSelectionEnd();

            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                btnTogglePassword.setImageResource(android.R.drawable.ic_secure);
            }
            etPassword.setSelection(Math.min(cursorPosition, etPassword.getText().length()));
        });
    }

    private void setupTextWatcher() {
        etPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentPassword = s.toString();

                PasswordStrengthResult result = PasswordAnalyzer.analyze(currentPassword);

                updateUI(result);
            }
        });
    }

    private void updateUI(PasswordStrengthResult result) {

        progressStrength.setProgress(result.getScore());

        tvStrengthLabel.setText(result.getLabel());

        int color = Color.parseColor(result.getColorHex());
        tvStrengthLabel.setTextColor(color);

        progressStrength.getProgressDrawable()
                .setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

        tvFeedbackMessage.setText(result.getFeedbackMessage());

        layoutTips.removeAllViews();

        if (result.hasTips()) {
            tvTipsHeader.setVisibility(View.VISIBLE);
            layoutTips.setVisibility(View.VISIBLE);

            for (String tip : result.getTips()) {
                TextView tipView = new TextView(requireContext());
                tipView.setText("• " + tip);
                tipView.setTextColor(Color.parseColor("#8B6914")); // Brown text (app theme)
                tipView.setTextSize(13);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 6, 0, 0); // 6dp top margin between tips
                tipView.setLayoutParams(params);

                layoutTips.addView(tipView);
            }
        } else {
            tvTipsHeader.setVisibility(View.GONE);
            layoutTips.setVisibility(View.GONE);
        }
    }
}