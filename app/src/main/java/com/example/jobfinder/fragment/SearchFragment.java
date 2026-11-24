package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.activity.JobDetailActivity;
import com.example.jobfinder.adapter.JobAdapter;
import com.example.jobfinder.model.Job;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String ALL_LOCATIONS = "Tất cả địa điểm";
    private static final String ALL_TYPES = "Tất cả loại hình";
    private static final String ALL_SALARIES = "Tất cả mức lương";

    private EditText etSearchKeyword;
    private Button btnSearch;
    private Spinner spinnerLocation, spinnerJobType, spinnerSalary;
    private RecyclerView recyclerViewSearchResult;
    private TextView tvSearchEmpty, tvSearchCount;

    private JobAdapter jobAdapter;
    private final List<Job> allJobs = new ArrayList<>();
    private final List<Job> filteredJobs = new ArrayList<>();

    private FirebaseFirestore db;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        etSearchKeyword = view.findViewById(R.id.etSearchKeyword);
        btnSearch = view.findViewById(R.id.btnSearch);
        spinnerLocation = view.findViewById(R.id.spinnerLocation);
        spinnerJobType = view.findViewById(R.id.spinnerJobType);
        spinnerSalary = view.findViewById(R.id.spinnerSalary);
        recyclerViewSearchResult = view.findViewById(R.id.recyclerViewSearchResult);
        tvSearchEmpty = view.findViewById(R.id.tvSearchEmpty);
        tvSearchCount = view.findViewById(R.id.tvSearchCount);

        recyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        jobAdapter = new JobAdapter(filteredJobs, job -> {
            Intent intent = new Intent(getActivity(), JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
            startActivity(intent);
        });
        recyclerViewSearchResult.setAdapter(jobAdapter);

        setupSpinners();

        btnSearch.setOnClickListener(v -> applyFilters());

        etSearchKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                applyFilters();
                return true;
            }
            return false;
        });

        etSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllJobs();
    }

    private void setupSpinners() {
        String[] locations = {ALL_LOCATIONS, "TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng"};
        String[] jobTypes = {ALL_TYPES, "Full-time", "Part-time", "Internship", "Remote"};
        String[] salaryRanges = {ALL_SALARIES, "Dưới 10 triệu", "10-20 triệu", "Trên 20 triệu"};

        bindSpinner(spinnerLocation, locations);
        bindSpinner(spinnerJobType, jobTypes);
        bindSpinner(spinnerSalary, salaryRanges);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerLocation.setOnItemSelectedListener(listener);
        spinnerJobType.setOnItemSelectedListener(listener);
        spinnerSalary.setOnItemSelectedListener(listener);
    }

    private void bindSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, items);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private void loadAllJobs() {
        db.collection("jobs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    allJobs.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Job job = doc.toObject(Job.class);
                        job.setId(doc.getId());
                        if (!job.isClosed() && job.isApproved()) {
                            allJobs.add(job);
                        }
                    }
                    Collections.sort(allJobs, (a, b) -> Long.compare(b.getPostedDate(), a.getPostedDate()));
                    applyFilters();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void applyFilters() {
        if (!isAdded() || spinnerLocation.getSelectedItem() == null) return;

        String keyword = etSearchKeyword.getText().toString().trim().toLowerCase();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();
        String selectedJobType = spinnerJobType.getSelectedItem().toString();
        String selectedSalary = spinnerSalary.getSelectedItem().toString();

        filteredJobs.clear();

        for (Job job : allJobs) {
            boolean matchKeyword = keyword.isEmpty()
                    || (job.getTitle() != null && job.getTitle().toLowerCase().contains(keyword))
                    || (job.getCompanyName() != null && job.getCompanyName().toLowerCase().contains(keyword));

            boolean matchJobType = selectedJobType.equals(ALL_TYPES)
                    || (job.getJobType() != null && job.getJobType().equalsIgnoreCase(selectedJobType));

            if (matchKeyword
                    && matchLocation(job.getLocation(), selectedLocation)
                    && matchJobType
                    && matchSalaryRange(job.getSalary(), selectedSalary)) {
                filteredJobs.add(job);
            }
        }

        jobAdapter.updateData(filteredJobs);
        tvSearchCount.setText(filteredJobs.size() + " việc làm");
        tvSearchEmpty.setVisibility(filteredJobs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean matchLocation(String jobLocation, String selected) {
        if (selected.equals(ALL_LOCATIONS)) return true;
        if (jobLocation == null) return false;

        String loc = jobLocation.toLowerCase();
        switch (selected) {
            case "TP. Hồ Chí Minh":
                return loc.contains("hồ chí minh") || loc.contains("ho chi minh")
                        || loc.contains("hcm") || loc.contains("sài gòn") || loc.contains("sai gon");
            case "Hà Nội":
                return loc.contains("hà nội") || loc.contains("ha noi") || loc.contains("hn");
            case "Đà Nẵng":
                return loc.contains("đà nẵng") || loc.contains("da nang");
            default:
                return loc.contains(selected.toLowerCase());
        }
    }

    private boolean matchSalaryRange(String salaryText, String selectedRange) {
        if (selectedRange.equals(ALL_SALARIES) || salaryText == null) {
            return true;
        }

        int salaryNumber = extractFirstNumber(salaryText);
        if (salaryNumber == -1) return true;

        switch (selectedRange) {
            case "Dưới 10 triệu":
                return salaryNumber < 10;
            case "10-20 triệu":
                return salaryNumber >= 10 && salaryNumber <= 20;
            case "Trên 20 triệu":
                return salaryNumber > 20;
            default:
                return true;
        }
    }

    private int extractFirstNumber(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            } else if (sb.length() > 0) {
                break;
            }
        }
        if (sb.length() == 0) return -1;
        try {
            return Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}