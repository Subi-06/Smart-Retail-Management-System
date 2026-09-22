package com.smartretail.designpatterns.structural.proxy;

import com.smartretail.entity.Product;
import com.smartretail.entity.User;
import com.smartretail.entity.enums.Role;
import com.smartretail.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * PROXY PATTERN - Protection Proxy
 * Intercepts calls to verify administrative role authorization and logs audit events.
 * Rejects non-admin attempts with 403 Forbidden (UnauthorizedException).
 */
@Component
public class AdminInventoryProxy implements ProductAdminOperations {

    private static final Logger log = LoggerFactory.getLogger(AdminInventoryProxy.class);

    @Autowired
    private RealProductAdminService realService;

    private void checkAdminAccess(User actor, String operation) {
        if (actor == null) {
            log.warn("SECURITY AUDIT ALERT: Unauthenticated attempt to perform [{}]", operation);
            throw new UnauthorizedException("Access Denied: You must be logged in to perform " + operation);
        }
        if (actor.getRole() != Role.ROLE_ADMIN) {
            log.warn("SECURITY AUDIT ALERT: User [{}] with role [{}] attempted unauthorized [{}] on inventory",
                    actor.getEmail(), actor.getRole(), operation);
            throw new UnauthorizedException("403 Forbidden: Only administrators have permission to perform " + operation);
        }
        log.info("SECURITY AUDIT: Admin [{}] authorized for operation [{}]", actor.getEmail(), operation);
    }

    @Override
    public Product updateStock(Long productId, int newQuantity, User actor) {
        checkAdminAccess(actor, "UPDATE_STOCK");
        return realService.updateStock(productId, newQuantity, actor);
    }

    @Override
    public Product changePrice(Long productId, BigDecimal newPrice, User actor) {
        checkAdminAccess(actor, "CHANGE_PRICE");
        return realService.changePrice(productId, newPrice, actor);
    }

    @Override
    public void deleteProduct(Long productId, User actor) {
        checkAdminAccess(actor, "DELETE_PRODUCT");
        realService.deleteProduct(productId, actor);
    }
}
