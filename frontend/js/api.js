/**
 * api.js — Modul komunikasi Frontend ↔ Backend Java
 * Semua fungsi fetch() terpusat di sini.
 * Base URL mengarah ke server Java lokal.
 */

const API_BASE = 'http://localhost:8000/api';

// ─── Utility ─────────────────────────────────────────────────────────────────

async function request(method, endpoint, body = null) {
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' },
  };
  if (body) options.body = JSON.stringify(body);

  const res = await fetch(`${API_BASE}${endpoint}`, options);
  if (!res.ok) {
    const err = await res.text();
    throw new Error(err || `HTTP Error ${res.status}`);
  }
  // Beberapa response DELETE mungkin tidak return body
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

// ─── ROOMS ───────────────────────────────────────────────────────────────────

const RoomAPI = {
  getAll:    ()       => request('GET',    '/rooms'),
  getById:   (id)     => request('GET',    `/rooms/${id}`),
  create:    (data)   => request('POST',   '/rooms', data),
  update:    (id, d)  => request('PUT',    `/rooms/${id}`, d),
  delete:    (id)     => request('DELETE', `/rooms/${id}`),
};

// ─── CUSTOMERS ───────────────────────────────────────────────────────────────

const CustomerAPI = {
  getAll:    ()       => request('GET',    '/customers'),
  getById:   (id)     => request('GET',    `/customers/${id}`),
  create:    (data)   => request('POST',   '/customers', data),
  update:    (id, d)  => request('PUT',    `/customers/${id}`, d),
  delete:    (id)     => request('DELETE', `/customers/${id}`),
};

// ─── BOOKINGS ────────────────────────────────────────────────────────────────

const BookingAPI = {
  getAll:    ()       => request('GET',    '/bookings'),
  getById:   (id)     => request('GET',    `/bookings/${id}`),
  create:    (data)   => request('POST',   '/bookings', data),
  update:    (id, d)  => request('PUT',    `/bookings/${id}`, d),
  delete:    (id)     => request('DELETE', `/bookings/${id}`),
};

// ─── SERVER HEALTH CHECK ─────────────────────────────────────────────────────

async function checkServerHealth() {
  try {
    await fetch(`${API_BASE}/health`, { signal: AbortSignal.timeout(2000) });
    return true;
  } catch {
    return false;
  }
}

/**
 * DEMO MODE — digunakan saat backend Java belum dijalankan.
 * Data dummy ini disimpan di memori browser (localStorage) agar
 * aplikasi tetap bisa didemonstrasikan tanpa backend.
 */
const DemoStore = {
  rooms: [
    { id: 1, nomorKamar: '101', tipe: 'Standard',      harga: 500000,  status: 'Tersedia' },
    { id: 2, nomorKamar: '201', tipe: 'Deluxe',        harga: 850000,  status: 'Dipesan'  },
    { id: 3, nomorKamar: '301', tipe: 'Suite',         harga: 1500000, status: 'Tersedia' },
    { id: 4, nomorKamar: '401', tipe: 'Presidential',  harga: 3500000, status: 'Maintenance'},
    { id: 5, nomorKamar: '102', tipe: 'Standard',      harga: 500000,  status: 'Tersedia' },
  ],
  customers: [
    { id: 1, nama: 'Rasyid',  email: 'mrasyid18@gmail.com',  telepon: '081345162892', membership: 'Gold'    },
    { id: 2, nama: 'Zidan',     email: 'zidan@gmail.com',  telepon: '082345678901', membership: 'Platinum'},
    { id: 3, nama: 'Andra',   email: 'andra@gmail.com', telepon: '083456789012', membership: 'Regular' },
    { id: 4, nama: 'Angel',  email: 'angel@gmail.com',  telepon: '084567890123', membership: 'Silver'  },
    { id: 5, nama: 'Renaya', email: 'renaya@gmail.com',  telepon: '085678901234', membership: 'Regular'    },
  ],
  bookings: [
    { id: 1, customerId: 1, roomId: 1, checkIn: '2025-07-10', checkOut: '2025-07-13', totalHarga: 2550000, status: 'Aktif'},
    { id: 2, customerId: 2, roomId: 2, checkIn: '2025-07-08', checkOut: '2025-07-09', totalHarga: 1500000, status: 'Selesai'},
    { id: 3, customerId: 3, roomId: 3, checkIn: '2026-10-04', checkOut: '2026-11-05', totalHarga: 1500000, status: 'Aktif'},
    { id: 4, customerId: 4, roomId: 4, checkIn: '2026-06-03', checkOut: '2026-06-04', totalHarga: 3500000, status: 'Selesai'},
    { id: 5, customerId: 5, roomId: 5, checkIn: '2026-02-02', checkOut: '2026-02-03', totalHarga: 500000, status: 'Selesai'},
  ],
  nextId: { rooms: 6, customers: 6, bookings: 3 },
};

