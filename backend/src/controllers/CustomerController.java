package controllers;

import com.sun.net.httpserver.HttpExchange;
import database.MemoryStore;
import models.Customer;

import java.io.IOException;
import java.util.ArrayList;

import static controllers.RoomController.*;

/**
 * CustomerController — Menangani request CRUD untuk endpoint /api/customers
 */
public class CustomerController {

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
            ArrayList<Customer> all = MemoryStore.getAllCustomers();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < all.size(); i++) {
                sb.append(all.get(i).toJson());
                if (i < all.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendResponse(ex, 200, sb.toString());
        } else {
            Customer c = MemoryStore.getCustomerById(id);
            if (c == null) sendResponse(ex, 404, "{\"error\":\"Pelanggan tidak ditemukan\"}");
            else           sendResponse(ex, 200, c.toJson());
        }
    }

    private static void handlePost(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes());
        try {
            String nama       = parseJsonField(body, "nama");
            String email      = parseJsonField(body, "email");
            String telepon    = parseJsonField(body, "telepon");
            String membership = parseJsonField(body, "membership");

            Customer created = MemoryStore.addCustomer(nama, email, telepon, membership);
            sendResponse(ex, 201, created.toJson());
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handlePut(HttpExchange ex, Integer id) throws IOException {
        if (id == null) { sendResponse(ex, 400, "{\"error\":\"ID diperlukan\"}"); return; }
        String body = new String(ex.getRequestBody().readAllBytes());
        try {
            String nama       = parseJsonField(body, "nama");
            String email      = parseJsonField(body, "email");
            String telepon    = parseJsonField(body, "telepon");
            String membership = parseJsonField(body, "membership");

            Customer updated = MemoryStore.updateCustomer(id, nama, email, telepon, membership);
            if (updated == null) sendResponse(ex, 404, "{\"error\":\"Pelanggan tidak ditemukan\"}");
            else                 sendResponse(ex, 200, updated.toJson());
        } catch (Exception e) {
            sendResponse(ex, 400, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static void handleDelete(HttpExchange ex, Integer id) throws IOException {
        if (id == null) { sendResponse(ex, 400, "{\"error\":\"ID diperlukan\"}"); return; }
        boolean deleted = MemoryStore.deleteCustomer(id);
        if (deleted) sendResponse(ex, 200, "{\"message\":\"Pelanggan berhasil dihapus\"}");
        else         sendResponse(ex, 404, "{\"error\":\"Pelanggan tidak ditemukan\"}");
    }
}
