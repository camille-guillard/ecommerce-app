package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.CustomerOrder;
import com.ecommerce.domain.model.OrderEvent;
import com.ecommerce.domain.repository.OrderEventRepository;
import com.ecommerce.domain.model.OrderStatus;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.model.Promotion;
import com.ecommerce.domain.model.ProductTranslation;
import com.ecommerce.domain.repository.CategoryRepository;
import com.ecommerce.domain.repository.OrderRepository;
import com.ecommerce.domain.repository.ProductRepository;
import com.ecommerce.domain.repository.ProductTranslationRepository;
import com.ecommerce.domain.repository.PromotionRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.infrastructure.dto.OrderResponse;
import com.ecommerce.infrastructure.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;
    private final ProductRepository productRepository;
    private final ProductTranslationRepository productTranslationRepository;
    private final PromotionRepository promotionRepository;
    private final CategoryRepository categoryRepository;

    private static final Path UPLOAD_DIR = Paths.get("images/items");

    public AdminController(UserRepository userRepository, OrderRepository orderRepository,
                           OrderEventRepository orderEventRepository,
                           ProductRepository productRepository, ProductTranslationRepository productTranslationRepository,
                           PromotionRepository promotionRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
        this.productRepository = productRepository;
        this.productTranslationRepository = productTranslationRepository;
        this.promotionRepository = promotionRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users.map(u -> Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "firstName", u.getFirstName() != null ? u.getFirstName() : "",
                "lastName", u.getLastName() != null ? u.getLastName() : "",
                "city", u.getCity() != null ? u.getCity() : "",
                "roles", u.getRoles().stream().map(r -> r.getName()).toList()
        )));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<CustomerOrder> orders = orderRepository.findAll(pageable);
        return ResponseEntity.ok(orders.map(OrderResponse::fromEntity));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        CustomerOrder order = orderRepository.findById(id).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        String newStatus = body.get("status");
        if (newStatus == null) return ResponseEntity.badRequest().body("Missing status");

        OrderStatus current = order.getStatus();
        OrderStatus target;
        try {
            target = OrderStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + newStatus);
        }

        boolean valid = switch (current) {
            case CONFIRMED -> target == OrderStatus.IN_TRANSIT || target == OrderStatus.CANCELLED;
            case IN_TRANSIT -> target == OrderStatus.COMPLETED;
            default -> false;
        };

        if (!valid) {
            return ResponseEntity.badRequest().body("Cannot transition from " + current + " to " + target);
        }

        order.setStatus(target);
        orderRepository.save(order);
        orderEventRepository.save(new OrderEvent(order, target));
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@PageableDefault(size = 20) Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Map<Long, Promotion> promoMap = promotionRepository.findAllActive(LocalDate.now()).stream()
                .collect(java.util.stream.Collectors.toMap(p -> p.getProduct().getId(), p -> p, (a, b) -> a));
        return ResponseEntity.ok(products.map(p -> {
            Promotion promo = promoMap.get(p.getId());
            var resp = new java.util.LinkedHashMap<String, Object>();
            resp.put("id", p.getId());
            resp.put("displayName", p.getDisplayName());
            resp.put("description", p.getDescription());
            resp.put("price", p.getPrice());
            resp.put("stock", p.getStock());
            resp.put("imageUrl", p.getImageUrl());
            resp.put("categoryId", p.getCategory().getId());
            resp.put("categoryDisplayName", p.getCategory().getDisplayName());
            resp.put("releaseDate", p.getReleaseDate());
            resp.put("active", p.isActive());
            resp.put("discountPercent", promo != null ? promo.getDiscountPercent() : null);
            resp.put("promoStartDate", promo != null ? promo.getStartDate() : null);
            resp.put("promoEndDate", promo != null ? promo.getEndDate() : null);
            return resp;
        }));
    }

    @PutMapping("/products/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return ResponseEntity.notFound().build();

        String newDisplayName = body.containsKey("displayName") ? (String) body.get("displayName") : null;
        String newDescription = body.containsKey("description") ? (String) body.get("description") : null;

        if (newDisplayName != null) product.setDisplayName(newDisplayName);
        if (newDescription != null) product.setDescription(newDescription);
        if (body.containsKey("price")) product.setPrice(new BigDecimal(body.get("price").toString()));
        if (body.containsKey("stock")) {
            Object stockVal = body.get("stock");
            product.setStock(stockVal != null ? ((Number) stockVal).intValue() : null);
        }
        if (body.containsKey("imageUrl")) product.setImageUrl((String) body.get("imageUrl"));
        if (body.containsKey("categoryId")) {
            Long catId = Long.valueOf(body.get("categoryId").toString());
            Category cat = categoryRepository.findById(catId).orElse(null);
            if (cat != null) product.setCategory(cat);
        }

        productRepository.save(product);

        if (newDisplayName != null || newDescription != null) {
            for (String locale : List.of("fr", "en")) {
                productTranslationRepository.findByProductIdAndLocale(id, locale).ifPresent(pt -> {
                    if (newDisplayName != null) pt.setDisplayName(newDisplayName);
                    if (newDescription != null) pt.setDescription(newDescription);
                    productTranslationRepository.save(pt);
                });
            }
        }

        if (body.containsKey("discountPercent")) {
            Object discountVal = body.get("discountPercent");
            Promotion existing = promotionRepository.findByProductId(id).orElse(null);
            if (discountVal == null || discountVal.toString().isEmpty() || Double.parseDouble(discountVal.toString()) <= 0) {
                if (existing != null) promotionRepository.delete(existing);
            } else {
                BigDecimal discount = new BigDecimal(discountVal.toString());
                if (existing != null) {
                    existing.setDiscountPercent(discount);
                    if (body.containsKey("promoEndDate")) {
                        Object endVal = body.get("promoEndDate");
                        existing.setEndDate(endVal != null && !endVal.toString().isEmpty() ? LocalDate.parse(endVal.toString()) : null);
                    }
                    promotionRepository.save(existing);
                } else {
                    LocalDate endDate = null;
                    if (body.containsKey("promoEndDate") && body.get("promoEndDate") != null && !body.get("promoEndDate").toString().isEmpty()) {
                        endDate = LocalDate.parse(body.get("promoEndDate").toString());
                    }
                    promotionRepository.save(new Promotion(product, discount, LocalDate.now(), endDate));
                }
            }
        }

        return ResponseEntity.ok(ProductResponse.fromEntity(product, null));
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String displayName = (String) body.get("displayName");
        String description = (String) body.get("description");
        BigDecimal price = new BigDecimal(body.get("price").toString());
        Integer stock = body.get("stock") != null ? ((Number) body.get("stock")).intValue() : null;
        String imageUrl = (String) body.get("imageUrl");
        Long categoryId = Long.valueOf(body.get("categoryId").toString());

        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) return ResponseEntity.badRequest().body("Invalid category");

        Product product = new Product(
                name != null ? name : displayName.toLowerCase().replaceAll("[^a-z0-9]+", "-"),
                displayName, description, price, stock, imageUrl, category
        );

        if (body.containsKey("releaseDate") && body.get("releaseDate") != null) {
            product.setReleaseDate(LocalDate.parse(body.get("releaseDate").toString()));
        }

        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromEntity(product, null));
    }

    @PutMapping("/products/{id}/toggle-active")
    public ResponseEntity<?> toggleProductActive(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return ResponseEntity.notFound().build();

        product.setActive(!product.isActive());
        productRepository.save(product);
        return ResponseEntity.ok(ProductResponse.fromEntity(product, null));
    }

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body("Invalid file type. Allowed: JPEG, PNG, GIF, WebP, SVG");
        }

        try {
            Files.createDirectories(UPLOAD_DIR);
            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = UPLOAD_DIR.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("url", "/images/items/" + filename));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories.stream().map(c -> Map.of(
                "id", c.getId(),
                "name", c.getName(),
                "displayName", c.getDisplayName()
        )).toList());
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}
