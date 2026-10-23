package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.FarmExpense;
import com.agrosmart.backend.repositories.FarmExpenseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceController {

    private final FarmExpenseRepository expenseRepository;

    public FinanceController(FarmExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping("/expenses")
    public ResponseEntity<?> getFarmerFinances(@RequestParam(required = false) String phone) {
        List<FarmExpense> entries;
        if (phone != null && !phone.trim().isEmpty()) {
            entries = expenseRepository.findByFarmerPhoneOrderByEntryDateDesc(phone);
        } else {
            entries = expenseRepository.findAll();
        }

        double totalExpenses = 0.0;
        double totalRevenue = 0.0;

        for (FarmExpense e : entries) {
            if ("REVENUE".equalsIgnoreCase(e.getEntryType())) {
                totalRevenue += (e.getAmount() != null ? e.getAmount() : 0.0);
            } else {
                totalExpenses += (e.getAmount() != null ? e.getAmount() : 0.0);
            }
        }

        double netProfit = totalRevenue - totalExpenses;

        Map<String, Object> response = new HashMap<>();
        response.put("entries", entries);
        response.put("totalExpenses", totalExpenses);
        response.put("totalRevenue", totalRevenue);
        response.put("netProfit", netProfit);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/expenses")
    public ResponseEntity<FarmExpense> saveExpense(@RequestBody FarmExpense expense) {
        if (expense.getId() == null || expense.getId().trim().isEmpty()) {
            expense.setId("EXP-" + System.currentTimeMillis());
        }
        if (expense.getEntryDate() == null || expense.getEntryDate().trim().isEmpty()) {
            expense.setEntryDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        }
        if (expense.getEntryType() == null || expense.getEntryType().trim().isEmpty()) {
            expense.setEntryType("EXPENSE");
        }

        FarmExpense saved = expenseRepository.save(expense);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable String id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Record deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
