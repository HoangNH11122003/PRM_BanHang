package com.prm.ocs.ui.fragments.user;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.adapters.UserAdapter;
import com.prm.ocs.ui.view.base.UserView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminUserListFragment extends Fragment implements UserView {
    private static final String TAG = "AdminUserListFragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private LinearLayout emptyViewContainer;
    private TextView emptyViewText;
    private FloatingActionButton fabScrollToTop;

    private UserAdapter userAdapter;
    private UserController userController;
    private List<User> fullUserList = new ArrayList<>();
    private List<User> displayedUserList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        findViews(view);
        setupRecyclerView(); // Gọi setup RecyclerView
        setupSearchView();
        setupSwipeToRefresh();
        setupFab(); // Gọi setup FAB

        userController = new UserController(this);

        return view;
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_users);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_user);
        searchView = view.findViewById(R.id.search_view_user);
        emptyViewContainer = view.findViewById(R.id.empty_view_container_user);
        emptyViewText = view.findViewById(R.id.empty_view_text_user);
        fabScrollToTop = view.findViewById(R.id.fab_scroll_to_top);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(requireContext(), displayedUserList);
        recyclerView.setAdapter(userAdapter);
    }

    private void setupFab() {
        fabScrollToTop.setOnClickListener(v -> {
            recyclerView.smoothScrollToPosition(0);
        });
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText != null ? newText.trim() : "";
                applyFilter();
                return true;
            }
        });
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Swipe to refresh: Resetting search and loading users.");
            resetSearchAndLoad();
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.purple_500,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    private void resetSearchAndLoad() {
        Log.d(TAG, "Resetting search and reloading data...");
        currentQuery = "";
        searchView.setQuery("", false);
        searchView.clearFocus();

        loadUsersData();
    }

    private void loadUsersData() {
        Log.d(TAG, "Loading users...");
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        userController.loadUsers();
    }

    @Override
    public void displayUsers(List<User> users) {
        Log.d(TAG, "Loaded " + (users != null ? users.size() : 0) + " users.");
        if (getView() == null) return;

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        fullUserList.clear();
        if (users != null) {
            fullUserList.addAll(users);
        }

        applyFilter();
    }

    @Override
    public void displayUserDetails(User user) {
        Log.w(TAG, "displayUserDetails called unexpectedly.");
    }

    @Override
    public void onLoginSuccess(User user) {
        Log.w(TAG, "onLoginSuccess called unexpectedly.");
    }

    private void applyFilter() {
        Log.d(TAG, "Applying filter: Query='" + currentQuery + "'");
        if (getView() == null) return;

        List<User> filteredList;

        if (currentQuery.isEmpty()) {
            filteredList = new ArrayList<>(fullUserList);
        } else {
            String lowerCaseQuery = currentQuery.toLowerCase();
            filteredList = fullUserList.stream()
                    .filter(user -> {
                        if (user == null) return false;
                        boolean nameMatch = user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseQuery);
                        boolean emailMatch = user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseQuery);
                        boolean phoneMatch = user.getPhone() != null && user.getPhone().toLowerCase().contains(lowerCaseQuery);
                        return nameMatch || emailMatch || phoneMatch;
                    })
                    .collect(Collectors.toList());
        }

        Log.d(TAG, "Filtering complete. Found " + filteredList.size() + " matching users.");

        displayedUserList.clear();
        displayedUserList.addAll(filteredList);
        userAdapter.notifyDataSetChanged();

        updateEmptyView(displayedUserList.isEmpty());

    }

    private void updateEmptyView(boolean show) {
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyViewContainer.setVisibility(View.VISIBLE);
            if (!currentQuery.isEmpty()) {
                emptyViewText.setText("Không tìm thấy người dùng phù hợp.");
            } else {
                emptyViewText.setText("Chưa có người dùng nào.");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewContainer.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed. Loading users data.");
        loadUsersData();
    }
}