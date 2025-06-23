package com.prm.ocs.data;

import android.content.Context;

import com.prm.ocs.data.db.AppDatabase;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.data.db.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class SeedData {
    private final AppDatabase db;
    private final ExecutorService executorService;

    public SeedData(Context context) {
        DatabaseClient dbClient = DatabaseClient.getInstance(context);
        this.db = dbClient.getAppDatabase();
        this.executorService = dbClient.getExecutorService();
    }

    public void seedDatabase() {
        executorService.execute(() -> {
            // Kiểm tra xem database đã có dữ liệu chưa
            if (db.userDao().getAllUsers().isEmpty()) {
                seedUsers();
                seedCategories();
                seedBrands();
                seedProducts();
            }
        });
    }

    private void seedUsers() {
        List<User> users = new ArrayList<>();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("password");
        admin.setEmail("admin@example.com");
        admin.setImgUrl("/data/user/0/com.prm.ocs/files/images/product_1742745065404.jpg");
        admin.setPhone(null); // Không có trong C# mẫu
        admin.setVerified(true);
        admin.setRole(0); // Admin
        users.add(admin);

        User customer1 = new User();
        customer1.setUsername("customer1");
        customer1.setPassword("password");
        customer1.setEmail("customer1@example.com");
        customer1.setImgUrl("/data/user/0/com.prm.ocs/files/images/product_1742745065404.jpg");
        customer1.setPhone("+84 386570165");
        customer1.setVerified(true);
        customer1.setRole(2); // Customer
        users.add(customer1);

        User customer2 = new User();
        customer2.setUsername("customer2");
        customer2.setPassword("password");
        customer2.setEmail("customer2@example.com");
        customer2.setImgUrl("/data/user/0/com.prm.ocs/files/images/product_1742745065404.jpg");
        customer2.setPhone(null);
        customer2.setVerified(false);
        customer2.setRole(2); // Customer
        users.add(customer2);

        User customer3 = new User();
        customer3.setUsername("customer3");
        customer3.setPassword("password");
        customer3.setEmail("customer3@example.com");
        customer3.setImgUrl("/data/user/0/com.prm.ocs/files/images/product_1742745065404.jpg");
        customer3.setPhone(null);
        customer3.setVerified(false);
        customer3.setRole(2); // Customer
        users.add(customer3);

        for (User user : users) {
            db.userDao().insert(user);
        }
    }

    private void seedCategories() {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category(UUID.randomUUID(), "T-Shirts", "Casual tops for everyday wear"));
        categories.add(new Category(UUID.randomUUID(), "Jeans", "Durable denim pants"));
        categories.add(new Category(UUID.randomUUID(), "Dresses", "Elegant outfits for special occasions"));
        categories.add(new Category(UUID.randomUUID(), "Sweaters", "Warm knitwear for cold weather"));
        categories.add(new Category(UUID.randomUUID(), "Jackets", "Outerwear for style and protection"));
        categories.add(new Category(UUID.randomUUID(), "Shoes", "Footwear for all occasions"));
        categories.add(new Category(UUID.randomUUID(), "Bags", "Stylish carriers for your essentials"));
        categories.add(new Category(UUID.randomUUID(), "Accessories", "Items to complement your outfit"));

        for (Category category : categories) {
            db.categoryDao().insert(category);
        }
    }
    private void seedBrands() {
        List<Brand> brands = new ArrayList<>();

        brands.add(new Brand(UUID.randomUUID(), "Nike", "Sportswear and athletic shoes"));
        brands.add(new Brand(UUID.randomUUID(), "Adidas", "Sportswear and athletic shoes"));
        brands.add(new Brand(UUID.randomUUID(), "Zara", "Fast fashion clothing"));
        brands.add(new Brand(UUID.randomUUID(), "H&M", "Fast fashion clothing"));
        brands.add(new Brand(UUID.randomUUID(), "Levi's", "Denim and casual wear"));
        brands.add(new Brand(UUID.randomUUID(), "Puma", "Sports and lifestyle products"));
        brands.add(new Brand(UUID.randomUUID(), "Coach", "Luxury leather goods and accessories"));

        for (Brand brand : brands) {
            db.brandDao().insert(brand);
        }
    }

    private void seedProducts() {
        List<Brand> brands = db.brandDao().getAllBrands();
        List<Category> categories = db.categoryDao().getAllCategories();

        if (brands.isEmpty() || categories.isEmpty()) {
            return; // Không thêm sản phẩm nếu thiếu brand hoặc category
        }

        // Ánh xạ Brand và Category theo tên
        Brand nike = brands.stream().filter(b -> b.getName().equals("Nike")).findFirst().orElse(null);
        Brand adidas = brands.stream().filter(b -> b.getName().equals("Adidas")).findFirst().orElse(null);
        Brand zara = brands.stream().filter(b -> b.getName().equals("Zara")).findFirst().orElse(null);
        Brand hm = brands.stream().filter(b -> b.getName().equals("H&M")).findFirst().orElse(null);
        Brand levi = brands.stream().filter(b -> b.getName().equals("Levi's")).findFirst().orElse(null);
        Brand puma = brands.stream().filter(b -> b.getName().equals("Puma")).findFirst().orElse(null);
        Brand coach = brands.stream().filter(b -> b.getName().equals("Coach")).findFirst().orElse(null);

        Category tshirts = categories.stream().filter(c -> c.getName().equals("T-Shirts")).findFirst().orElse(null);
        Category jeans = categories.stream().filter(c -> c.getName().equals("Jeans")).findFirst().orElse(null);
        Category dresses = categories.stream().filter(c -> c.getName().equals("Dresses")).findFirst().orElse(null);
        Category sweaters = categories.stream().filter(c -> c.getName().equals("Sweaters")).findFirst().orElse(null);
        Category jackets = categories.stream().filter(c -> c.getName().equals("Jackets")).findFirst().orElse(null);
        Category shoes = categories.stream().filter(c -> c.getName().equals("Shoes")).findFirst().orElse(null);
        Category bags = categories.stream().filter(c -> c.getName().equals("Bags")).findFirst().orElse(null);
        Category accessories = categories.stream().filter(c -> c.getName().equals("Accessories")).findFirst().orElse(null);

        if (nike == null || adidas == null || zara == null || hm == null || levi == null || puma == null || coach == null ||
                tshirts == null || jeans == null || dresses == null || sweaters == null || jackets == null || shoes == null || bags == null || accessories == null) {
            return; // Không thêm sản phẩm nếu thiếu dữ liệu cần thiết
        }

        List<Product> products = new ArrayList<>();

        // Nike Products
        products.add(createProduct("Nike Air Max", 129.99, 199.99, "Iconic sports shoes", 100, 20, shoes, nike, "nike_air_max.png"));
        products.add(createProduct("Nike Dri-FIT T-Shirt", 39.99, 59.99, "Moisture-wicking workout t-shirt", 150, 30, tshirts, nike, "nike_dri_fit_tshirt.png"));

        // Adidas Products
        products.add(createProduct("Adidas Ultraboost", 179.99, 249.99, "Premium running shoes", 80, 15, shoes, adidas, "adidas_ultraboost.png"));
        products.add(createProduct("Adidas Essentials Hoodie", 59.99, 89.99, "Comfortable hoodie for everyday wear", 120, 25, sweaters, adidas, "adidas_essentials_hoodie.png"));

        // Zara Products
        products.add(createProduct("Zara Casual Dress", 79.99, 119.99, "Lightweight summer dress", 60, 10, dresses, zara, "zara_casual_dress.png"));
        products.add(createProduct("Zara Denim Jacket", 99.99, 149.99, "Classic denim jacket", 45, 8, jackets, zara, "zara_denim_jacket.png"));

        // Levi's Products
        products.add(createProduct("Levi's 511 Slim Fit", 89.99, 129.99, "Slim fit denim jeans", 90, 15, jeans, levi, "levis_511_slim_fit.png"));
        products.add(createProduct("Levi's Trucker Jacket", 129.99, 189.99, "Iconic denim trucker jacket", 50, 10, jackets, levi, "levis_trucker_jacket.png"));

        // Puma Products
        products.add(createProduct("Puma Running Shoes", 99.99, 149.99, "Comfortable running shoes", 110, 20, shoes, puma, "puma_running_shoes.png"));
        products.add(createProduct("Puma Sports T-Shirt", 29.99, 49.99, "Breathable t-shirt for sports", 200, 40, tshirts, puma, "puma_sports_tshirt.png"));

        // Coach Products
        products.add(createProduct("Coach Leather Tote", 349.99, 499.99, "Elegant leather tote bag", 30, 5, bags, coach, "coach_leather_tote.png"));
        products.add(createProduct("Coach Crossbody Bag", 199.99, 299.99, "Stylish crossbody bag", 40, 8, bags, coach, "coach_crossbody_bag.png"));

        // Additional Products
        products.add(createProduct("Nike Sports Cap", 19.99, 29.99, "Adjustable sports cap", 150, 30, accessories, nike, "nike_sports_cap.png"));
        products.add(createProduct("Zara Wool Scarf", 29.99, 49.99, "Soft wool scarf", 100, 20, accessories, zara, "zara_wool_scarf.png"));
        products.add(createProduct("Adidas Socks", 9.99, 19.99, "Comfortable sports socks", 250, 50, accessories, adidas, "adidas_socks.png"));
        products.add(createProduct("Levi's Graphic T-Shirt", 34.99, 54.99, "Casual graphic tee", 120, 25, tshirts, levi, "levis_graphic_tshirt.png"));
        products.add(createProduct("Nike Sport Shorts", 39.99, 59.99, "Comfortable shorts for running", 180, 35, accessories, nike, "nike_sport_shorts.png"));
        products.add(createProduct("Puma Tracksuit", 89.99, 129.99, "Comfortable tracksuit for sports", 100, 20, accessories, puma, "puma_tracksuit.png"));
        products.add(createProduct("Coach Leather Wallet", 199.99, 299.99, "Elegant leather wallet", 70, 15, accessories, coach, "coach_leather_wallet.png"));
        products.add(createProduct("Zara Knit Sweater", 49.99, 79.99, "Cozy knit sweater for fall", 150, 30, sweaters, zara, "zara_knit_sweater.png"));
        products.add(createProduct("Adidas Running Shoes", 119.99, 179.99, "High-performance running shoes", 120, 25, shoes, adidas, "adidas_running_shoes.png"));
        products.add(createProduct("Nike Backpack", 49.99, 79.99, "Durable backpack for sports and daily use", 200, 40, accessories, nike, "nike_backpack.png"));
        products.add(createProduct("Levi's Bootcut Jeans", 79.99, 119.99, "Classic bootcut denim jeans", 160, 30, jeans, levi, "levis_bootcut_jeans.png"));
        products.add(createProduct("Zara Bomber Jacket", 89.99, 129.99, "Stylish bomber jacket", 110, 20, jackets, zara, "zara_bomber_jacket.png"));
        products.add(createProduct("Coach Sunglasses", 179.99, 259.99, "Stylish designer sunglasses", 50, 10, accessories, coach, "coach_sunglasses.png"));
        products.add(createProduct("Puma Sport Cap", 24.99, 39.99, "Comfortable sports cap", 180, 35, accessories, puma, "puma_sport_cap.png"));
        products.add(createProduct("Zara Chino Pants", 49.99, 79.99, "Stylish chino pants for casual wear", 140, 25, jeans, zara, "zara_chino_pants.png"));
        products.add(createProduct("Nike Running Tights", 49.99, 79.99, "Breathable running tights", 200, 40, accessories, nike, "nike_running_tights.png"));
        products.add(createProduct("Levi's Straight Jeans", 89.99, 129.99, "Straight fit denim jeans", 130, 25, jeans, levi, "levis_straight_jeans.png"));
        products.add(createProduct("Adidas Yoga Mat", 39.99, 59.99, "Non-slip yoga mat", 100, 20, accessories, adidas, "adidas_yoga_mat.png"));
        products.add(createProduct("Puma Basketball Shoes", 119.99, 179.99, "High-performance basketball shoes", 80, 15, shoes, puma, "puma_basketball_shoes.png"));
        products.add(createProduct("Nike Running Jacket", 69.99, 99.99, "Lightweight jacket for running", 110, 20, jackets, nike, "nike_running_jacket.png"));
        products.add(createProduct("Zara Trench Coat", 129.99, 189.99, "Elegant trench coat for winter", 90, 15, jackets, zara, "zara_trench_coat.png"));
        products.add(createProduct("Coach Messenger Bag", 249.99, 349.99, "Premium leather messenger bag", 60, 10, bags, coach, "coach_messenger_bag.png"));

        for (Product product : products) {
            db.productDao().insert(product);
        }
    }

    private Product createProduct(String name, double importPrice, double sellingPrice, String description,
                                  int stock, int sold, Category category, Brand brand, String image) {
        Product product = new Product();
        product.setName(name);
        product.setImportPrice(importPrice);
        product.setSellingPrice(sellingPrice);
        product.setDescription(description);
        product.setStock(stock);
        product.setSold(sold);
        product.setCategoryId(category.getCategoryId());
        product.setBrandId(brand.getBrandId());
        product.setImage(image);
        return product;
    }
}