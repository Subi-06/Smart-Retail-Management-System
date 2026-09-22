package com.smartretail.designpatterns.creational.singleton;

import java.math.BigDecimal;

/**
 * SINGLETON PATTERN
 * Thread-safe Bill Pugh Singleton for Store-wide Global Configuration.
 */
public class StoreConfiguration {

    private String storeName = "SMART RETAIL";
    private String currency = "₹";
    private BigDecimal defaultTaxRate = new BigDecimal("0.05"); // 5%
    private String supportEmail = "support@smartretail.com";
    private String storeAddress = "104 Tech Boulevard, Silicon Galleria, Retail Hub";

    private StoreConfiguration() {
        // Private constructor prevents instantiation
    }

    private static class HelperHolder {
        private static final StoreConfiguration INSTANCE = new StoreConfiguration();
    }

    public static StoreConfiguration getInstance() {
        return HelperHolder.INSTANCE;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getDefaultTaxRate() {
        return defaultTaxRate;
    }

    public void setDefaultTaxRate(BigDecimal defaultTaxRate) {
        this.defaultTaxRate = defaultTaxRate;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }
}
