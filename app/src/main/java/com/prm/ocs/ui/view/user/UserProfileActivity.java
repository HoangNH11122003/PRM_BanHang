package com.prm.ocs.ui.view.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.ocs.R;
import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.SeedData;
import com.prm.ocs.ui.fragments.user.CustomerUserDetailFragment;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Hiển thị CustomerUserDetailFragment
        CustomerUserDetailFragment fragment = new CustomerUserDetailFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}