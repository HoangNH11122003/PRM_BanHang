package com.prm.ocs.ui.view.base;

import com.prm.ocs.data.db.entity.User;
import java.util.List;

public interface UserView {
    void displayUsers(List<User> users);       // Hiển thị danh sách người dùng
    void displayUserDetails(User user);        // Hiển thị chi tiết một người dùng
    void onLoginSuccess(User user);            // Thông báo đăng nhập thành công
}