// Simpan demo data ke sessionStorage agar tidak hilang saat navigate antar tab
function loadDemoStore() {
  const saved = sessionStorage.getItem('demoStore');
  if (saved) {
    const parsed = JSON.parse(saved);
    Object.assign(DemoStore, parsed);
  }
}
function saveDemoStore() {
  sessionStorage.setItem('demoStore', JSON.stringify(DemoStore));
}
loadDemoStore();

// Demo API simulasi delay seperti network request
function demoDelay() {
  return new Promise(r => setTimeout(r, 120));
}

const DemoRoomAPI = {
  getAll:  async () => { await demoDelay(); return [...DemoStore.rooms]; },
  create:  async (d) => { await demoDelay(); const item = { ...d, id: DemoStore.nextId.rooms++ }; DemoStore.rooms.push(item); saveDemoStore(); return item; },
  update:  async (id, d) => { await demoDelay(); const i = DemoStore.rooms.findIndex(r => r.id === id); if (i < 0) throw new Error('Not found'); DemoStore.rooms[i] = { ...DemoStore.rooms[i], ...d }; saveDemoStore(); return DemoStore.rooms[i]; },
  delete:  async (id) => { await demoDelay(); DemoStore.rooms = DemoStore.rooms.filter(r => r.id !== id); saveDemoStore(); return null; },
};

const DemoCustomerAPI = {
  getAll:  async () => { await demoDelay(); return [...DemoStore.customers]; },
  create:  async (d) => { await demoDelay(); const item = { ...d, id: DemoStore.nextId.customers++ }; DemoStore.customers.push(item); saveDemoStore(); return item; },
  update:  async (id, d) => { await demoDelay(); const i = DemoStore.customers.findIndex(c => c.id === id); if (i < 0) throw new Error('Not found'); DemoStore.customers[i] = { ...DemoStore.customers[i], ...d }; saveDemoStore(); return DemoStore.customers[i]; },
  delete:  async (id) => { await demoDelay(); DemoStore.customers = DemoStore.customers.filter(c => c.id !== id); saveDemoStore(); return null; },
};

const DemoBookingAPI = {
  getAll:  async () => { await demoDelay(); return [...DemoStore.bookings]; },
  create:  async (d) => { await demoDelay(); const item = { ...d, id: DemoStore.nextId.bookings++ }; DemoStore.bookings.push(item); saveDemoStore(); return item; },
  update:  async (id, d) => { await demoDelay(); const i = DemoStore.bookings.findIndex(b => b.id === id); if (i < 0) throw new Error('Not found'); DemoStore.bookings[i] = { ...DemoStore.bookings[i], ...d }; saveDemoStore(); return DemoStore.bookings[i]; },
  delete:  async (id) => { await demoDelay(); DemoStore.bookings = DemoStore.bookings.filter(b => b.id !== id); saveDemoStore(); return null; },
};

// Variabel global untuk mode aktif (diset dari ui.js setelah health check)
window.IS_DEMO = false;
window.API = {
  rooms:     null,
  customers: null,
  bookings:  null,
};

async function initAPI() {
  const online = await checkServerHealth();
  if (online) {
    window.IS_DEMO = false;
    window.API.rooms     = RoomAPI;
    window.API.customers = CustomerAPI;
    window.API.bookings  = BookingAPI;
    return 'online';
  } else {
    window.IS_DEMO = true;
    window.API.rooms     = DemoRoomAPI;
    window.API.customers = DemoCustomerAPI;
    window.API.bookings  = DemoBookingAPI;
    return 'demo';
  }
}
