package com.smartretail.applicationpatterns.viper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * VIPER PATTERN - View-Interactor-Presenter-Entity-Router
 * Enterprise clean architecture pattern separating:
 * - View: Renders formatted UI representation
 * - Interactor: Pure business logic & data operations
 * - Presenter: Bridges UI user actions with business interactor
 * - Entity: Plain data structures
 * - Router: Navigation wireframe transitions
 */
public class InventoryVIPER {

    // 1. ENTITY
    @Getter
    @Setter
    @Builder
    public static class InventoryItemEntity {
        private Long id;
        private String name;
        private String category;
        private int stock;
        private int minThreshold;
        private BigDecimal unitPrice;
    }

    // 2. VIEW CONTRACT
    public interface InventoryViewContract {
        void showLoading();
        void hideLoading();
        void displayInventoryViewModel(List<InventoryItemViewModel> viewModels);
        void showLowStockBadgeAlert(String productName, int stockLeft);
    }

    @Getter
    @Builder
    public static class InventoryItemViewModel {
        private Long id;
        private String displayName;
        private String categoryTag;
        private String stockBadgeClass; // "badge-green", "badge-yellow", "badge-red"
        private String stockText;
        private String formattedPrice;
    }

    // 3. INTERACTOR CONTRACT & IMPLEMENTATION
    public interface InventoryInteractorContract {
        List<InventoryItemEntity> fetchInventoryData();
        InventoryItemEntity updateStockLevel(Long id, int newStock);
    }

    // 4. ROUTER CONTRACT
    public interface InventoryRouterContract {
        void navigateToProductDetails(Long productId);
        void navigateToSupplierPurchaseOrder(Long productId);
        void navigateToInventoryAuditLog();
    }

    // 5. PRESENTER
    public static class InventoryPresenter {
        private final InventoryViewContract view;
        private final InventoryInteractorContract interactor;
        private final InventoryRouterContract router;

        public InventoryPresenter(InventoryViewContract view,
                                  InventoryInteractorContract interactor,
                                  InventoryRouterContract router) {
            this.view = view;
            this.interactor = interactor;
            this.router = router;
        }

        public void loadInventory() {
            view.showLoading();
            List<InventoryItemEntity> entities = interactor.fetchInventoryData();
            List<InventoryItemViewModel> vms = new ArrayList<>();

            for (InventoryItemEntity entity : entities) {
                String badge;
                String text;
                if (entity.getStock() <= 0) {
                    badge = "badge-red";
                    text = "Out of Stock";
                } else if (entity.getStock() <= entity.getMinThreshold()) {
                    badge = "badge-yellow";
                    text = "Low Stock (" + entity.getStock() + " left)";
                    view.showLowStockBadgeAlert(entity.getName(), entity.getStock());
                } else {
                    badge = "badge-green";
                    text = "In Stock (" + entity.getStock() + ")";
                }

                vms.add(InventoryItemViewModel.builder()
                        .id(entity.getId())
                        .displayName(entity.getName())
                        .categoryTag(entity.getCategory())
                        .stockBadgeClass(badge)
                        .stockText(text)
                        .formattedPrice("₹" + entity.getUnitPrice())
                        .build());
            }

            view.displayInventoryViewModel(vms);
            view.hideLoading();
        }

        public void onItemClicked(Long productId) {
            router.navigateToProductDetails(productId);
        }

        public void onReorderClicked(Long productId) {
            router.navigateToSupplierPurchaseOrder(productId);
        }
    }
}
