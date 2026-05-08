# 🏨 Grand Nusantara Hotel — Sistem Manajemen Pemesanan Kamar

Aplikasi web full-stack untuk manajemen hotel dengan **Frontend HTML/CSS/JS** dan **Backend Java murni**.

---

## 📁 Struktur Folder

```
hotel-booking-app/
│
├── frontend/                     # Wilayah kerja Tim Frontend (2 Orang)
│   ├── index.html                # Halaman utama (Dashboard + semua tab)
│   ├── css/
│   │   └── style.css             # Desain & tata letak (Dark Luxury Theme)
│   └── js/
│       ├── api.js                # Fungsi Fetch/komunikasi dengan Backend Java
│       └── ui.js                 # Manipulasi DOM (tabel, modal, alert)
│
└── backend/                      # Wilayah kerja Tim Backend (3 Orang)
    └── src/
        ├── Main.java             # Entry point, HTTP Server lokal (port 8000)
        │
        ├── models/               # OOP: Class, Inheritance, Abstraction
        │   ├── Person.java       # Abstract class (Abstraction)
        │   ├── Customer.java     # Extends Person (Inheritance)
        │   ├── Room.java         # Class dengan Encapsulation penuh
        │   └── Booking.java      # Agregasi Customer & Room
        │
        ├── database/             # Java Collection — pengganti database
        │   └── MemoryStore.java  # ArrayList<Room>, <Customer>, <Booking>
        │
        └── controllers/          # REST API — Polymorphism
            ├── RoomController.java
            ├── CustomerController.java
            └── BookingController.java
```

---

## 🚀 Cara Menjalankan

### Langkah 1 — Jalankan Backend Java

**Syarat:** Java JDK 11+ terinstall

```bash
# Masuk ke folder backend
cd backend

# Buat folder output
mkdir -p out

# Compile semua file Java
javac -d out src/models/*.java src/database/*.java src/controllers/*.java src/Main.java

# Jalankan server
java -cp out Main
```

Server akan berjalan di `http://localhost:8000`

> **Catatan VS Code:** Install ekstensi **Extension Pack for Java**, lalu buka `Main.java` dan klik tombol ▶ **Run** di atas method `main()`.

### Langkah 2 — Buka Frontend

1. Install ekstensi **Live Server** di VS Code
2. Klik kanan `frontend/index.html`
3. Pilih **"Open with Live Server"**

Atau buka file `frontend/index.html` langsung di browser.

---

## ✨ Fitur Aplikasi

### Dashboard
- Statistik: Total kamar, kamar tersedia, total pelanggan, total pemesanan
- Preview 5 kamar terbaru & 5 pemesanan aktif

### Manajemen Kamar (CRUD)
- Tambah kamar baru (Nomor, Tipe, Harga, Status)
- Edit data kamar
- Hapus kamar
- Tipe: Standard, Deluxe, Suite, Presidential Suite
- Status: Tersedia, Dipesan, Maintenance

### Data Pelanggan (CRUD)
- Registrasi pelanggan baru
- Edit data pelanggan
- Hapus data pelanggan
- Tipe Keanggotaan: Regular, Silver, Gold, Platinum

### Pemesanan (CRUD)
- Buat pemesanan baru (pilih pelanggan + kamar tersedia)
- Kalkulasi otomatis total harga berdasarkan durasi
- Edit status pemesanan
- Hapus pemesanan

---

## 🎯 Penerapan Konsep

| Konsep OOP | Implementasi |
|------------|-------------|
| **Encapsulation** | Semua field di `Room.java`, `Customer.java`, `Booking.java` adalah `private` dengan getter/setter bervalidasi |
| **Abstraction** | `Person.java` adalah abstract class dengan metode abstract `getInfo()` |
| **Inheritance** | `Customer.java` extends `Person.java` |
| **Polymorphism** | Method `handle()` di controller bereaksi berbeda sesuai HTTP method (GET/POST/PUT/DELETE) |
| **Java Collection** | `MemoryStore.java` menggunakan `ArrayList<Room>`, `ArrayList<Customer>`, `ArrayList<Booking>` |
| **Agregasi** | `Booking.java` mengacu ke `customerId` dan `roomId` |

---

## 🌐 API Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/api/health` | Cek status server |
| GET | `/api/rooms` | Ambil semua kamar |
| POST | `/api/rooms` | Tambah kamar baru |
| PUT | `/api/rooms/{id}` | Update kamar |
| DELETE | `/api/rooms/{id}` | Hapus kamar |
| GET | `/api/customers` | Ambil semua pelanggan |
| POST | `/api/customers` | Tambah pelanggan baru |
| PUT | `/api/customers/{id}` | Update pelanggan |
| DELETE | `/api/customers/{id}` | Hapus pelanggan |
| GET | `/api/bookings` | Ambil semua pemesanan |
| POST | `/api/bookings` | Buat pemesanan baru |
| PUT | `/api/bookings/{id}` | Update pemesanan |
| DELETE | `/api/bookings/{id}` | Hapus pemesanan |

### Contoh JSON

**Room:**
```json
{ "id": 1, "nomorKamar": "101", "tipe": "Standard", "harga": 500000, "status": "Tersedia" }
```

**Customer:**
```json
{ "id": 1, "nama": "Budi Santoso", "email": "budi@email.com", "telepon": "081234567890", "membership": "Gold" }
```

**Booking:**
```json
{ "id": 1, "customerId": 1, "roomId": 2, "checkIn": "2025-07-10", "checkOut": "2025-07-13", "totalHarga": 2550000, "status": "Aktif" }
```

---

## 🎨 Mode Demo

Jika backend Java **belum dijalankan**, frontend akan otomatis beralih ke **Mode Demo** dengan data lokal (simulasi). Semua operasi CRUD tetap berfungsi menggunakan `sessionStorage` browser.

---

## 👥 Pembagian Tugas Tim

| Anggota | Fokus | File |
|---------|-------|------|
| Backend Dev 1 | OOP & Models | `Person.java`, `Customer.java`, `Room.java`, `Booking.java` |
| Backend Dev 2 | Data & CRUD | `MemoryStore.java` |
| Backend Dev 3 | Server & Controllers | `Main.java`, `RoomController.java`, `CustomerController.java`, `BookingController.java` |
| Frontend Dev 1 | UI/UX & HTML/CSS | `index.html`, `style.css` |
| Frontend Dev 2 | JavaScript | `api.js`, `ui.js` |
