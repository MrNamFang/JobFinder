package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jobfinder.R;
import com.example.jobfinder.activity.JobDetailActivity;
import com.example.jobfinder.adapter.JobAdapter;
import com.example.jobfinder.model.Job;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearchKeyword;
    private Button btnSearch;
    private Spinner spinnerLocation, spinnerJobType, spinnerSalary;
    private RecyclerView recyclerViewSearchResult;
    private TextView tvSearchEmpty;

    private JobAdapter jobAdapter;
    private List<Job> allJobs = new ArrayList<>(); // toàn bộ job tải từ Firestore
    private List<Job> filteredJobs = new ArrayList<>(); // job sau khi lọc

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

        recyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        jobAdapter = new JobAdapter(filteredJobs, job -> {
            Intent intent = new Intent(getActivity(), JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
            startActivity(intent);
        });
        recyclerViewSearchResult.setAdapter(jobAdapter);

        setupSpinners();
        loadAllJobs();

        btnSearch.setOnClickListener(v -> applyFilters());

        // Cho phép bấm nút "Tìm kiếm" trên bàn phím ảo
        etSearchKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                applyFilters();
                return true;
            }
            return false;
        });
    }

    private void setupSpinners() {
        String[] locations = {"Tất cả địa điểm", "TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng"};
        String[] jobTypes = {"Tất cả loại hình", "Full-time", "Part-time"};
        String[] salaryRanges = {"Tất cả mức lương", "Dưới 10 triệu", "10-20 triệu", "Trên 20 triệu"};

        spinnerLocation.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, locations));
        spinnerJobType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, jobTypes));
        spinnerSalary.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, salaryRanges));
    }

    private void loadAllJobs() {
        db.collection("jobs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allJobs.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Job job = doc.toObject(Job.class);
                        job.setId(doc.getId());
                        allJobs.add(job);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void applyFilters() {
        String keyword = etSearchKeyword.getText().toString().trim().toLowerCase();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();
        String selectedJobType = spinnerJobType.getSelectedItem().toString();
        String selectedSalary = spinnerSalary.getSelectedItem().toString();

        filteredJobs.clear();

        for (Job job : allJobs) {
            boolean matchKeyword = keyword.isEmpty()
                    || (job.getTitle() != null && job.getTitle().toLowerCase().contains(keyword))
                    || (job.getCompanyName() != null && job.getCompanyName().toLowerCase().contains(keyword));

            boolean matchLocation = selectedLocation.equals("Tất cả địa điểm")
                    || (job.getLocation() != null && job.getLocation().contains(selectedLocation));

            boolean matchJobType = selectedJobType.equals("Tất cả loại hình")
                    || (job.getJobType() != null && job.getJobType().equals(selectedJobType));

            boolean matchSalary = matchSalaryRange(job.getSalary(), selectedSalary);

            if (matchKeyword && matchLocation && matchJobType && matchSalary) {
                filteredJobs.add(job);
            }
        }

        jobAdapter.updateData(filteredJobs);
        tvSearchEmpty.setVisibility(filteredJobs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Lọc lương đơn giản dựa vào chữ số đầu tiên tìm thấy trong chuỗi salary (vd: "15-20 triệu" -> 15)
    private boolean matchSalaryRange(String salaryText, String selectedRange) {
        if (selectedRange.equals("Tất cả mức lương") || salaryText == null) {
            return true;
        }

        int salaryNumber = extractFirstNumber(salaryText);
        if (salaryNumber == -1) return true; // không parse được thì không loại bỏ

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
                break; // gặp ký tự không phải số sau khi đã có số -> dừng
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