package com.vigil.security.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.vigil.security.R;
import com.vigil.security.models.GeneratorConfig;
import com.vigil.security.models.PasswordStrengthResult;
import com.vigil.security.security.PasswordAnalyzer;
import com.vigil.security.security.PasswordGenerator;

public class PasswordFragment extends Fragment {
    private EditText etPassword;
    private ImageButton btnTogglePassword;
    private ProgressBar progressStrength;
    private TextView tvStrengthLabel;
    private TextView tvFeedbackMessage;
    private LinearLayout layoutTips;
    private TextView tvTipsHeader;

    private TextView tvGeneratedPassword;  // Displays the generated password
    private ImageButton btnCopyPassword;   // Copy to clipboard button
    private SeekBar seekBarLength;         // The length slider (6–32)
    private TextView tvLengthValue;        // The "12" number label next to slider
    private CheckBox cbUppercase;          // "Uppercase" checkbox
    private CheckBox cbLowercase;          // "Lowercase" checkbox
    private CheckBox cbNumbers;            // "Numbers" checkbox
    private CheckBox cbSymbols;            // "Symbols" checkbox
    private Button btnGenerate;            // "Generate New Password" button
    private TextView tvResetDefaults;      // "Reset defaults" text button

    private boolean isPasswordVisible = false;

