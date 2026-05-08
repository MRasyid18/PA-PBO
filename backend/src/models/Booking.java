package models;

/**
 * Booking — Menerapkan Agregasi dari Customer & Room.
 * Satu pemesanan menghubungkan satu Customer dengan satu Room.
 */
public class Booking {

    private int    id;
    private int    customerId;
    private int    roomId;
    private String checkIn;     // Format: yyyy-MM-dd
    private String checkOut;    // Format: yyyy-MM-dd
    private long   totalHarga;
    private String status;      // Aktif | Selesai | Dibatalkan

    // Constructor
    public Booking(int id, int customerId, int roomId,
                   String checkIn, String checkOut, long totalHarga, String status) {
        this.id         = id;
        this.customerId = customerId;
        this.roomId     = roomId;
        setCheckIn(checkIn);
        setCheckOut(checkOut);
        setTotalHarga(totalHarga);
        setStatus(status);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()         { return id; }
    public int    getCustomerId() { return customerId; }
    public int    getRoomId()     { return roomId; }
    public String getCheckIn()    { return checkIn; }
    public String getCheckOut()   { return checkOut; }
    public long   getTotalHarga() { return totalHarga; }
    public String getStatus()     { return status; }

    // ── Setters dengan validasi ───────────────────────────────────────────────

    public void setCheckIn(String checkIn) {
        if (checkIn == null || checkIn.isBlank())
            throw new IllegalArgumentException("Tanggal check-in tidak boleh kosong.");
        this.checkIn = checkIn.trim();
    }

    public void setCheckOut(String checkOut) {
        if (checkOut == null || checkOut.isBlank())
            throw new IllegalArgumentException("Tanggal check-out tidak boleh kosong.");
        this.checkOut = checkOut.trim();
    }

    public void setTotalHarga(long totalHarga) {
        if (totalHarga < 0)
            throw new IllegalArgumentException("Total harga tidak boleh negatif.");
        this.totalHarga = totalHarga;
    }

    public void setStatus(String status) {
        if (status == null) status = "Aktif";
        String s = status.trim();
        if (!s.equals("Aktif") && !s.equals("Selesai") && !s.equals("Dibatalkan"))
            throw new IllegalArgumentException("Status booking harus: Aktif, Selesai, atau Dibatalkan.");
        this.status = s;
    }

    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setRoomId(int roomId)         { this.roomId = roomId; }

    // ── Konversi ke JSON string ───────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"customerId\":%d,\"roomId\":%d," +
            "\"checkIn\":\"%s\",\"checkOut\":\"%s\",\"totalHarga\":%d,\"status\":\"%s\"}",
            id, customerId, roomId, checkIn, checkOut, totalHarga, escJson(status)
        );
    }

    private String escJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return String.format("Booking[%d] Cust#%d Room#%d | %s → %s | Rp%,d | %s",
            id, customerId, roomId, checkIn, checkOut, totalHarga, status);
    }
}
