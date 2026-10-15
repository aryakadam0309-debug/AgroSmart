package com.agrosmart.activities.admin;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.adapters.NewsAdapter;
import com.agrosmart.databinding.ActivityManageNewsBinding;
import com.agrosmart.models.NewsItem;
import com.agrosmart.repository.AgriNewsRepository;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageNewsActivity extends AppCompatActivity {

    private ActivityManageNewsBinding binding;
    private NewsAdapter adapter;
    private AgriNewsRepository repository;

    private Uri selectedNewsImageUri = null;
    private ImageView currentDialogImagePreview = null;
    private View currentDialogPlaceholder = null;
    private View currentDialogRemoveButton = null;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AgriNewsRepository.getInstance(this);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        long size = 0;
                        try (AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(uri, "r")) {
                            if (afd != null) {
                                size = afd.getLength();
                            }
                        } catch (Exception ignored) {}

                        if (size > 5 * 1024 * 1024) {
                            Toast.makeText(this, "Image file exceeds 5MB limit. Please choose a smaller image.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        selectedNewsImageUri = uri;
                        if (currentDialogImagePreview != null) {
                            currentDialogImagePreview.setImageURI(uri);
                        }
                        if (currentDialogPlaceholder != null) {
                            currentDialogPlaceholder.setVisibility(View.GONE);
                        }
                        if (currentDialogRemoveButton != null) {
                            currentDialogRemoveButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadNews();

        binding.fabAddNews.setOnClickListener(v -> showAddNewsDialog());
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter(this);
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNews.setAdapter(adapter);
    }

    private void loadNews() {
        List<NewsItem> list = repository.getAllNewsList();
        adapter.setNewsList(list);
        binding.tvCountHeader.setText("Published Agriculture News (" + list.size() + ")");
    }

    private void showAddNewsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_news, null);

        EditText etTitle = dialogView.findViewById(R.id.etNewsTitle);
        EditText etCat = dialogView.findViewById(R.id.etNewsCategory);
        EditText etSummary = dialogView.findViewById(R.id.etNewsSummary);
        EditText etContent = dialogView.findViewById(R.id.etNewsContent);
        EditText etSource = dialogView.findViewById(R.id.etNewsSource);

        currentDialogImagePreview = dialogView.findViewById(R.id.ivNewsPreview);
        currentDialogPlaceholder = dialogView.findViewById(R.id.layoutImagePlaceholder);
        MaterialButton btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        currentDialogRemoveButton = dialogView.findViewById(R.id.btnRemoveImage);

        selectedNewsImageUri = null;

        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        currentDialogRemoveButton.setOnClickListener(v -> {
            selectedNewsImageUri = null;
            if (currentDialogImagePreview != null) {
                currentDialogImagePreview.setImageResource(R.drawable.bg_gradient_header);
            }
            if (currentDialogPlaceholder != null) {
                currentDialogPlaceholder.setVisibility(View.VISIBLE);
            }
            currentDialogRemoveButton.setVisibility(View.GONE);
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Publish Agricultural Story")
                .setView(dialogView)
                .setPositiveButton("Publish Now", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String cat = etCat.getText().toString().trim();
                    String summary = etSummary.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    String source = etSource.getText().toString().trim();

                    if (title.isEmpty() || summary.isEmpty() || content.isEmpty()) {
                        Toast.makeText(this, "Headline, summary, and details are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long id = System.currentTimeMillis();
                    String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

                    String imageUrl;
                    if (selectedNewsImageUri != null) {
                        imageUrl = saveImageToInternalStorage(id, selectedNewsImageUri);
                    } else {
                        imageUrl = "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=600";
                    }

                    NewsItem item = new NewsItem(
                            id,
                            title,
                            summary,
                            content,
                            cat.isEmpty() ? "Markets" : cat,
                            imageUrl,
                            source.isEmpty() ? "Agri Smart Newsroom" : source,
                            date
                    );

                    repository.addNews(item);
                    loadNews();
                    Toast.makeText(this, "News story published with persistent image!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String saveImageToInternalStorage(long newsId, Uri uri) {
        try {
            File imagesDir = new File(getFilesDir(), "news_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }
            File targetFile = new File(imagesDir, "news_" + newsId + ".jpg");
            try (InputStream in = getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }
            return targetFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=600";
        }
    }
}
