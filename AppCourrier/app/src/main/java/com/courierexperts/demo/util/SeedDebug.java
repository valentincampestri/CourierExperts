package com.courierexperts.demo.util;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
                com.google.firebase.firestore.DocumentReference doc = db.collection("users").document(uid)
                        .collection("purchases").document();
                Map<String, Object> m = new HashMap<>();
                m.put("id", doc.getId());
                m.put("storeName", stores.get(i % stores.size()));
                m.put("orderId", String.format(Locale.US, "ORD-%05d", 10000 + rnd.nextInt(90000)));
                m.put("status", status);
                m.put("createdAt", FieldValue.serverTimestamp());
                m.put("thumbnailUrl", "https://picsum.photos/seed/purchase" + i + "/96/96");
                batch.set(doc, m);
            }

            // Packages distribution
            List<String> packageStatuses = new ArrayList<>();
            addTimes(packageStatuses, "PENDING", 3);
            addTimes(packageStatuses, "IN_WAREHOUSE", 2);
            addTimes(packageStatuses, "IN_TRANSIT", 2);
            addTimes(packageStatuses, "DELIVERED", 2);
            addTimes(packageStatuses, "CANCELLED", 1);
            Collections.shuffle(packageStatuses, rnd);

            List<com.google.firebase.firestore.DocumentReference> packageDocs = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                com.google.firebase.firestore.DocumentReference doc = db.collection("users").document(uid)
                        .collection("packages").document();
                packageDocs.add(doc);
                Map<String, Object> m = new HashMap<>();
                m.put("id", doc.getId());
                m.put("label", "Paquete " + (i + 1));
                m.put("description", "Contenido ejemplo " + (i + 1));
                m.put("status", packageStatuses.get(i));
                m.put("lastUpdate", FieldValue.serverTimestamp());
                m.put("thumbnailUrl", "https://picsum.photos/seed/package" + i + "/96/96");
                // shipmentId se setea luego para algunos
                batch.set(doc, m);
            }

            // Shipments distribution
            List<String> shipmentStatuses = new ArrayList<>();
            addTimes(shipmentStatuses, "CREATED", 2);
            addTimes(shipmentStatuses, "IN_TRANSIT", 3);
            addTimes(shipmentStatuses, "OUT_FOR_DELIVERY", 2);
            addTimes(shipmentStatuses, "DELIVERED", 2);
            addTimes(shipmentStatuses, "CANCELLED", 1);
            Collections.shuffle(shipmentStatuses, rnd);

            List<com.google.firebase.firestore.DocumentReference> shipmentDocs = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                com.google.firebase.firestore.DocumentReference doc = db.collection("users").document(uid)
                        .collection("shipments").document();
                shipmentDocs.add(doc);
                List<String> pkgIds = pickPackageIds(packageDocs, rnd);
                Map<String, Object> m = new HashMap<>();
                String id = doc.getId();
                m.put("id", id);
                m.put("title", "Envío " + id.substring(0, Math.min(6, id.length())));
                m.put("trackingNumber", String.format(Locale.US, "TRK-%06d", 100000 + rnd.nextInt(900000)));
                m.put("status", shipmentStatuses.get(i));
                m.put("lastUpdate", FieldValue.serverTimestamp());
                m.put("thumbnailUrl", "https://picsum.photos/seed/shipment" + i + "/96/96");
                m.put("packageIds", pkgIds);
                batch.set(doc, m);

                // For each package in pkgIds, set shipmentId
                for (String pid : pkgIds) {
                    for (com.google.firebase.firestore.DocumentReference pd : packageDocs) {
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

    private static List<String> pickPackageIds(List<com.google.firebase.firestore.DocumentReference> pkgs, Random rnd) {
        // pick 0-3 package ids for a shipment
        int count = rnd.nextInt(4); // 0..3
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int idx = rnd.nextInt(pkgs.size());
            String id = pkgs.get(idx).getId();
            if (!ids.contains(id)) ids.add(id);
        }
        return ids;
    }

    private static String safeUid() {
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null || uid.isEmpty()) return null;
            return uid;
        } catch (Exception e) { return null; }
    }
}

