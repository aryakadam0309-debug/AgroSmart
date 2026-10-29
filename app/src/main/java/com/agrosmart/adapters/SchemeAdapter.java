package com.agrosmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.activities.farmer.SchemeDetailActivity;
import com.agrosmart.databinding.ItemSchemeBinding;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.SchemeEligibilityResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {

    private List<GovernmentScheme> schemes = new ArrayList<>();
    private final Map<String, SchemeEligibilityResult> eligibilityMap = new HashMap<>();
    private final Map<String, Boolean> savedMap = new HashMap<>();
    private final Context context;
    private OnSchemeBookmarkListener bookmarkListener;

    public interface OnSchemeBookmarkListener {
        void onBookmarkToggle(GovernmentScheme scheme, boolean isSaved);
    }

    public SchemeAdapter(Context context) {
        this.context = context;
    }

    public void setOnSchemeBookmarkListener(OnSchemeBookmarkListener listener) {
        this.bookmarkListener = listener;
    }

    public void setSchemes(List<GovernmentScheme> list) {
        this.schemes = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setEligibilityResults(List<SchemeEligibilityResult> results) {
        this.schemes.clear();
        this.eligibilityMap.clear();
        if (results != null) {
            for (SchemeEligibilityResult r : results) {
                if (r.getScheme() != null) {
                    this.schemes.add(r.getScheme());
                    this.eligibilityMap.put(r.getScheme().getSchemeId(), r);
                    this.savedMap.put(r.getScheme().getSchemeId(), r.isSaved());
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setSavedState(String schemeId, boolean saved) {
        savedMap.put(schemeId, saved);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSchemeBinding binding = ItemSchemeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SchemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder holder, int position) {
        holder.bind(schemes.get(position));
    }

    @Override
    public int getItemCount() {
        return schemes.size();
    }

    class SchemeViewHolder extends RecyclerView.ViewHolder {
        private final ItemSchemeBinding binding;

        public SchemeViewHolder(ItemSchemeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GovernmentScheme item) {
            if (item == null) return;

            binding.tvSchemeName.setText(item.getSchemeName());
            binding.tvSchemeDesc.setText(item.getDescription());
            binding.tvSchemeBenefits.setText("🎁 " + item.getBenefits());
            binding.tvSchemeCategory.setText(item.getCategory());
            binding.tvSchemeLevel.setText(item.getLevel() != null ? item.getLevel().toUpperCase() : "CENTRAL");

            String deadlineText = "🗓️ " + item.getDeadline();
            if (item.getLastVerifiedDate() != null) {
                deadlineText += " • Verified: " + item.getLastVerifiedDate();
            }
            binding.tvSchemeDeadline.setText(deadlineText);

            // Eligibility Badge Check
            SchemeEligibilityResult res = eligibilityMap.get(item.getSchemeId());
            if (res != null) {
                binding.layoutEligibilityBadge.setVisibility(View.VISIBLE);
                String status = res.getEligibilityStatus();
                binding.tvEligibilityBadge.setText(status);

                if ("Likely Eligible".equalsIgnoreCase(status)) {
                    binding.tvEligibilityBadge.setText("✅ Likely Eligible");
                    binding.tvEligibilityBadge.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    binding.tvEligibilityBadge.setTextColor(Color.parseColor("#2E7D32"));
                } else if ("Possibly Eligible – More Information Required".equalsIgnoreCase(status)) {
                    binding.tvEligibilityBadge.setText("⚠️ More Info Required");
                    binding.tvEligibilityBadge.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    binding.tvEligibilityBadge.setTextColor(Color.parseColor("#E65100"));
                } else if ("Not Eligible".equalsIgnoreCase(status)) {
                    binding.tvEligibilityBadge.setText("❌ Not Eligible");
                    binding.tvEligibilityBadge.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    binding.tvEligibilityBadge.setTextColor(Color.parseColor("#C62828"));
                } else if ("Application Closed".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(item.getApplicationStatus())) {
                    binding.tvEligibilityBadge.setText("🔒 Application Closed");
                    binding.tvEligibilityBadge.setBackgroundColor(Color.parseColor("#ECEFF1"));
                    binding.tvEligibilityBadge.setTextColor(Color.parseColor("#455A64"));
                } else {
                    binding.tvEligibilityBadge.setText("❓ Status Unverified");
                    binding.tvEligibilityBadge.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    binding.tvEligibilityBadge.setTextColor(Color.parseColor("#757575"));
                }
            } else if ("CLOSED".equalsIgnoreCase(item.getApplicationStatus())) {
                binding.layoutEligibilityBadge.setVisibility(View.VISIBLE);
                binding.tvEligibilityBadge.setText("🔒 Application Closed");
                binding.tvEligibilityBadge.setBackgroundColor(Color.parseColor("#ECEFF1"));
                binding.tvEligibilityBadge.setTextColor(Color.parseColor("#455A64"));
            } else {
                binding.layoutEligibilityBadge.setVisibility(View.GONE);
            }

            // Bookmark Saved State
            boolean isSaved = Boolean.TRUE.equals(savedMap.get(item.getSchemeId()));
            binding.btnBookmark.setColorFilter(isSaved ? Color.parseColor("#D32F2F") : Color.parseColor("#9E9E9E"));

            binding.btnBookmark.setOnClickListener(v -> {
                boolean newState = !isSaved;
                savedMap.put(item.getSchemeId(), newState);
                notifyItemChanged(getAdapterPosition());
                if (bookmarkListener != null) {
                    bookmarkListener.onBookmarkToggle(item, newState);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(context, SchemeDetailActivity.class);
                intent.putExtra("extra_scheme_item", item);
                if (res != null) {
                    intent.putExtra("extra_eligibility_result", res);
                }
                context.startActivity(intent);
            });
        }
    }
}
