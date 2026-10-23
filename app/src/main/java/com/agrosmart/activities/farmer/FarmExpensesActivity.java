package com.agrosmart.activities.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.FarmExpenseAdapter;
import com.agrosmart.databinding.ActivityFarmExpensesBinding;
import com.agrosmart.models.FarmExpense;
import com.agrosmart.repository.FinanceRepository;
import com.agrosmart.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FarmExpensesActivity extends BaseActivity {

    private ActivityFarmExpensesBinding binding;
    private FinanceRepository repository;
    private SessionManager sessionManager;
    private FarmExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = FinanceRepository.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new FarmExpenseAdapter(item -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Entry")
                    .setMessage("Delete " + item.getTitle() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        repository.deleteExpense(item.getId(), this::loadFinances);
                        Toast.makeText(this, "Entry removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExpenses.setAdapter(adapter);

        binding.btnAddExpense.setOnClickListener(v -> showAddEntryDialog("EXPENSE"));
        binding.btnRecordSale.setOnClickListener(v -> showAddEntryDialog("REVENUE"));

        loadFinances();
    }

    private void loadFinances() {
        String phone = sessionManager.getUserPhone();
        repository.getFinances(phone, new FinanceRepository.FinanceCallback() {
            @Override
            public void onLoaded(List<FarmExpense> entries, double totalExpenses, double totalRevenue, double netProfit) {
                adapter.setItems(entries);
                binding.tvTotalExpenses.setText(String.format(Locale.getDefault(), "₹ %,.2f", totalExpenses));
                binding.tvTotalRevenue.setText(String.format(Locale.getDefault(), "₹ %,.2f", totalRevenue));
                binding.tvNetProfit.setText(String.format(Locale.getDefault(), "₹ %,.2f", netProfit));

                if (netProfit >= 0) {
                    binding.tvNetProfit.setTextColor(0xFF15803D); // Green
                    binding.tvNetProfitLabel.setText("Net Farm Profit (Surplus) ✅");
                } else {
                    binding.tvNetProfit.setTextColor(0xFFDC2626); // Red
                    binding.tvNetProfitLabel.setText("Net Farm Deficit (Pending Harvest) ⚠️");
                }

                if (entries == null || entries.isEmpty()) {
                    binding.tvEmptyExpenses.setVisibility(View.VISIBLE);
                    binding.rvExpenses.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyExpenses.setVisibility(View.GONE);
                    binding.rvExpenses.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.tvEmptyExpenses.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAddEntryDialog(String type) {
        boolean isExpense = "EXPENSE".equalsIgnoreCase(type);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isExpense ? "Add Farm Expense" : "Record Crop Harvest Sale");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null);
        EditText etTitle = dialogView.findViewById(R.id.etExpenseTitle);
        EditText etAmount = dialogView.findViewById(R.id.etExpenseAmount);
        EditText etNotes = dialogView.findViewById(R.id.etExpenseNotes);
        Spinner spnCategory = dialogView.findViewById(R.id.spnCategory);

        etTitle.setHint(isExpense ? "e.g. Sowing Labor, Urea 5 Bags" : "e.g. 50 Quintals Soybean Sold");
        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();
            String category = spnCategory.getSelectedItem() != null ? spnCategory.getSelectedItem().toString() : "General";

            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Title and amount are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            FarmExpense entry = new FarmExpense();
            entry.setId("EXP-" + System.currentTimeMillis());
            entry.setFarmerPhone(sessionManager.getUserPhone());
            entry.setEntryType(type);
            entry.setCategory(category);
            entry.setTitle(title);
            entry.setAmount(amount);
            entry.setEntryDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            entry.setCropSeason("Kharif 2026");
            entry.setNotes(notes);

            repository.saveExpense(entry, new FinanceRepository.SingleExpenseCallback() {
                @Override
                public void onSuccess(FarmExpense expense) {
                    Toast.makeText(FarmExpensesActivity.this, "Entry recorded in ledger", Toast.LENGTH_SHORT).show();
                    loadFinances();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(FarmExpensesActivity.this, "Error saving: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
