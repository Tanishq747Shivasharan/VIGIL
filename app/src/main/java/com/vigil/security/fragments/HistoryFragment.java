package com.vigil.security.fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vigil.security.R;
import com.vigil.security.adapters.HistoryAdapter;
import com.vigil.security.database.ScanHistoryDao;
import com.vigil.security.database.VigilDatabase;
import com.vigil.security.models.ScanRecord;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemActionListener {

    private RecyclerView   recyclerHistory;
    private HistoryAdapter adapter;
    private TextView       tvEmptyState, tvTotalCount;
    private Spinner        spinnerFilter;
    private ScanHistoryDao dao;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        dao = new ScanHistoryDao(VigilDatabase.getInstance(requireContext()));

        bindViews(view);
        setupRecyclerView();
        setupFilter();
        setupSwipeToDelete();
        loadHistory(null);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }

    private void bindViews(View view) {
        recyclerHistory = view.findViewById(R.id.recycler_history);
        tvEmptyState    = view.findViewById(R.id.tv_history_empty);
        tvTotalCount    = view.findViewById(R.id.tv_history_count);
        spinnerFilter   = view.findViewById(R.id.spinner_filter);

        view.findViewById(R.id.btn_clear_history)
                .setOnClickListener(v -> confirmClearAll());
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(this);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerHistory.setAdapter(adapter);
        recyclerHistory.setHasFixedSize(false);
    }

    private void setupFilter() {
        String[] filterOptions = {"All Scans", "WiFi", "LAN", "SMS", "Password"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = {null,
                        ScanRecord.TYPE_WIFI,
                        ScanRecord.TYPE_LAN,
                        ScanRecord.TYPE_SMS,
                        ScanRecord.TYPE_PASSWORD};
                loadHistory(types[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSwipeToDelete() {
        Paint deletePaint = new Paint();
        deletePaint.setColor(Color.parseColor("#C0392B"));

        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView rv,
                                          @NonNull RecyclerView.ViewHolder vh,
                                          @NonNull RecyclerView.ViewHolder target) { return false; }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        ScanRecord toDelete = adapter.getAt(position);
                        adapter.removeAt(position);
                        onItemDelete(toDelete, position);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        View itemView = viewHolder.itemView;
                        if (dX < 0) {
                            c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                                    itemView.getRight(), itemView.getBottom(), deletePaint);
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerHistory);
    }

    private void loadHistory(@Nullable String scanType) {
        executor.execute(() -> {
            List<ScanRecord> records = (scanType == null)
                    ? dao.getAll()
                    : dao.getByType(scanType);
            int total = dao.getTotalCount();

            requireActivity().runOnUiThread(() -> {
                adapter.setRecords(records);
                if (records.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerHistory.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerHistory.setVisibility(View.VISIBLE);
                }
                tvTotalCount.setText(total + " total scans recorded");
            });
        });
    }

    @Override
    public void onItemClick(ScanRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle(record.getScanTypeLabel() + " — " + record.getFormattedDate())
                .setMessage(
                        "Risk Level: " + record.getRiskLabel() + "\n" +
                                "Score: "      + record.getRiskScore() + "/100\n\n" +
                                record.getSummary() + "\n\n" +
                                "Details:\n"  + (record.getDetails() != null ? record.getDetails() : "—"))
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onItemDelete(ScanRecord record, int position) {
        executor.execute(() -> {
            dao.deleteById(record.getId());
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Record deleted", Toast.LENGTH_SHORT).show());
        });
    }

    private void confirmClearAll() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All History")
                .setMessage("This will permanently delete all scan records. This cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    executor.execute(() -> {
                        dao.deleteAll();
                        requireActivity().runOnUiThread(() -> {
                            loadHistory(null);
                            Toast.makeText(requireContext(),
                                    "History cleared", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
