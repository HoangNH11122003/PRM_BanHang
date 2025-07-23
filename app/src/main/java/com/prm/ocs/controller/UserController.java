package com.prm.ocs.controller;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.prm.ocs.data.DatabaseClient;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.view.base.UserView;
import com.prm.ocs.utils.CountCallback;

import java.util.List;
import java.util.UUID;

public class UserController {
    private final UserView view;
    private final Context context;
    private final DatabaseClient dbClient;

    public UserController(UserView view) {
        this.view = view;
        Context context;
        if (view instanceof Fragment) {
            context = ((Fragment) view).requireContext();
        } else if (view instanceof Context) {
            context = (Context) view;
        } else {
            throw new IllegalArgumentException("UserView must be a Fragment or Context");
        }
        this.context = context;
        this.dbClient = DatabaseClient.getInstance(context);
    }

    public UserController(Context context) {
        this.view = null;
        this.context = context;
        this.dbClient = DatabaseClient.getInstance(context);
    }

    public void loadUsers() {
        dbClient.getExecutorService().execute(() -> {
            final List<User> users = dbClient.getAppDatabase().userDao().getAllUsers();
            dbClient.getMainHandler().post(() -> view.displayUsers(users));
        });
    }

    public void loadUserDetails(UUID userId) {
        dbClient.getExecutorService().execute(() -> {
            final User user = dbClient.getAppDatabase().userDao().getUserById(userId);
            dbClient.getMainHandler().post(() -> view.displayUserDetails(user));
        });
    }

    public void loadUserDetailsByUsername(String username) {
        dbClient.getExecutorService().execute(() -> {
            final User user = dbClient.getAppDatabase().userDao().getUserByUsername(username);
            dbClient.getMainHandler().post(() -> view.displayUserDetails(user));
        });
    }

