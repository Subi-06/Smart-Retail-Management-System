package com.smartretail.applicationpatterns.mvp;

import com.smartretail.entity.Discount;
import com.smartretail.entity.enums.DiscountType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * MVP PATTERN - Discount Management Example
 * Clear separation: Model (business data), View Contract (UI actions), Presenter (interaction logic).
 */
public class DiscountMVP {

    // 1. MODEL
    @Getter
    @Setter
    @Builder
    public static class DiscountModel {
        private Long id;
        private String code;
        private String name;
        private DiscountType type;
        private BigDecimal value;
        private boolean isPercentage;
        private BigDecimal minSpend;
        private boolean active;

        public static DiscountModel fromEntity(Discount entity) {
            return DiscountModel.builder()
                    .id(entity.getId())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .type(entity.getType())
                    .value(entity.getValue())
                    .isPercentage(Boolean.TRUE.equals(entity.getIsPercentage()))
                    .minSpend(entity.getMinSpend())
                    .active(Boolean.TRUE.equals(entity.getActive()))
                    .build();
        }
    }

    // 2. VIEW CONTRACT
    public interface DiscountViewContract {
        void showLoading();
        void hideLoading();
        void displayDiscounts(List<DiscountModel> discounts);
        void showDiscountAppliedSuccess(String discountCode, BigDecimal savingAmount);
        void showError(String errorMessage);
    }

    // 3. PRESENTER
    public static class DiscountPresenter {
        private final DiscountViewContract view;
        private final List<DiscountModel> inMemoryDiscounts = new ArrayList<>();

        public DiscountPresenter(DiscountViewContract view) {
            this.view = view;
        }

        public void loadDiscounts(List<Discount> rawEntities) {
            view.showLoading();
            inMemoryDiscounts.clear();
            for (Discount d : rawEntities) {
                inMemoryDiscounts.add(DiscountModel.fromEntity(d));
            }
            view.displayDiscounts(inMemoryDiscounts);
            view.hideLoading();
        }

        public void applyDiscountCode(String code, BigDecimal cartSubtotal) {
            view.showLoading();
            DiscountModel match = inMemoryDiscounts.stream()
                    .filter(d -> d.getCode().equalsIgnoreCase(code) && d.isActive())
                    .findFirst()
                    .orElse(null);

            if (match == null) {
                view.showError("Discount code '" + code + "' is invalid or inactive.");
                view.hideLoading();
                return;
            }

            if (match.getMinSpend() != null && cartSubtotal.compareTo(match.getMinSpend()) < 0) {
                view.showError("Coupon requires minimum purchase of ₹" + match.getMinSpend());
                view.hideLoading();
                return;
            }

            BigDecimal savings;
            if (match.isPercentage()) {
                savings = cartSubtotal.multiply(match.getValue()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            } else {
                savings = match.getValue().min(cartSubtotal);
            }

            view.showDiscountAppliedSuccess(match.getCode(), savings);
            view.hideLoading();
        }
    }
}
