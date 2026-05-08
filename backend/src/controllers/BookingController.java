package controllers;

import com.sun.net.httpserver.HttpExchange;
import database.MemoryStore;
import models.Booking;

import java.io.IOException;
import java.util.ArrayList;

import static controllers.RoomController.*;

/**
 * BookingController — Menangani request CRUD untuk endpoint /api/bookings
 */
public class BookingController {

    public static void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin",  "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if (method.equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

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
            ArrayList<Booking> all = MemoryStore.getAllBookings();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toJson());
                if (i < all.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendResponse(ex, 200, sb.toString());
        } else {
            Booking b = MemoryStore.getBookingById(id);
            if (b == null) sendResponse(ex, 404, "{\"error\":\"Pemesanan tidak ditemukan\"}");
            else           sendResponse(ex, 200, b.toJson());
        }
    }

    private static void handlePost(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes());
        try {
            int    customerId = Integer.parseInt(parseJsonField(body, "customerId"));
            int    roomId     = Integer.parseInt(parseJsonField(body, "roomId"));
            String checkIn    = parseJsonField(body, "checkIn");
            String checkOut   = parseJsonField(body, "checkOut");
            long   total      = Long.parseLong(parseJsonField(body, "totalHarga"));
            String status     = parseJsonField(body, "status");

            Booking created = MemoryStore.addBooking(customerId, roomId, checkIn, checkOut, total, status);
            sendResponse(ex, 201, created.toJson());
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handlePut(HttpExchange ex, Integer id) throws IOException {
        if (id == null) { sendResponse(ex, 400, "{\"error\":\"ID diperlukan\"}"); return; }
        String body = new String(ex.getRequestBody().readAllBytes());
        try {
            int    customerId = Integer.parseInt(parseJsonField(body, "customerId"));
            int    roomId     = Integer.parseInt(parseJsonField(body, "roomId"));
            String checkIn    = parseJsonField(body, "checkIn");
            String checkOut   = parseJsonField(body, "checkOut");
            long   total      = Long.parseLong(parseJsonField(body, "totalHarga"));
            String status     = parseJsonField(body, "status");

            Booking updated = MemoryStore.updateBooking(id, customerId, roomId, checkIn, checkOut, total, status);
            if (updated == null) sendResponse(ex, 404, "{\"error\":\"Pemesanan tidak ditemukan\"}");
            else                 sendResponse(ex, 200, updated.toJson());
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleDelete(HttpExchange ex, Integer id) throws IOException {
        if (id == null) { sendResponse(ex, 400, "{\"error\":\"ID diperlukan\"}"); return; }
        boolean deleted = MemoryStore.deleteBooking(id);
        if (deleted) sendResponse(ex, 200, "{\"message\":\"Pemesanan berhasil dihapus\"}");
        else         sendResponse(ex, 404, "{\"error\":\"Pemesanan tidak ditemukan\"}");
    }
}
