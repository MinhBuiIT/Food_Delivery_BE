package com.dev.enums;

public enum OrderStatus {
    PENDING(0),
    CONFIRMED(1),
    SHIPPING(2),
    DELIVERED(3),
    CANCELLED(4);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }
    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }

    public int getValue() {
        return value;
    }
}
