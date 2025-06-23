package com.prm.ocs.ui.fragments.product;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.prm.ocs.R;
import com.prm.ocs.controller.BrandController;
import com.prm.ocs.controller.CategoryController;
import com.prm.ocs.controller.ProductController;
import com.prm.ocs.data.db.entity.Brand;
import com.prm.ocs.data.db.entity.Category;
import com.prm.ocs.data.db.entity.Product;
import com.prm.ocs.ui.adapters.BrandAdapter;
import com.prm.ocs.ui.adapters.CategoryAdapter;
import com.prm.ocs.ui.view.base.ProductView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminProductAddFragment extends Fragment implements ProductView, CategoryController.CategorySimpleCallback, BrandController.BrandSimpleCallback {
    private ProductController productController;
    private CategoryController categoryController;
    private BrandController brandController;
    private EditText nameEditText, sellingPriceEditText, importPriceEditText, stockEditText, soldEditText, descriptionEditText;
    private Spinner categorySpinner, brandSpinner;
    private ImageView clothesImageView;
    private Button saveButton;
    private List<Category> categoryList = new ArrayList<>();
    private List<Brand> brandList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private BrandAdapter brandAdapter;
    private String imagePath; // Đường dẫn ảnh được lưu trong bộ nhớ nội bộ
    private Uri photoUri; // URI của ảnh chụp từ camera

    private static final String ARG_PRODUCT_ID = "product_id";

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    public static AdminProductAddFragment newInstance(UUID productId) {
        AdminProductAddFragment fragment = new AdminProductAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Activity Result Launchers
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                    clothesImageView.setImageBitmap(bitmap);
                    imagePath = saveImageToInternalStorage(bitmap, "product_" + System.currentTimeMillis() + ".jpg");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), photoUri);
                    clothesImageView.setImageBitmap(bitmap);
                    imagePath = saveImageToInternalStorage(bitmap, "product_" + System.currentTimeMillis() + ".jpg");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product_detail, container, false);

        nameEditText = view.findViewById(R.id.clothes_name);
        sellingPriceEditText = view.findViewById(R.id.clothes_selling_price);
        importPriceEditText = view.findViewById(R.id.clothes_sold_price);
        stockEditText = view.findViewById(R.id.clothes_stock);
        soldEditText = view.findViewById(R.id.clothes_sold);
        categorySpinner = view.findViewById(R.id.clothes_category);
        brandSpinner = view.findViewById(R.id.clothes_brand);
        descriptionEditText = view.findViewById(R.id.clothes_description);
        clothesImageView = view.findViewById(R.id.clothes_image);

        saveButton = view.findViewById(R.id.save_button);
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        productController = new ProductController(this);
        categoryController = new CategoryController(getContext());
        brandController = new BrandController(getContext());

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        brandAdapter = new BrandAdapter(getContext(), brandList);

        categorySpinner.setAdapter(categoryAdapter);
        brandSpinner.setAdapter(brandAdapter);

        brandController.loadBrands(this);
        categoryController.loadCategories(this);

        Product newProduct = new Product();
        newProduct.setProductId(UUID.fromString(getArguments().getString(ARG_PRODUCT_ID)));
        displayProductDetails(newProduct);

        // Xử lý sự kiện nhấn vào ImageView
        clothesImageView.setOnClickListener(v -> showImageSelectionDialog());

        saveButton.setOnClickListener(v -> addProduct());

        return view;
    }

    private void showImageSelectionDialog() {

        String[] options = {"Choose from Gallery","Enter Image URL"};
        boolean hasCamera = requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        List<String> optionsList = new ArrayList<>();
        optionsList.add("Choose from Gallery");
        if (hasCamera) {
            optionsList.add("Take a Photo");
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Chọn từ thư viện
                            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickImageLauncher.launch(pickIntent);
                            break;
                        case 1: // Chụp ảnh
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File photoFile = createImageFile();
                            if (photoFile != null) {
                                photoUri = FileProvider.getUriForFile(requireContext(),
                                        "com.prm.ocs.fileprovider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                takePictureLauncher.launch(takePictureIntent);
                            }
                            break;
                        case 2: // Nhập URL
                            showUrlInputDialog();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private File createImageFile() {
        try {
            String imageFileName = "product_" + System.currentTimeMillis();
            File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            return image;
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void showUrlInputDialog() {
        EditText urlInput = new EditText(getContext());
        urlInput.setHint("Enter image URL");
        new AlertDialog.Builder(requireContext())
                .setTitle("Enter Image URL")
                .setView(urlInput)
                .setPositiveButton("Load", (dialog, which) -> {
                    String url = urlInput.getText().toString().trim();
                    if (!url.isEmpty()) {
                        loadImageFromUrl(url);
                    } else {
                        Toast.makeText(getContext(), "Please enter a valid URL", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                requireActivity().runOnUiThread(() -> {
                    clothesImageView.setImageBitmap(bitmap);
                    imagePath = saveImageToInternalStorage(bitmap, "product_" + System.currentTimeMillis() + ".jpg");
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading image from URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            File directory = new File(requireContext().getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void displayProductDetails(Product product) {
        nameEditText.setText(product.getName() != null ? product.getName() : "");
        sellingPriceEditText.setText(String.valueOf(product.getSellingPrice()));
        importPriceEditText.setText(String.valueOf(product.getImportPrice()));
        stockEditText.setText(String.valueOf(product.getStock()));
        soldEditText.setText(String.valueOf(product.getSold()));
        descriptionEditText.setText(product.getDescription() != null ? product.getDescription() : "");

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            File imageFile = new File(product.getImage());
            if (imageFile.exists()) {
                clothesImageView.setImageURI(Uri.fromFile(imageFile));
            }
        }

        if (product.getCategoryId() != null && !categoryList.isEmpty()) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategoryId().equals(product.getCategoryId())) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }
        if (product.getBrandId() != null && !brandList.isEmpty()) {
            for (int i = 0; i < brandList.size(); i++) {
                if (brandList.get(i).getBrandId().equals(product.getBrandId())) {
                    brandSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void displayProducts(List<Product> products) {
        // Không dùng trong Fragment này
    }

    @Override
    public void onSimpleBrandsLoaded(List<Brand> brands) {
        brandList.clear();
        brandList.addAll(brands);
        brandAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSimpleCategoriesLoaded(List<Category> categories) {
        categoryList.clear();
        categoryList.addAll(categories);
        categoryAdapter.notifyDataSetChanged();
    }

    private void addProduct() {
        try {
            Product newProduct = new Product();
            newProduct.setProductId(UUID.fromString(getArguments().getString(ARG_PRODUCT_ID)));
            newProduct.setName(nameEditText.getText().toString());
            newProduct.setSellingPrice(Double.parseDouble(sellingPriceEditText.getText().toString()));
            newProduct.setImportPrice(Double.parseDouble(importPriceEditText.getText().toString()));
            newProduct.setStock(Integer.parseInt(stockEditText.getText().toString()));
            newProduct.setSold(Integer.parseInt(soldEditText.getText().toString()));
            newProduct.setCategoryId(categoryList.get(categorySpinner.getSelectedItemPosition()).getCategoryId());
            newProduct.setBrandId(brandList.get(brandSpinner.getSelectedItemPosition()).getBrandId());
            newProduct.setDescription(descriptionEditText.getText().toString());
            newProduct.setImage(imagePath); // Lưu đường dẫn ảnh vào Product

            if (newProduct.getName().isEmpty()) {
                Toast.makeText(getContext(), "Product name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newProduct.getStock() < 0) {
                Toast.makeText(getContext(), "Stock must be greater than or equal to 0!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newProduct.getImportPrice() <= 0) {
                Toast.makeText(getContext(), "Import price must be greater than 0!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newProduct.getSellingPrice() <= 0 && newProduct.getSellingPrice() < newProduct.getImportPrice()) {
                Toast.makeText(getContext(), "Selling price must be greater than 0 and greater than import price!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newProduct.getCategoryId() == null) {
                Toast.makeText(getContext(), "Category is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newProduct.getBrandId() == null) {
                Toast.makeText(getContext(), "Brand is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newProduct.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            productController.addProduct(newProduct);
            Toast.makeText(getContext(), "Product added", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}