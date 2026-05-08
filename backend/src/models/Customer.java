package models;

/**
 * Customer — Menerapkan Inheritance dari Person.
 * Menambahkan atribut: telepon & membership (tipe keanggotaan).
 */
public class Customer extends Person {

    private String telepon;
    private String membership; // Regular | Silver | Gold | Platinum

    // Constructor
    public Customer(int id, String nama, String email, String telepon, String membership) {
        super(id, nama, email);  // Memanggil constructor induk (Person)
        setTelepon(telepon);
        setMembership(membership);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getTelepon()    { return telepon; }
    public String getMembership() { return membership; }

    // ── Setters dengan validasi ───────────────────────────────────────────────

    public void setTelepon(String telepon) {
        if (telepon == null || telepon.isBlank())
            throw new IllegalArgumentException("Telepon tidak boleh kosong.");
        this.telepon = telepon.trim();
    }

    public void setMembership(String membership) {
        if (membership == null) membership = "Regular";
        String m = membership.trim();
        if (!m.equals("Regular") && !m.equals("Silver") && !m.equals("Gold") && !m.equals("Platinum"))
            throw new IllegalArgumentException("Membership harus Regular, Silver, Gold, atau Platinum.");
        this.membership = m;
    }

    // ── Implementasi abstract method dari Person ───────────────────────────────

    @Override
    public String getInfo() {
        return String.format("Customer[%d] %s <%s> | Tel: %s | Membership: %s",
            getId(), getNama(), getEmail(), telepon, membership);
    }

    // ── Konversi ke JSON string ───────────────────────────────────────────────

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"nama\":\"%s\",\"email\":\"%s\",\"telepon\":\"%s\",\"membership\":\"%s\"}",
            getId(), escJson(getNama()), escJson(getEmail()), escJson(telepon), escJson(membership)
        );
    }

    private String escJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
