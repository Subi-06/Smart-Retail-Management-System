package com.smartretail.designpatterns.behavioral.chain;

import org.springframework.stereotype.Component;

@Component
public class PurchaseValidationPipeline {

    public PurchaseValidationHandler buildValidationChain() {
        PurchaseValidationHandler stockValidator = new StockValidationHandler();
        PurchaseValidationHandler membershipValidator = new MembershipValidationHandler();
        PurchaseValidationHandler couponValidator = new CouponValidationHandler();
        PurchaseValidationHandler largeOrderValidator = new LargeOrderValidationHandler();
        PurchaseValidationHandler approvalHandler = new ApprovalHandler();

        stockValidator
            .setNext(membershipValidator)
            .setNext(couponValidator)
            .setNext(largeOrderValidator)
            .setNext(approvalHandler);

        return stockValidator;
    }
}
