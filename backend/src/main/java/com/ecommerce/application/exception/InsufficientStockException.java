package com.ecommerce.application.exception;

public class InsufficientStockException extends RuntimeException {

    private final String productName;
    private final int availableStock;
    private final int requestedQuantity;

    public InsufficientStockException(String productName, int availableStock, int requestedQuantity) {
        super("Insufficient stock for " + productName + ": requested " + requestedQuantity + ", available " + availableStock);
        this.productName = productName;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public String getProductName() { return productName; }
    public int getAvailableStock() { return availableStock; }
    public int getRequestedQuantity() { return requestedQuantity; }
}