    // insert user
    public void insertUser(User user) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().userDao().insert(user);
            dbClient.getMainHandler().post(() -> view.onLoginSuccess(user));
        });
    }

    // update user
    public void updateUser(User user) {
        dbClient.getExecutorService().execute(() -> {
            dbClient.getAppDatabase().userDao().update(user);
            dbClient.getMainHandler().post(() -> view.onLoginSuccess(user));
        });
    }

    // delete user catch sql constraint exception when user has orders
    // // // //dbClient.getExecutorService().execute(() -> {
    // // // // try {
    // // // // dbClient.getAppDatabase().brandDao().delete(brand);
    // // // // dbClient.getMainHandler().post(() -> {
    // // // // Toast.makeText(context, "Brand '" + brand.getName() + "' deleted
    // successfully.", Toast.LENGTH_SHORT).show();
    // // // // });
    // // // // } catch (SQLiteConstraintException e) {
    // // // // Log.w("BrandController", "Constraint error deleting brand: " +
    // brand.getBrandId(), e);
    // // // // dbClient.getMainHandler().post(() -> {
    // // // // Toast.makeText(context, "Cannot delete brand '" + brand.getName() +
    // "'. Products are still associated with it.", Toast.LENGTH_LONG).show();
    // // // // });
    // // // // } catch (Exception e) {
    // // // // Log.e("BrandController", "Error deleting brand: " +
    // brand.getBrandId(), e);
    // // // // dbClient.getMainHandler().post(() -> {
    // // // // Toast.makeText(context, "Error deleting brand: " + e.getMessage(),
    // Toast.LENGTH_SHORT).show();
    // // // // });
    // // // // }
    // // // // });
    // delete user catch sql constraint exception when user has orders
    // // // //dbClient.getExecutorService().execute(() -> {
    // // // // try {
    // // // // dbClient.getAppDatabase().brandDao().delete(brand);
    // // // // dbClient.getMainHandler().post(() -> {
    // // // // Toast.makeText(context, "Brand '" + brand.getName() + "' deleted
    // successfully.", Toast.LENGTH_SHORT).show();
    // // // // });
    // // // // } catch (SQLiteConstraintException e) {
    // // // // Log.w("BrandController", "Constraint error deleting brand: " +
    // brand.getBrandId(), e);
    // // // // dbClient.getMainHandler().post(() -> {
    // // // // Toast.makeText(context, "Cannot delete brand '" + brand.getName() +
    // "'. Products are still associated with it.", Toast.LENGTH_LONG).show();
    // // // // });
    // // // // } catch (Exception e) {
    // // // // Log.e("BrandController", "Error deleting brand: " +
    // brand.getBrandId(), e);
    // // // // dbClient.getMainHandler().post(() -> {
    // // // // Toast.makeText(context, "Error deleting brand: " + e.getMessage(),
    // Toast.LENGTH_SHORT).show();
    // // // // });
    // // // // }
    // // // // });
    public void deleteUser(User user) {
        dbClient.getExecutorService().execute(() -> {
            try {
                dbClient.getAppDatabase().userDao().delete(user);
                dbClient.getMainHandler().post(() -> view.onLoginSuccess(user));
            } catch (SQLiteConstraintException e) {
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context,
                            "Cannot delete user '" + user.getUsername() + "'. Orders are still associated with it.",
                            Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                dbClient.getMainHandler().post(() -> {
                    Toast.makeText(context, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // dbClient.getAppDatabase().userDao().delete(user);
    //
    public void getUserCount(CountCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            try {

                final int count = dbClient.getAppDatabase().userDao().getUserCount();
                dbClient.getMainHandler().post(() -> callback.onCountLoaded(count));
            } catch (Exception e) {
                dbClient.getMainHandler().post(() -> callback.onError("Error getting user count: " + e.getMessage()));
            }
        });
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword, Callback callback) {
        dbClient.getExecutorService().execute(() -> {
            // Kiểm tra mật khẩu cũ
            User user = dbClient.getAppDatabase().userDao().checkUserPassword(userId, oldPassword);
            if (user == null) {
                // Mật khẩu cũ không đúng
                dbClient.getMainHandler().post(() -> callback.onResult(false));
                return;
            }

            // Cập nhật mật khẩu mới
            user.setPassword(newPassword);
            dbClient.getAppDatabase().userDao().update(user);
            dbClient.getMainHandler().post(() -> callback.onResult(true));
        });
    }

    public void checkUserExists(String username, String email, String phone) {
        dbClient.getExecutorService().execute(() -> {
            final User user = dbClient.getAppDatabase().userDao().findByUsernameEmailOrPhone(username, email, phone);
            dbClient.getMainHandler().post(() -> view.displayUserDetails(user));
        });
    }

    public void checkUserExistsForInsert(String username, String email, String phone,
            CheckUserExistsCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            try {
                User existingUser = dbClient.getAppDatabase().userDao().findByUsernameForInsert(username);
                if (existingUser != null) {
                    dbClient.getMainHandler().post(() -> callback.onResult("username"));
                    return;
                }
                existingUser = dbClient.getAppDatabase().userDao().findByEmailForInsert(email);
                if (existingUser != null) {
                    dbClient.getMainHandler().post(() -> callback.onResult("email"));
                    return;
                }
                existingUser = dbClient.getAppDatabase().userDao().findByPhoneForInsert(phone);
                if (existingUser != null) {
                    dbClient.getMainHandler().post(() -> callback.onResult("phone"));
                    return;
                }
                dbClient.getMainHandler().post(() -> callback.onResult(null));
            } catch (Exception e) {
                Log.e(TAG, "Error checking user existence for insert", e);
                dbClient.getMainHandler().post(() -> callback.onError("Error checking user: " + e.getMessage()));
            }
        });
    }

    public void checkUserExistsForUpdate(String username, String email, String phone, UUID currentUserId,
            CheckUserExistsCallback callback) {
        dbClient.getExecutorService().execute(() -> {
            try {
                User existingUser = dbClient.getAppDatabase().userDao().findByUsernameExcludingId(username,
                        currentUserId);
                if (existingUser != null) {
                    dbClient.getMainHandler().post(() -> callback.onResult("username"));
                    return;
                }
                existingUser = dbClient.getAppDatabase().userDao().findByEmailExcludingId(email, currentUserId);
                if (existingUser != null) {
                    dbClient.getMainHandler().post(() -> callback.onResult("email"));
                    return;
                }
                existingUser = dbClient.getAppDatabase().userDao().findByPhoneExcludingId(phone, currentUserId);
                if (existingUser != null) {
                    dbClient.getMainHandler().post(() -> callback.onResult("phone"));
                    return;
                }
                dbClient.getMainHandler().post(() -> callback.onResult(null));
            } catch (Exception e) {
                Log.e(TAG, "Error checking user existence for update", e);
                dbClient.getMainHandler().post(() -> callback.onError("Error checking user: " + e.getMessage()));
            }
        });
    }

    // Callback interface để trả về kết quả
    public interface Callback {
        void onResult(boolean success);
    }

    public interface CheckUserExistsCallback {
        void onResult(String field);

        void onError(String message);
    }

    public void dummyFunction1() {
        // Đây là một hàm mẫu không làm gì cả
    }

    public int dummyFunction2(int a, int b) {
        // Hàm mẫu trả về một giá trị bất kỳ, không có tác dụng thực tế
        return a + b;
    }

    public void dummyFunction3(String message) {
        // Hàm mẫu chỉ in ra một thông báo
        System.out.println(message);
    }

}