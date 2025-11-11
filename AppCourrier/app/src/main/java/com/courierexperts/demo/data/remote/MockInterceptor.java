package com.courierexperts.demo.data.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String path = req.url().encodedPath();

        String body = "{}";
        int code = 200;

        if ("/purchases".equals(path)) {
            body = "[{" +
                    "\"id\":1," +
                    "\"storeName\":\"Amazon\"," +
                    "\"orderId\":\"AMZ-12345\"," +
                    "\"status\":\"pending\"," +
                    "\"createdAt\":\"2025-11-01T10:00:00Z\"," +
                    "\"thumbnailUrl\":\"https://picsum.photos/seed/amazon/96/96\"" +
                    "},{" +
                    "\"id\":2," +
                    "\"storeName\":\"eBay\"," +
                    "\"orderId\":\"EBY-7788\"," +
                    "\"status\":\"received\"," +
                    "\"createdAt\":\"2025-10-28T16:40:00Z\"," +
                    "\"thumbnailUrl\":\"https://picsum.photos/seed/ebay/96/96\"" +
                    "}]";
        } else if ("/packages".equals(path)) {
            body = "[{" +
                    "\"id\":101," +
                    "\"label\":\"Paquete 1\"," +
                    "\"description\":\"Caja mediana con ropa\"," +
                    "\"status\":\"en_deposito\"," +
                    "\"lastUpdate\":\"2025-11-07T12:30:00Z\"," +
                    "\"thumbnailUrl\":\"https://picsum.photos/seed/pack1/96/96\"" +
                    "},{" +
                    "\"id\":102," +
                    "\"label\":\"Paquete 2\"," +
                    "\"description\":\"Electrónica - accesorio\"," +
                    "\"status\":\"listo\"," +
                    "\"lastUpdate\":\"2025-11-08T09:15:00Z\"," +
                    "\"thumbnailUrl\":\"https://picsum.photos/seed/pack2/96/96\"" +
                    "}]";
        } else if ("/shipments".equals(path)) {
            body = "[{" +
                    "\"id\":5," +
                    "\"title\":\"Envío 5\"," +
                    "\"trackingNumber\":\"12345\"," +
                    "\"status\":\"en_transito\"," +
                    "\"lastUpdate\":\"2025-11-09T18:00:00Z\"," +
                    "\"thumbnailUrl\":\"https://picsum.photos/seed/ship1/96/96\"" +
                    "},{" +
                    "\"id\":4," +
                    "\"title\":\"Envío 4\"," +
                    "\"trackingNumber\":\"67890\"," +
                    "\"status\":\"entregado\"," +
                    "\"lastUpdate\":\"2025-11-06T11:20:00Z\"," +
                    "\"thumbnailUrl\":\"https://picsum.photos/seed/ship2/96/96\"" +
                    "}]";
        }

        return new Response.Builder()
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("OK")
                .body(ResponseBody.create(body, JSON))
                .build();
    }
}
