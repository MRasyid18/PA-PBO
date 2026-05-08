package models;

/**
 * Room — Menerapkan Encapsulation (OOP).
 * Semua field private, diakses hanya lewat getter/setter bervalidasi.
 */
public class Room {

    private int    id;
    private String nomorKamar;
    private String tipe;      // Standard | Deluxe | Suite | Presidential
    private long   harga;     // per malam, dalam Rupiah
    private String status;    // Tersedia | Dipesan | Maintenance

    // Constructor
    public Room(int id, String nomorKamar, String tipe, long harga, String status) {
        this.id = id;
        setNomorKamar(nomorKamar);
        setTipe(tipe);
        setHarga(harga);
        setStatus(status);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()         { return id; }
    public String getNomorKamar() { return nomorKamar; }
    public String getTipe()       { return tipe; }
    public long   getHarga()      { return harga; }
    public String getStatus()     { return status; }

    // ── Setters dengan validasi ───────────────────────────────────────────────

    public void setNomorKamar(String nomorKamar) {
        if (nomorKamar == null || nomorKamar.isBlank())
            throw new IllegalArgumentException("Nomor kamar tidak boleh kosong.");
        this.nomorKamar = nomorKamar.trim();
    }

    public void setTipe(String tipe) {
        if (tipe == null || tipe.isBlank())
            throw new IllegalArgumentException("Tipe kamar tidak boleh kosong.");
        this.tipe = tipe.trim();
    }

    public void setHarga(long harga) {
        if (harga < 0)
            throw new IllegalArgumentException("Harga kamar tidak boleh negatif.");
        this.harga = harga;
    }

    public void setStatus(String status) {
        if (status == null) status = "Tersedia";
        String s = status.trim();
        if (!s.equals("Tersedia") && !s.equals("Dipesan") && !s.equals("Maintenance"))
            throw new IllegalArgumentException("Status harus: Tersedia, Dipesan, atau Maintenance.");
        this.status = s;
    }

    // ── Konversi ke JSON string ───────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"nomorKamar\":\"%s\",\"tipe\":\"%s\",\"harga\":%d,\"status\":\"%s\"}",
            id, escJson(nomorKamar), escJson(tipe), harga, escJson(status)
        );
    }

    private String escJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return String.format("Room[%d] No.%s (%s) Rp%,d — %s", id, nomorKamar, tipe, harga, status);
    }
}
