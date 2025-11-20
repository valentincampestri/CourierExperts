package com.courierexperts.demo.util;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SeedDebug {

    private static final List<String> PRODUCT_NAMES = Arrays.asList(
            "Wireless Earbuds Pro",
            "CourierBook 14 Laptop",
            "Smartwatch Runner 2",
            "Sketch Tablet 11",
            "UltraWide Monitor 34",
            "Mirrorless Camera Z5",
            "Trail Sneakers X",
            "Flow Ergonomic Chair",
            "TravelPack Backpack",
            "WiFi 6 Router Plus",
            "Mini Drone Kit",
            "Neo Mechanical Keyboard"
    );

    private static final List<String> CARRIERS = Arrays.asList(
            "DHL Express",
            "FedEx",
            "UPS",
            "USPS",
            "Andreani",
            "Correo Argentino",
            "Mercado Envios"
    );

    private static final List<String> PACKAGE_NOTES = Arrays.asList(
            "Ready for pickup",
            "Arrived to warehouse",
            "Waiting for customs",
            "Consolidated shipment",
            "Address verified",
            "Queued for dispatch"
    );

    private static final List<String> PACKAGE_LOCATIONS = Arrays.asList(
            "Miami, FL",
            "New York, NY",
            "Los Angeles, CA",
            "Buenos Aires, AR",
            "Madrid, ES",
            "Santiago, CL",
            "CDMX, MX"
    );

    public static void seedNow(Context ctx) {
        try {
            // Only intended for debug builds; guarded by caller.
            final String uid = safeUid();
            if (uid == null || uid.isEmpty()) {
                Toast.makeText(ctx, "Seed: uid no disponible (inicia sesión)", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            WriteBatch batch = db.batch();

            Random rnd = new Random();
            List<PurchaseSeed> purchaseSeeds = new ArrayList<>();
            // Purchases distribution
            List<String> purchaseStatuses = new ArrayList<>();
            addTimes(purchaseStatuses, "PENDING", 3);
            addTimes(purchaseStatuses, "RECEIVED", 2);
            addTimes(purchaseStatuses, "SHIPPED", 2);
            addTimes(purchaseStatuses, "DELIVERED", 2);
            addTimes(purchaseStatuses, "CANCELLED", 1);
            Collections.shuffle(purchaseStatuses, rnd);

            List<String> stores = Arrays.asList("Amazon", "eBay", "Mercado Libre", "Ikea", "Apple", "Samsung", "Nike", "Adidas", "Sony", "Xiaomi");

            for (int i = 0; i < 10; i++) {
                String status = purchaseStatuses.get(i);
                DocumentReference doc = db.collection("users").document(uid)
                        .collection("purchases").document();

                String productName = PRODUCT_NAMES.get(i % PRODUCT_NAMES.size());
                String store = stores.get(i % stores.size());
                String carrier = CARRIERS.get(rnd.nextInt(CARRIERS.size()));
                double price = roundPrice(49 + rnd.nextInt(850) + rnd.nextDouble() * 10);
                String orderId = String.format(Locale.US, "ORD-%05d", 10000 + rnd.nextInt(90000));
                String description = String.format(Locale.getDefault(),
                        "%s purchased at %s via %s",
                        productName,
                        store,
                        carrier);

                Map<String, Object> m = new HashMap<>();
                m.put("id", doc.getId());
                m.put("productName", productName);
                m.put("storeName", store);
                m.put("carrierName", carrier);
                m.put("price", price);
                m.put("orderId", orderId);
                m.put("status", status);
                m.put("description", description);
                m.put("createdAt", FieldValue.serverTimestamp());
                m.put("thumbnailUrl", "https://picsum.photos/seed/purchase" + i + "/640/360");
                batch.set(doc, m);

                purchaseSeeds.add(new PurchaseSeed(doc, price, productName, store));
            }

            // Packages distribution
            List<String> packageStatuses = new ArrayList<>();
            addTimes(packageStatuses, "PENDING", 3);
            addTimes(packageStatuses, "IN_WAREHOUSE", 2);
            addTimes(packageStatuses, "IN_TRANSIT", 2);
            addTimes(packageStatuses, "DELIVERED", 2);
            addTimes(packageStatuses, "CANCELLED", 1);
            Collections.shuffle(packageStatuses, rnd);

            List<DocumentReference> packageDocs = new ArrayList<>();
            Map<String, Double> packagePriceMap = new HashMap<>();
            List<String> packageIdPool = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                DocumentReference doc = db.collection("users").document(uid)
                        .collection("packages").document();
                packageDocs.add(doc);
                packageIdPool.add(doc.getId());
                PurchaseSeed linkedPurchase = purchaseSeeds.isEmpty()
                        ? null
                        : purchaseSeeds.get(i % purchaseSeeds.size());
                String note = PACKAGE_NOTES.get(i % PACKAGE_NOTES.size());
                String location = PACKAGE_LOCATIONS.get(
                        (i + rnd.nextInt(PACKAGE_LOCATIONS.size())) % PACKAGE_LOCATIONS.size()
                );
                String labelBase = linkedPurchase != null ? linkedPurchase.productName : "Package";
                String label = labelBase + " #" + (i + 1);
                String description = linkedPurchase != null
                        ? String.format(Locale.getDefault(), "%s - %s (%s)", note, linkedPurchase.productName, location)
                        : String.format(Locale.getDefault(), "%s - %s", note, location);
                double packagePrice = linkedPurchase != null
                        ? roundPrice(linkedPurchase.price)
                        : roundPrice(40 + rnd.nextDouble() * 200);
                Map<String, Object> m = new HashMap<>();
                m.put("id", doc.getId());
                m.put("label", label);
                m.put("description", description);
                m.put("price", packagePrice);
                m.put("status", packageStatuses.get(i));
                m.put("lastUpdate", FieldValue.serverTimestamp());
                m.put("thumbnailUrl", "https://picsum.photos/seed/package" + i + "/640/360");
                if (linkedPurchase != null) {
                    m.put("purchaseFsId", linkedPurchase.doc.getId());
                }
                // shipmentId se setea luego para algunos
                batch.set(doc, m);
                packagePriceMap.put(doc.getId(), packagePrice);
            }

            // Shipments distribution
            List<String> shipmentStatuses = new ArrayList<>();
            addTimes(shipmentStatuses, "CREATED", 2);
            addTimes(shipmentStatuses, "IN_TRANSIT", 3);
            addTimes(shipmentStatuses, "OUT_FOR_DELIVERY", 2);
            addTimes(shipmentStatuses, "DELIVERED", 2);
            addTimes(shipmentStatuses, "CANCELLED", 1);
            Collections.shuffle(shipmentStatuses, rnd);

            int shipmentCount = Math.min(5, packageDocs.size());
            List<List<String>> shipmentPackages = allocatePackagesForShipments(packageIdPool, shipmentCount, rnd);

            for (int i = 0; i < shipmentCount; i++) {
                DocumentReference doc = db.collection("users").document(uid)
                        .collection("shipments").document();
                List<String> pkgIds = shipmentPackages.get(i);
                Map<String, Object> m = new HashMap<>();
                String id = doc.getId();
                m.put("id", id);
                m.put("title", "Envio " + id.substring(0, Math.min(6, id.length())));
                m.put("trackingNumber", String.format(Locale.US, "TRK-%06d", 100000 + rnd.nextInt(900000)));
                m.put("status", shipmentStatuses.get(i % shipmentStatuses.size()));
                m.put("lastUpdate", FieldValue.serverTimestamp());
                m.put("thumbnailUrl", "https://picsum.photos/seed/shipment" + i + "/640/360");
                m.put("packageIds", pkgIds);
                double packagesTotal = 0d;
                for (String pid : pkgIds) {
                    Double price = packagePriceMap.get(pid);
                    if (price != null) packagesTotal += price;
                }
                m.put("cost", roundPrice(packagesTotal * 0.2d));
                batch.set(doc, m);

                for (String pid : pkgIds) {
                    for (DocumentReference pd : packageDocs) {
                        if (pd.getId().equals(pid)) {
                            Map<String, Object> upd = new HashMap<>();
                            upd.put("shipmentId", id);
                            batch.update(pd, upd);
                            break;
                        }
                    }
                }
            }
            Toast.makeText(ctx, "Seed: creando 30 documentos...", Toast.LENGTH_SHORT).show();
            batch.commit()
                    .addOnSuccessListener(v -> Toast.makeText(ctx, "Seed completado", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ctx, "Seed falló: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            Toast.makeText(ctx, "Seed error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static void addTimes(List<String> list, String val, int times) {
        for (int i = 0; i < times; i++) list.add(val);
    }

    private static double roundPrice(double value) {
        return Math.round(value * 100d) / 100d;
    }

    private static List<List<String>> allocatePackagesForShipments(List<String> packageIds, int shipmentCount, Random rnd) {
        List<List<String>> result = new ArrayList<>();
        if (shipmentCount <= 0 || packageIds == null || packageIds.isEmpty()) {
            return result;
        }

        List<String> pool = new ArrayList<>(packageIds);
        Collections.shuffle(pool, rnd);

        int cursor = 0;
        for (int i = 0; i < shipmentCount; i++) {
            int remainingShipments = shipmentCount - i;
            int remainingPackages = pool.size() - cursor;
            int maxForThis = Math.max(1, remainingPackages - (remainingShipments - 1));
            int take = maxForThis > 1 ? 1 + rnd.nextInt(maxForThis) : 1;

            List<String> subset = new ArrayList<>();
            for (int j = 0; j < take && cursor < pool.size(); j++) {
                subset.add(pool.get(cursor++));
            }
            if (subset.isEmpty() && cursor < pool.size()) {
                subset.add(pool.get(cursor++));
            }
            result.add(subset);
        }
        return result;
    }

    private static String safeUid() {
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null || uid.isEmpty()) return null;
            return uid;
        } catch (Exception e) { return null; }
    }

    private static final class PurchaseSeed {
        final DocumentReference doc;
        final double price;
        final String productName;
        final String storeName;

        PurchaseSeed(DocumentReference doc, double price, String productName, String storeName) {
            this.doc = doc;
            this.price = price;
            this.productName = productName;
            this.storeName = storeName;
        }
    }
}
