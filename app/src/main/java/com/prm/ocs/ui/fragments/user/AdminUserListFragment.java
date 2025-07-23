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



        userController = new UserController(this);

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed. Loading users data.");

    }
}