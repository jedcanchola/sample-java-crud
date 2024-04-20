package com.products.shoping;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.products.shoping.models.Product;
import com.products.shoping.models.ProductDto;
import com.products.shoping.services.ProductRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductsController {

  @Autowired
  private ProductRepository productRepository;

  @GetMapping("/create")
  public String createProduct(Model model) {
    ProductDto productDto = new ProductDto();
    model.addAttribute("productDto", productDto);
    return "products/create";
  }

  @GetMapping()
  public String getMethodName(Model model) {
    List<Product> products = productRepository.findAll();
    model.addAttribute("products", products);
    return "products/index";
  }

  @PostMapping("/create")
  public String saveProduct(
      @Valid @ModelAttribute ProductDto productDto,
      BindingResult result) {

    if (productDto.getImageFile().isEmpty()) {
      result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
    }

    if (result.hasErrors()) {
      System.err.println(result.getAllErrors());
    }

    // Save image file
    MultipartFile image = productDto.getImageFile();
    Date createdAt = new Date();
    String storageFileName = createdAt.getTime() + "" + image.getOriginalFilename();

    try {
      String uploadDir = "public/images/";
      Path uploadPath = Paths.get(uploadDir);

      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      try (InputStream inputStream = image.getInputStream()) {
        Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (Exception ex) {
      System.out.println("Exception: " + ex.getMessage());
    }

    Product product = new Product();
    product.setName(productDto.getName());
    product.setBrand(productDto.getBrand());
    product.setCategory(productDto.getCategory());
    product.setPrice(productDto.getPrice());
    product.setDescription(productDto.getDescription());
    product.setCreatedAt(createdAt);
    product.setImageFileName(storageFileName);

    productRepository.save(product);

    return "redirect:/products";
  }

  @GetMapping("/edit")
  public String editProduct(
      Model model,
      @RequestParam UUID id) {
    try {
      Product product = productRepository.findById(id).get();
      model.addAttribute("product", product);

      ProductDto productDto = new ProductDto();
      productDto.setName(product.getName());
      productDto.setBrand(product.getBrand());
      productDto.setCategory(product.getCategory());
      productDto.setPrice(product.getPrice());
      productDto.setDescription(product.getDescription());

      model.addAttribute("productDto", productDto);

    } catch (Exception ex) {
      System.out.println(("Exception: " + ex.getMessage()));
      return "redirect:/products";
    }
    return "products/edit";
  }

  @PostMapping("/edit")
  public String updateProduct(
      Model model,
      @RequestParam UUID id,
      @Valid @ModelAttribute ProductDto productDto,
      BindingResult result) {

    try {
      Product product = productRepository.findById(id).get();
      model.addAttribute("product", product);

      if (result.hasErrors()) {
        return "products/edit";
        // System.err.println(result.getAllErrors());
      }

      if (!productDto.getImageFile().isEmpty()) {
        // Delete old image before creating the new one
        String uploadDir = "public/images/";
        Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

        try {
          Files.delete((oldImagePath));
        } catch (Exception ex) {
          System.out.println(("Exception: " + ex.getMessage()));
        }

        // Save new image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try (InputStream inputStream = image.getInputStream()) {
          Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        product.setImageFileName(storageFileName);
      }

      product.setName(productDto.getName());
      product.setBrand(productDto.getBrand());
      product.setCategory(productDto.getCategory());
      product.setPrice(productDto.getPrice());
      product.setDescription(productDto.getDescription());

      productRepository.save(product);

    } catch (Exception ex) {
      System.out.println(("Exception: " + ex.getMessage()));
    }

    return "redirect:/products";
  }

}