    private GeneratorConfig config = new GeneratorConfig();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_password, container, false);

        etPassword        = view.findViewById(R.id.et_password);
        btnTogglePassword = view.findViewById(R.id.btn_toggle_password);
        progressStrength  = view.findViewById(R.id.progress_strength);
        tvStrengthLabel   = view.findViewById(R.id.tv_strength_label);
        tvFeedbackMessage = view.findViewById(R.id.tv_feedback_message);
        layoutTips        = view.findViewById(R.id.layout_tips);
        tvTipsHeader      = view.findViewById(R.id.tv_tips_header);

        tvGeneratedPassword = view.findViewById(R.id.tv_generated_password);
        btnCopyPassword     = view.findViewById(R.id.btn_copy_password);
        seekBarLength       = view.findViewById(R.id.seekbar_length);
        tvLengthValue       = view.findViewById(R.id.tv_length_value);
        cbUppercase         = view.findViewById(R.id.cb_uppercase);
        cbLowercase         = view.findViewById(R.id.cb_lowercase);
        cbNumbers           = view.findViewById(R.id.cb_numbers);
        cbSymbols           = view.findViewById(R.id.cb_symbols);
        btnGenerate         = view.findViewById(R.id.btn_generate);
        tvResetDefaults     = view.findViewById(R.id.tv_reset_defaults);

        setupStrengthChecker();
        setupGenerator();

        return view;
    }

    private void setupStrengthChecker() {
        setupPasswordToggle();
        setupTextWatcher();
        updateStrengthUI(PasswordAnalyzer.analyze(""));
    }

    private void setupPasswordToggle() {
        btnTogglePassword.setOnClickListener(v -> {
            int cursorPosition = etPassword.getSelectionEnd();
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(android.R.drawable.ic_secure);
            }
            etPassword.setSelection(Math.min(cursorPosition, etPassword.getText().length()));
        });
    }

    private void setupTextWatcher() {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                PasswordStrengthResult result = PasswordAnalyzer.analyze(s.toString());
                updateStrengthUI(result);
            }
        });
    }

    private void updateStrengthUI(PasswordStrengthResult result) {
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
                tipView.setTextColor(Color.parseColor("#8B6914"));
                tipView.setTextSize(13);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 0);
                tipView.setLayoutParams(params);
                layoutTips.addView(tipView);
            }
        } else {
            tvTipsHeader.setVisibility(View.GONE);
            layoutTips.setVisibility(View.GONE);
        }
    }

    private void setupGenerator() {
        setupSeekBar();
        setupCheckBoxes();
        setupGenerateButton();
        setupCopyButton();
        setupResetDefaults();
        syncCheckBoxesToConfig();
        generateAndDisplay(); // Show a password immediately when screen opens
    }

    private void setupSeekBar() {
        int seekBarMax = GeneratorConfig.MAX_LENGTH - GeneratorConfig.MIN_LENGTH; // 26
        seekBarLength.setMax(seekBarMax);

        int initialProgress = GeneratorConfig.DEFAULT_LENGTH - GeneratorConfig.MIN_LENGTH; // 6
        seekBarLength.setProgress(initialProgress);
        tvLengthValue.setText(String.valueOf(GeneratorConfig.DEFAULT_LENGTH));

        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert 0-based progress back to actual length
                int actualLength = progress + GeneratorConfig.MIN_LENGTH;

                // Update the number label (the "12" in your mockup)
                tvLengthValue.setText(String.valueOf(actualLength));

                // Keep the config in sync
                config.setLength(actualLength);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupCheckBoxes() {
        cbUppercase.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.setUseUppercase(isChecked);
            guardAtLeastOneChecked(cbUppercase, isChecked);
        });
        cbLowercase.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.setUseLowercase(isChecked);
            guardAtLeastOneChecked(cbLowercase, isChecked);
        });
        cbNumbers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.setUseNumbers(isChecked);
            guardAtLeastOneChecked(cbNumbers, isChecked);
        });
        cbSymbols.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.setUseSymbols(isChecked);
            guardAtLeastOneChecked(cbSymbols, isChecked);
        });
    }

    private void guardAtLeastOneChecked(CheckBox checkBox, boolean isChecked) {
        if (!isChecked && !config.isValid()) {
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(true);
            resyncConfigForCheckBox(checkBox);
            setupCheckBoxes();

            Toast.makeText(requireContext(),
                    "At least one character type must be selected",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void resyncConfigForCheckBox(CheckBox checkBox) {
        if      (checkBox == cbUppercase) config.setUseUppercase(true);
        else if (checkBox == cbLowercase) config.setUseLowercase(true);
        else if (checkBox == cbNumbers)   config.setUseNumbers(true);
        else if (checkBox == cbSymbols)   config.setUseSymbols(true);
    }

    private void setupGenerateButton() {
        btnGenerate.setOnClickListener(v -> generateAndDisplay());
    }

    private void setupCopyButton() {
        btnCopyPassword.setOnClickListener(v -> {
            String passwordText = tvGeneratedPassword.getText().toString();

            if (passwordText.isEmpty() || passwordText.equals("Select at least one option")) {
                Toast.makeText(requireContext(), "Nothing to copy", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get system clipboard service and cast it to ClipboardManager
            ClipboardManager clipboard = (ClipboardManager)
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

            // Wrap text in a ClipData object ("label" is metadata, not shown to user)
            ClipData clip = ClipData.newPlainText("Generated Password", passwordText);

            // Set it as the active clipboard content
            clipboard.setPrimaryClip(clip);

            Toast.makeText(requireContext(), "Password copied!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupResetDefaults() {
        tvResetDefaults.setOnClickListener(v -> {
            config = new GeneratorConfig(); // brand new default config

            syncCheckBoxesToConfig();

            // Reset slider position: DEFAULT_LENGTH(12) - MIN_LENGTH(6) = 6
            seekBarLength.setProgress(
                    GeneratorConfig.DEFAULT_LENGTH - GeneratorConfig.MIN_LENGTH);
            tvLengthValue.setText(String.valueOf(GeneratorConfig.DEFAULT_LENGTH));

            generateAndDisplay();

            Toast.makeText(requireContext(), "Reset to defaults", Toast.LENGTH_SHORT).show();
        });
    }

    private void syncCheckBoxesToConfig() {
        // Detach all listeners first
        cbUppercase.setOnCheckedChangeListener(null);
        cbLowercase.setOnCheckedChangeListener(null);
        cbNumbers.setOnCheckedChangeListener(null);
        cbSymbols.setOnCheckedChangeListener(null);

        // Apply config values to checkboxes
        cbUppercase.setChecked(config.useUppercase());
        cbLowercase.setChecked(config.useLowercase());
        cbNumbers.setChecked(config.useNumbers());
        cbSymbols.setChecked(config.useSymbols());

        // Re-attach listeners
        setupCheckBoxes();
    }

    private void generateAndDisplay() {
        String password = PasswordGenerator.generate(config);
        tvGeneratedPassword.setText(password);
    }
}