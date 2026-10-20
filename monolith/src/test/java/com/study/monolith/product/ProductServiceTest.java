package com.study.monolith.product;

import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductServiceTest {


    private ProductService productService;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepositoryImpl();
        productService = new ProductService(productRepository);
    }

    @Test
    void 상품생성() {
        AddProductRequest request = 상품생성_요청();

        long l = productService.createProduct(request);

        Product product = productService.getProduct(l);

        Assertions.assertThat(product.getPrice()).isEqualTo(request.price());

    }

    private static @NonNull AddProductRequest 상품생성_요청() {
        final int price = 1000;
        final int quantity = 10;
        return new AddProductRequest(price, quantity);
    }

    @Test
    void 재고차감() {
        AddProductRequest request = 상품생성_요청();
        long productId = productService.createProduct(request);

        int quantity = 2;

        productService.decreaseStock(productId, quantity);

        Product product = productService.getProduct(productId);

        Assertions.assertThat(product.getQuantity()).isEqualTo(request.quantity() - quantity);
    }

    @Test
    void 재고차감_예외() {
        AddProductRequest request = 상품생성_요청();
        long productId = productService.createProduct(request);

        int quantity = request.quantity() + 1;

        Assertions.assertThatThrownBy(() -> productService.decreaseStock(productId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");

        Product product = productService.getProduct(productId);

        Assertions.assertThat(product.getQuantity()).isEqualTo(request.quantity());
    }

    private class ProductService {

        private final ProductRepository productRepository;

        private ProductService(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        public long createProduct(AddProductRequest request) {
            Product product = Product.create(request.price(), request.quantity());
            return productRepository.save(product);
        }

        public Product getProduct(long id) {
            return productRepository.findById(id);
        }

        public void decreaseStock(long productId, int quantity) {
            Product product = productRepository.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("해당 상품은 존재하지 않습니다.");
            }

            product.decreaseStock(quantity);
        }
    }

    private record AddProductRequest(int price, int quantity) {

    }

    private interface ProductRepository {

        long save(Product product);

        Product findById(long id);
    }

    private class ProductRepositoryImpl implements ProductRepository {

        private long sequence;
        private Map<Long, Product> map = new HashMap<>();
        ;

        @Override
        public long save(Product product) {
            map.put(++sequence, product);

            product.setId(sequence);
            return sequence;
        }

        @Override
        public Product findById(long id) {
            return map.get(id);
        }
    }

    private static class Product {

        private long id;

        private final int price;

        private int quantity;

        private Product(int price, int quantity) {
            this.price = price;
            this.quantity = quantity;
        }


        private static Product create(int price, int quantity) {
            return new Product(price, quantity);
        }

        public void setId(long sequence) {
            this.id = sequence;
        }

        public int getPrice() {
            return this.price;
        }

        public int getQuantity() {
            return this.quantity;
        }

        public void decreaseStock(int quantity) {
            if (this.quantity < quantity) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            this.quantity -= quantity;
        }
    }
}
