package models;

/**
 * Abstract class Person — Menerapkan Abstraction & Inheritance (OOP).
 * Class ini tidak bisa di-instantiate langsung.
 * Setiap subclass WAJIB mengimplementasikan metode getInfo().
 */
public abstract class Person {

    // Encapsulation: variabel private, diakses via getter/setter
    private int    id;
    private String nama;
    private String email;

    // Constructor
    public Person(int id, String nama, String email) {
        this.id    = id;
        this.nama  = nama;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()    { return id; }
    public String getNama()  { return nama; }
    public String getEmail() { return email; }

    // ── Setters dengan validasi ───────────────────────────────────────────────

    public void setNama(String nama) {
        if (nama == null || nama.isBlank())
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        this.nama = nama.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Format email tidak valid.");
        this.email = email.trim().toLowerCase();
    }

    // ── Abstract method ───────────────────────────────────────────────────────

    /**
     * Setiap turunan Person harus bisa mengembalikan info dirinya.
     * Ini adalah penerapan Abstraction.
     */
    public abstract String getInfo();

    // ── Helper ────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return getInfo();
    }
}
