package com.multitenant.demo.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantContext {
    private static ThreadLocal<String> activeTenant = new InheritableThreadLocal<>();

    public static void setActiveTenant(String tenant) {
        activeTenant.set(tenant);
    }

    public static String getActiveTenant() {
        return activeTenant.get();
    }

    public static void clearActiveTenant() {
        activeTenant.remove();
    }
}
