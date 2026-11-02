package com.example.jobfinder;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployerNav {

    public static void setup(Activity activity, BottomNavigationView nav, int selectedId) {
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == selectedId) return true;

            Class<?> target;
            if (id == R.id.nav_dashboard) {
                target = EmployerMainActivity.class;
            } else if (id == R.id.nav_manage_jobs) {
                target = ManageJobsActivity.class;
            } else if (id == R.id.nav_applicants) {
                target = ApplicantsListActivity.class;
            } else if (id == R.id.nav_profile) {
                target = EmployerProfileActivity.class;
            } else {
                return false;
            }

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(intent);
            if (!(activity instanceof EmployerMainActivity)) {
                activity.finish();
            }
            activity.overridePendingTransition(0, 0);
            return true;
        });
    }
}