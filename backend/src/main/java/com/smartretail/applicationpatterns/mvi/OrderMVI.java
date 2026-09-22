package com.smartretail.applicationpatterns.mvi;

import com.smartretail.dto.CheckoutDTOs;
import com.smartretail.entity.enums.OrderStatus;

import java.util.Collections;
import java.util.List;

/**
 * MVI PATTERN - Model-View-Intent Order Management Flow
 * Unidirectional reactive data flow:
 * User Action -> Intent -> Reducer/Processor -> Immutable State -> View
 */
public class OrderMVI {

    // 1. INTENTS (User Actions as Immutable Intent Objects)
    public sealed interface OrderIntent permits
            OrderIntent.LoadOrders,
            OrderIntent.FilterByStatus,
            OrderIntent.SelectOrder,
            OrderIntent.CancelOrder {

        record LoadOrders(Long userId) implements OrderIntent {}
        record FilterByStatus(OrderStatus filterStatus) implements OrderIntent {}
        record SelectOrder(Long orderId) implements OrderIntent {}
        record CancelOrder(Long orderId) implements OrderIntent {}
    }

    // 2. IMMUTABLE VIEW STATE
    public static class OrderViewState {
        private final boolean isLoading;
        private final List<CheckoutDTOs.OrderDTO> orders;
        private final List<CheckoutDTOs.OrderDTO> filteredOrders;
        private final CheckoutDTOs.OrderDTO selectedOrder;
        private final OrderStatus activeFilter;
        private final String errorMessage;

        public OrderViewState(boolean isLoading,
                              List<CheckoutDTOs.OrderDTO> orders,
                              List<CheckoutDTOs.OrderDTO> filteredOrders,
                              CheckoutDTOs.OrderDTO selectedOrder,
                              OrderStatus activeFilter,
                              String errorMessage) {
            this.isLoading = isLoading;
            this.orders = orders != null ? orders : Collections.emptyList();
            this.filteredOrders = filteredOrders != null ? filteredOrders : Collections.emptyList();
            this.selectedOrder = selectedOrder;
            this.activeFilter = activeFilter;
            this.errorMessage = errorMessage;
        }

        public static OrderViewState initial() {
            return new OrderViewState(false, Collections.emptyList(), Collections.emptyList(), null, null, null);
        }

        public boolean isLoading() { return isLoading; }
        public List<CheckoutDTOs.OrderDTO> getOrders() { return orders; }
        public List<CheckoutDTOs.OrderDTO> getFilteredOrders() { return filteredOrders; }
        public CheckoutDTOs.OrderDTO getSelectedOrder() { return selectedOrder; }
        public OrderStatus getActiveFilter() { return activeFilter; }
        public String getErrorMessage() { return errorMessage; }

        public OrderViewState withLoading(boolean loading) {
            return new OrderViewState(loading, this.orders, this.filteredOrders, this.selectedOrder, this.activeFilter, this.errorMessage);
        }

        public OrderViewState withOrders(List<CheckoutDTOs.OrderDTO> newOrders) {
            return new OrderViewState(false, newOrders, newOrders, this.selectedOrder, this.activeFilter, null);
        }

        public OrderViewState withFilter(OrderStatus filter, List<CheckoutDTOs.OrderDTO> filtered) {
            return new OrderViewState(this.isLoading, this.orders, filtered, this.selectedOrder, filter, this.errorMessage);
        }

        public OrderViewState withSelected(CheckoutDTOs.OrderDTO selected) {
            return new OrderViewState(this.isLoading, this.orders, this.filteredOrders, selected, this.activeFilter, this.errorMessage);
        }
    }

    // 3. REDUCER / PROCESSOR
    public static class OrderMviProcessor {

        public static OrderViewState reduce(OrderViewState currentState, OrderIntent intent, List<CheckoutDTOs.OrderDTO> freshOrders) {
            if (intent instanceof OrderIntent.LoadOrders) {
                return currentState.withOrders(freshOrders);
            } else if (intent instanceof OrderIntent.FilterByStatus filterIntent) {
                List<CheckoutDTOs.OrderDTO> filtered = filterIntent.filterStatus() == null ?
                        currentState.getOrders() :
                        currentState.getOrders().stream()
                                .filter(o -> o.getStatus() == filterIntent.filterStatus())
                                .toList();

                return currentState.withFilter(filterIntent.filterStatus(), filtered);
            } else if (intent instanceof OrderIntent.SelectOrder selectIntent) {
                CheckoutDTOs.OrderDTO selected = currentState.getOrders().stream()
                        .filter(o -> o.getId().equals(selectIntent.orderId()))
                        .findFirst()
                        .orElse(null);

                return currentState.withSelected(selected);
            }
            return currentState;
        }
    }
}
