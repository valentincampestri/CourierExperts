package com.courierexperts.demo.domain;

/** Centraliza enums y mapeos de estado (Firestore ⇄ UI). */
public final class StatusMapper {

    private StatusMapper() {}

    // Purchases
    public enum PurchaseStatus { PENDING, RECEIVED, SHIPPED, DELIVERED, CANCELLED }

    public static PurchaseStatus purchaseFrom(String raw) {
        if (raw == null) return PurchaseStatus.PENDING;
        switch (raw.toUpperCase()) {
            case "RECEIVED": return PurchaseStatus.RECEIVED;
            case "SHIPPED": return PurchaseStatus.SHIPPED;
            case "DELIVERED": return PurchaseStatus.DELIVERED;
            case "CANCELLED": return PurchaseStatus.CANCELLED;
            case "PENDING":
            default: return PurchaseStatus.PENDING;
        }
    }

    public static String labelPurchase(String raw) { return label(purchaseFrom(raw)); }
    public static String label(PurchaseStatus s) {
        switch (s) {
            case RECEIVED: return "Recibida";
            case SHIPPED: return "Despachada";
            case DELIVERED: return "Entregada";
            case CANCELLED: return "Cancelada";
            case PENDING:
            default: return "Pendiente";
        }
    }

    // Packages
    public enum PackageStatus { PENDING, IN_WAREHOUSE, IN_TRANSIT, DELIVERED, CANCELLED }

    public static PackageStatus packageFrom(String raw) {
        if (raw == null) return PackageStatus.PENDING;
        String r = raw.toUpperCase();
        // Compat con valores antiguos en español
        if ("EN_DEPOSITO".equals(r)) r = "IN_WAREHOUSE";
        switch (r) {
            case "IN_WAREHOUSE": return PackageStatus.IN_WAREHOUSE;
            case "IN_TRANSIT": return PackageStatus.IN_TRANSIT;
            case "DELIVERED": return PackageStatus.DELIVERED;
            case "CANCELLED": return PackageStatus.CANCELLED;
            case "PENDING":
            default: return PackageStatus.PENDING;
        }
    }

    public static String labelPackage(String raw) { return label(packageFrom(raw)); }
    public static String label(PackageStatus s) {
        switch (s) {
            case IN_WAREHOUSE: return "En depósito";
            case IN_TRANSIT: return "En tránsito";
            case DELIVERED: return "Entregado";
            case CANCELLED: return "Cancelado";
            case PENDING:
            default: return "Pendiente";
        }
    }

    // Shipments
    public enum ShipmentStatus { CREATED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED }

    public static ShipmentStatus shipmentFrom(String raw) {
        if (raw == null) return ShipmentStatus.CREATED;
        String r = raw.toUpperCase();
        // Compat con valores antiguos
        if ("EN_TRANSITO".equals(r)) r = "IN_TRANSIT";
        switch (r) {
            case "IN_TRANSIT": return ShipmentStatus.IN_TRANSIT;
            case "OUT_FOR_DELIVERY": return ShipmentStatus.OUT_FOR_DELIVERY;
            case "DELIVERED": return ShipmentStatus.DELIVERED;
            case "CANCELLED": return ShipmentStatus.CANCELLED;
            case "CREATED":
            default: return ShipmentStatus.CREATED;
        }
    }

    public static String labelShipment(String raw) { return label(shipmentFrom(raw)); }
    public static String label(ShipmentStatus s) {
        switch (s) {
            case IN_TRANSIT: return "En tránsito";
            case OUT_FOR_DELIVERY: return "En reparto";
            case DELIVERED: return "Entregado";
            case CANCELLED: return "Cancelado";
            case CREATED:
            default: return "Creado";
        }
    }
}

