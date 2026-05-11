import com.sun.net.httpserver.HttpServer;
import controllers.BookingController;
import controllers.CustomerController;
import controllers.RoomController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT = 8000;

    public static void main(String[] args) throws IOException {

        // Buat HTTP Server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // ── Daftarkan Endpoint ──────────────────────────────────────────────

        // Health check endpoint (untuk cek koneksi dari frontend)
        server.createContext("/api/health", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            String response = "{\"status\":\"ok\",\"message\":\"Grand Nusantara Hotel API running\"}";
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        });

        // Rooms endpoint → /api/rooms dan /api/rooms/{id}
        server.createContext("/api/rooms",     exchange -> RoomController.handle(exchange));

        // Customers endpoint → /api/customers dan /api/customers/{id}
        server.createContext("/api/customers", exchange -> CustomerController.handle(exchange));

        // Bookings endpoint → /api/bookings dan /api/bookings/{id}
        server.createContext("/api/bookings",  exchange -> BookingController.handle(exchange));

        // ── Thread Pool ────────────────────────────────────────────────────

        // Gunakan thread pool agar server bisa melayani banyak request sekaligus
        server.setExecutor(Executors.newFixedThreadPool(4));

        // ── Mulai Server ───────────────────────────────────────────────────

        server.start();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Grand Nusantara Hotel — Backend API    ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Server aktif di: http://localhost:" + PORT + "  ║");
        System.out.println("║                                          ║");
        System.out.println("║  Endpoint tersedia:                      ║");
        System.out.println("║  GET/POST   /api/rooms                   ║");
        System.out.println("║  GET/PUT/DELETE /api/rooms/{id}          ║");
        System.out.println("║  GET/POST   /api/customers               ║");
        System.out.println("║  GET/PUT/DELETE /api/customers/{id}      ║");
        System.out.println("║  GET/POST   /api/bookings                ║");
        System.out.println("║  GET/PUT/DELETE /api/bookings/{id}       ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Buka frontend dengan Live Server        ║");
        System.out.println("║  Tekan Ctrl+C untuk menghentikan server  ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Tambahkan shutdown hook untuk pesan ketika server dihentikan
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            System.out.println("\n[Server dihentikan]")
        ));
    }
}
