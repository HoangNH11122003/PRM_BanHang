# Dự án Ứng dụng OCS (Online Commerce System)

Chào mừng đến với dự án **OCS**! Đây là tài liệu hướng dẫn cách tổ chức mã nguồn theo kiến trúc **MVC** với Java thuần, sử dụng **Room** làm cơ sở dữ liệu local và xử lý bất đồng bộ bằng `ExecutorService` và `Handler`. Vui lòng tuân thủ cấu trúc này để đảm bảo tính nhất quán và dễ bảo trì.

## 1. Tổng quan kiến trúc dự án
Dự án sử dụng kiến trúc **MVC**:
- **Model**: Chứa logic dữ liệu (entities, DAOs, database).
- **View**: Chứa giao diện người dùng (Activities, Fragments).
- **Controller**: Điều phối giữa Model và View, xử lý logic nghiệp vụ.
- Xử lý bất đồng bộ được thực hiện bằng ExecutorService (luồng nền) và Handler (luồng chính) để tránh chặn UI thread.

## 2. Cấu trúc thư mục package
Mã nguồn được tổ chức trong package gốc com.prm.ocs. Dưới đây là cấu trúc chi tiết:

```com.prm.ocs
├── data
│   ├── db
│   │   ├── AppDatabase.java            // Class định nghĩa Room Database
│   │   ├── dao
│   │   │   ├── UserDao.java           // DAO cho User
│   │   │   ├── ProductDao.java        // DAO cho Product
│   │   │   └── ...                    // DAO cho các entity khác
│   │   └── entity
│   │       ├── User.java              // Entity User
│   │       ├── Product.java           // Entity Product
│   │       └── ...                    // Entity cho các bảng khác
│   └── DatabaseClient.java            // Class quản lý database (Singleton)
├── ui
│   ├── MainActivity.java              // Activity chính (View)
│   ├── fragments                      // Các Fragment (nếu có)
│   │   └── UserFragment.java          // Ví dụ Fragment
│   └── adapters                       // Các Adapter cho RecyclerView/ListView
│       └── UserAdapter.java           // Ví dụ Adapter
└── controller
    ├── UserController.java            // Controller cho User
    ├── ProductController.java         // Controller cho Product
    └── ...                            // Controller cho các entity khác
```
    Giải thích:
        data: Chứa toàn bộ logic liên quan đến dữ liệu.
        db: Định nghĩa database (AppDatabase), DAO (UserDao, v.v.), và entity (User, v.v.).
        DatabaseClient.java: Quản lý instance của AppDatabase và xử lý bất đồng bộ.
        ui: Chứa các thành phần giao diện (Activities, Fragments, Adapters).
        controller: Chứa các class điều phối giữa Model và View.

## 3. Hướng dẫn viết code theo MVC
Model
    Entity: Đặt trong com.prm.ocs.data.db.entity.
        Mỗi entity là một class Java với annotation @Entity của Room.

    DAO: Đặt trong com.prm.ocs.data.db.dao.
        Mỗi DAO là một interface với annotation @Dao, chứa các truy vấn cơ bản.

    AppDatabase: Đặt trong com.prm.ocs.data.db.
        Là abstract class định nghĩa database và các DAO.

    DatabaseClient: Đặt trong com.prm.ocs.data.
        Quản lý instance của AppDatabase và xử lý bất đồng bộ.
View
    Activity/Fragment: Đặt trong com.prm.ocs.ui hoặc com.prm.ocs.ui.fragments.
        Chỉ hiển thị dữ liệu và gửi sự kiện đến Controller.

Controller
    Controller: Đặt trong com.prm.ocs.controller.
        Điều phối giữa View và Model, xử lý bất đồng bộ.
        Ví dụ UserController.java:

            package com.prm.ocs.controller;
            import com.prm.ocs.data.DatabaseClient;
            import com.prm.ocs.data.db.entity.User;
            import com.prm.ocs.ui.MainActivity;
            import java.util.List;

            public class UserController {
                private MainActivity view;
                private DatabaseClient dbClient;

                public UserController(MainActivity view) {
                    this.view = view;
                    this.dbClient = DatabaseClient.getInstance(view);
                }

                public void onInsertUser(final String username, final String password) {
                    dbClient.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            User user = new User();
                            user.setUsername(username);
                            user.setPassword(password);
                            user.setVerified(false);
                            user.setRole(1);

                            dbClient.getAppDatabase().userDao().insert(user);

                            dbClient.getMainHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    loadUsers();
                                }
                            });
                        }
                    });
                }

                public void loadUsers() {
                    dbClient.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<User> users = dbClient.getAppDatabase().userDao().getAllUsers();
                            final StringBuilder userList = new StringBuilder();
                            for (User user : users) {
                                userList.append(user.getUsername()).append("\n");
                            }

                            dbClient.getMainHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    view.displayUsers(userList.toString());
                                }
                            });
                        }
                    });
                }
            }

## 4. Hướng dẫn xử lý bất đồng bộ
    - Luồng nền: Sử dụng DatabaseClient.getExecutorService().execute() để chạy các tác vụ cơ sở dữ liệu (insert, update, query).

    - Luồng chính: Sử dụng DatabaseClient.getMainHandler().post() để cập nhật UI sau khi hoàn thành tác vụ nền.

    Ví dụ: Xem UserController.java ở trên.

## 5. Quy tắc viết code
- Đặt tên:

    - Entity: TênEntity.java (VD: User.java).
    - DAO: TênEntityDao.java (VD: UserDao.java).
    - Controller: TênEntityController.java (VD: UserController.java).
    - Package: Luôn tuân thủ cấu trúc com.prm.ocs.data, com.prm.ocs.ui, com.prm.ocs.controller.

- Bất đồng bộ: Không gọi trực tiếp phương thức DAO trên luồng chính, luôn dùng ExecutorService và Handler.

- Dependencies: Thêm thư viện mới vào gradle/libs.versions.toml, không thêm trực tiếp trong build.gradle.

Happy coding!# PRM_BanHang
