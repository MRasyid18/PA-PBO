package controllers;

import com.sun.net.httpserver.HttpExchange;
import database.MemoryStore;
import models.Room;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * RoomController — Menangani semua request CRUD untuk endpoint /api/rooms
 * Menerapkan Polymorphism: metode handle() berbeda perilakunya sesuai HTTP method.
 */
public class RoomController {

    public static void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath(); // e.g. /api/rooms atau /api/rooms/1

        // Tambahkan CORS headers agar bisa diakses oleh frontend di Live Server
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if (method.equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Ambil ID dari path (jika ada), contoh: /api/rooms/3 → id = 3
        Integer id = parseId(path);

        switch (method) {
            case "GET"    -> handleGet(exchange, id);
            case "POST"   -> handlePost(exchange);
            case "PUT"    -> handlePut(exchange, id);
            case "DELETE" -> handleDelete(exchange, id);
            default       -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private static void handleGet(HttpExchange ex, Integer id) throws IOException {
        if (id == null) {
            // GET /api/rooms → kembalikan semua kamar
            ArrayList<Room> all = MemoryStore.getAllRooms();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toJson());
                if (i < all.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendResponse(ex, 200, sb.toString());
        } else {
            // GET /api/rooms/{id}
            Room r = MemoryStore.getRoomById(id);
            if (r == null) sendResponse(ex, 404, "{\"error\":\"Kamar tidak ditemukan\"}");
            else           sendResponse(ex, 200, r.toJson());
        }
    }

    private static void handlePost(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            String nomorKamar = parseJsonField(body, "nomorKamar");
            String tipe       = parseJsonField(body, "tipe");
            long   harga      = Long.parseLong(parseJsonField(body, "harga"));
            String status     = parseJsonField(body, "status");

            Room created = MemoryStore.addRoom(nomorKamar, tipe, harga, status);
            sendResponse(ex, 201, created.toJson());
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handlePut(HttpExchange ex, Integer id) throws IOException {
        if (id == null) { sendResponse(ex, 400, "{\"error\":\"ID diperlukan\"}"); return; }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            String nomorKamar = parseJsonField(body, "nomorKamar");
            String tipe       = parseJsonField(body, "tipe");
            long   harga      = Long.parseLong(parseJsonField(body, "harga"));
            String status     = parseJsonField(body, "status");

            Room updated = MemoryStore.updateRoom(id, nomorKamar, tipe, harga, status);
            if (updated == null) sendResponse(ex, 404, "{\"error\":\"Kamar tidak ditemukan\"}");
            else                 sendResponse(ex, 200, updated.toJson());
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleDelete(HttpExchange ex, Integer id) throws IOException {
        if (id == null) { sendResponse(ex, 400, "{\"error\":\"ID diperlukan\"}"); return; }
        boolean deleted = MemoryStore.deleteRoom(id);
        if (deleted) sendResponse(ex, 200, "{\"message\":\"Kamar berhasil dihapus\"}");
        else         sendResponse(ex, 404, "{\"error\":\"Kamar tidak ditemukan\"}");
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    static void sendResponse(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    static Integer parseId(String path) {
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parser JSON sederhana — mengambil nilai field dari JSON string.
     * Karena Java murni (tanpa library), kita parse manual.
     * Cocok untuk JSON flat (tidak nested).
     */
    static String parseJsonField(String json, String key) {
        // Cari pola: "key": value  atau  "key":"value"
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";

        int colon = json.indexOf(":", idx + search.length());
        int start = colon + 1;

        // Skip spasi
        while (start < json.length() && json.charAt(start) == ' ') start++;

        if (json.charAt(start) == '"') {
            // String value
            int end = json.indexOf("\"", start + 1);
            return json.substring(start + 1, end);
        } else {
            // Number / boolean
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }
}
