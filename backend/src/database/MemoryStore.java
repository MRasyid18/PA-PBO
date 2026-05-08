package database;

import models.Booking;
import models.Customer;
import models.Room;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MemoryStore — Penerapan Java Collection sebagai pengganti database.
 * Menyimpan semua data dalam ArrayList selama program berjalan.
 * Menggunakan AtomicInteger untuk ID yang thread-safe.
 */
public class MemoryStore {

    // ── Java Collections (ArrayList) ─────────────────────────────────────────

    private static final ArrayList<Room>     rooms     = new ArrayList<>();
    private static final ArrayList<Customer> customers = new ArrayList<>();
    private static final ArrayList<Booking>  bookings  = new ArrayList<>();

    // ── Auto-increment ID ────────────────────────────────────────────────────

    private static final AtomicInteger roomIdSeq     = new AtomicInteger(1);
    private static final AtomicInteger customerIdSeq = new AtomicInteger(1);
    private static final AtomicInteger bookingIdSeq  = new AtomicInteger(1);

    // ── Seed Data ────────────────────────────────────────────────────────────

    static {
        // Isi data awal agar langsung ada data saat program dijalankan
        rooms.add(new Room(roomIdSeq.getAndIncrement(), "101", "Standard",      500_000, "Tersedia"));
        rooms.add(new Room(roomIdSeq.getAndIncrement(), "201", "Deluxe",        850_000, "Dipesan"));
        rooms.add(new Room(roomIdSeq.getAndIncrement(), "301", "Suite",       1_500_000, "Tersedia"));
        rooms.add(new Room(roomIdSeq.getAndIncrement(), "401", "Presidential", 3_500_000, "Maintenance"));
        rooms.add(new Room(roomIdSeq.getAndIncrement(), "102", "Standard",      500_000, "Tersedia"));

        customers.add(new Customer(customerIdSeq.getAndIncrement(), "Budi Santoso", "budi@email.com",  "081234567890", "Gold"));
        customers.add(new Customer(customerIdSeq.getAndIncrement(), "Sari Dewi",    "sari@email.com",  "082345678901", "Platinum"));
        customers.add(new Customer(customerIdSeq.getAndIncrement(), "Ahmad Fauzi",  "ahmad@email.com", "083456789012", "Regular"));
        customers.add(new Customer(customerIdSeq.getAndIncrement(), "Rina Marlina", "rina@email.com",  "084567890123", "Silver"));

        bookings.add(new Booking(bookingIdSeq.getAndIncrement(), 1, 2, "2025-07-10", "2025-07-13", 2_550_000, "Aktif"));
        bookings.add(new Booking(bookingIdSeq.getAndIncrement(), 2, 3, "2025-07-08", "2025-07-09", 1_500_000, "Selesai"));
    }

    // ════════════════════════════════════════════════
    //  ROOM CRUD
    // ════════════════════════════════════════════════

    public static ArrayList<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public static Room getRoomById(int id) {
        return rooms.stream()
                    .filter(r -> r.getId() == id)
                    .findFirst()
                    .orElse(null);
    }

    public static Room addRoom(String nomorKamar, String tipe, long harga, String status) {
        Room r = new Room(roomIdSeq.getAndIncrement(), nomorKamar, tipe, harga, status);
        rooms.add(r);
        return r;
    }

    public static Room updateRoom(int id, String nomorKamar, String tipe, long harga, String status) {
        Room r = getRoomById(id);
        if (r == null) return null;
        r.setNomorKamar(nomorKamar);
        r.setTipe(tipe);
        r.setHarga(harga);
        r.setStatus(status);
        return r;
    }

    public static boolean deleteRoom(int id) {
        return rooms.removeIf(r -> r.getId() == id);
    }

    // ════════════════════════════════════════════════
    //  CUSTOMER CRUD
    // ════════════════════════════════════════════════

    public static ArrayList<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public static Customer getCustomerById(int id) {
        return customers.stream()
                        .filter(c -> c.getId() == id)
                        .findFirst()
                        .orElse(null);
    }

    public static Customer addCustomer(String nama, String email, String telepon, String membership) {
        Customer c = new Customer(customerIdSeq.getAndIncrement(), nama, email, telepon, membership);
        customers.add(c);
        return c;
    }

    public static Customer updateCustomer(int id, String nama, String email, String telepon, String membership) {
        Customer c = getCustomerById(id);
        if (c == null) return null;
        c.setNama(nama);
        c.setEmail(email);
        c.setTelepon(telepon);
        c.setMembership(membership);
        return c;
    }

    public static boolean deleteCustomer(int id) {
        return customers.removeIf(c -> c.getId() == id);
    }

    // ════════════════════════════════════════════════
    //  BOOKING CRUD
    // ════════════════════════════════════════════════

    public static ArrayList<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public static Booking getBookingById(int id) {
        return bookings.stream()
                       .filter(b -> b.getId() == id)
                       .findFirst()
                       .orElse(null);
    }

    public static Booking addBooking(int customerId, int roomId,
                                     String checkIn, String checkOut,
                                     long totalHarga, String status) {
        Booking b = new Booking(bookingIdSeq.getAndIncrement(), customerId, roomId,
                                checkIn, checkOut, totalHarga, status);
        bookings.add(b);
        return b;
    }

    public static Booking updateBooking(int id, int customerId, int roomId,
                                        String checkIn, String checkOut,
                                        long totalHarga, String status) {
        Booking b = getBookingById(id);
        if (b == null) return null;
        b.setCustomerId(customerId);
        b.setRoomId(roomId);
        b.setCheckIn(checkIn);
        b.setCheckOut(checkOut);
        b.setTotalHarga(totalHarga);
        b.setStatus(status);
        return b;
    }

    public static boolean deleteBooking(int id) {
        return bookings.removeIf(b -> b.getId() == id);
    }
}
