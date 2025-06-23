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

import com.bumptech.glide.Glide;
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

public class AdminProductDetailFragment extends Fragment implements ProductView, CategoryController.CategorySimpleCallback, BrandController.BrandSimpleCallback {
    private static final String ARG_PRODUCT_ID = "product_id";
    private final List<Category> categoryList = new ArrayList<>();
    private final List<Brand> brandList = new ArrayList<>();
    private ProductController productController;
    private CategoryController categoryController;
    private BrandController brandController;
    private EditText nameEditText, sellingPriceEditText, importPriceEditText, stockEditText, soldEditText, descriptionEditText;
    private Spinner categorySpinner, brandSpinner;
    private ImageView clothesImageView;
    private Button saveButton, deleteButton;
    private UUID productId;
    private Product currentProduct;
    private CategoryAdapter categoryAdapter;
    private BrandAdapter brandAdapter;
    private String imagePath;
    private Uri photoUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    public static AdminProductDetailFragment newInstance(UUID productId) {
        AdminProductDetailFragment fragment = new AdminProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        deleteButton = view.findViewById(R.id.delete_button);

        productController = new ProductController(this);
        categoryController = new CategoryController(getContext());
        brandController = new BrandController(getContext());

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        brandAdapter = new BrandAdapter(getContext(), brandList);

        categorySpinner.setAdapter(categoryAdapter);
        brandSpinner.setAdapter(brandAdapter);

        productId = UUID.fromString(getArguments().getString(ARG_PRODUCT_ID));
        productController.loadProductDetails(productId);

        categoryController.loadCategories(this);
        brandController.loadBrands(this);

        clothesImageView.setOnClickListener(v -> showImageSelectionDialog());

        saveButton.setOnClickListener(v -> updateProduct());
        deleteButton.setOnClickListener(v -> showDeletePopup());

        return view;
    }

    //Show delete popup
    private void showDeletePopup() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProduct())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showImageSelectionDialog() {
        boolean hasCamera = requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        List<String> optionsList = new ArrayList<>();
        optionsList.add("Choose from Gallery");
        if (hasCamera) {
            optionsList.add("Take a Photo");
        }
        optionsList.add("Enter Image URL");

        String[] options = optionsList.toArray(new String[0]);
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (options[which].equals("Choose from Gallery")) {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickImageLauncher.launch(pickIntent);
                    } else if (options[which].equals("Take a Photo")) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = createImageFile();
                        if (photoFile != null) {
                            photoUri = FileProvider.getUriForFile(requireContext(),
                                    "com.prm.ocs.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            takePictureLauncher.launch(takePictureIntent);
                        }
                    } else if (options[which].equals("Enter Image URL")) {
                        showUrlInputDialog();
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
        currentProduct = product;
        if (product != null) {
            nameEditText.setText(product.getName() != null ? product.getName() : "");
            sellingPriceEditText.setText(String.valueOf(product.getSellingPrice()));
            importPriceEditText.setText(String.valueOf(product.getImportPrice()));
            stockEditText.setText(String.valueOf(product.getStock()));
            soldEditText.setText(String.valueOf(product.getSold()));
            descriptionEditText.setText(product.getDescription() != null ? product.getDescription() : "");

            if (product.getImage() != null && !product.getImage().isEmpty()) {
                if (product.getImage().startsWith("/") || product.getImage().contains("/")) {
                    File imageFile = new File(product.getImage());
                    if (imageFile.exists()) {
                        Glide.with(this)
                                .load(imageFile)
                                .error(R.drawable.ic_launcher_background)
                                .into(clothesImageView);
                    } else {
                        loadImageFromDrawable(product.getImage());
                    }
                } else {
                    loadImageFromDrawable(product.getImage());
                }
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_launcher_background)
                        .into(clothesImageView);
            }

            updateCategorySelection();
            updateBrandSelection();
        }
    }

    private void loadImageFromDrawable(String imageName) {
        String imageNameCleaned = imageName.replace(".png", "");
        int imageResId = requireContext().getResources().getIdentifier(imageNameCleaned, "drawable", requireContext().getPackageName());
        Glide.with(this)
                .load(imageResId)
                .error(R.drawable.ic_launcher_background)
                .into(clothesImageView);
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
        updateBrandSelection();
    }

    @Override
    public void onSimpleCategoriesLoaded(List<Category> categories) {
        categoryList.clear();
        categoryList.addAll(categories);
        categoryAdapter.notifyDataSetChanged();
        updateCategorySelection();
    }

    private void updateCategorySelection() {
        if (currentProduct != null && currentProduct.getCategoryId() != null && !categoryList.isEmpty()) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getCategoryId().equals(currentProduct.getCategoryId())) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateBrandSelection() {
        if (currentProduct != null && currentProduct.getBrandId() != null && !brandList.isEmpty()) {
            for (int i = 0; i < brandList.size(); i++) {
                if (brandList.get(i).getBrandId().equals(currentProduct.getBrandId())) {
                    brandSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateProduct() {
        try {
            if (currentProduct == null) return;

            if (imagePath != null && currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()) {
                if (currentProduct.getImage().startsWith("/") || currentProduct.getImage().contains("/")) {
                    File oldImageFile = new File(currentProduct.getImage());
                    if (oldImageFile.exists()) {
                        oldImageFile.delete();
                    }
                }
            }

            currentProduct.setName(nameEditText.getText().toString());
            currentProduct.setSellingPrice(Double.parseDouble(sellingPriceEditText.getText().toString()));
            currentProduct.setImportPrice(Double.parseDouble(importPriceEditText.getText().toString()));
            currentProduct.setStock(Integer.parseInt(stockEditText.getText().toString()));
            currentProduct.setSold(Integer.parseInt(soldEditText.getText().toString()));
            currentProduct.setCategoryId(categoryList.get(categorySpinner.getSelectedItemPosition()).getCategoryId());
            currentProduct.setBrandId(brandList.get(brandSpinner.getSelectedItemPosition()).getBrandId());
            currentProduct.setDescription(descriptionEditText.getText().toString());
            currentProduct.setImage(imagePath != null ? imagePath : currentProduct.getImage());

            if (currentProduct.getName().isEmpty()) {
                Toast.makeText(getContext(), "Product name is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct.getStock() < 0) {
                Toast.makeText(getContext(), "Stock must be greater than or equal to 0!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct.getImportPrice() <= 0) {
                Toast.makeText(getContext(), "Import price must be greater than 0!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct.getSellingPrice() <= 0 && currentProduct.getSellingPrice() < currentProduct.getImportPrice()) {
                Toast.makeText(getContext(), "Selling price must be greater than 0 and greater than import price!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct.getCategoryId() == null) {
                Toast.makeText(getContext(), "Category is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct.getBrandId() == null) {
                Toast.makeText(getContext(), "Brand is required!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct.getDescription().isEmpty()) {
                Toast.makeText(getContext(), "Description is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            productController.updateProduct(currentProduct);
            Toast.makeText(getContext(), "Product saved", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        if (currentProduct == null) return;

        if (currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()) {
            if (currentProduct.getImage().startsWith("/") || currentProduct.getImage().contains("/")) {
                File imageFile = new File(currentProduct.getImage());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
        }

        productController.deleteProduct(currentProduct);
        getParentFragmentManager().popBackStack();
    }
}