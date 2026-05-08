/**
 * ui.js — Modul manipulasi DOM & interaktivitas
 * Menangani: render tabel, buka/tutup modal, submit form, alert
 */

// ─── GLOBAL STATE ─────────────────────────────────────────────────────────────

let cachedRooms     = [];
let cachedCustomers = [];
let cachedBookings  = [];

// ─── INIT ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', async () => {
  setDate();
  await bootApp();
  bindNavigation();
  bindModals();
  bindForms();
  bindBookingCalculator();
});

async function bootApp() {
  const mode = await initAPI();   // dari api.js

  const dot  = document.getElementById('statusDot');
  const txt  = document.getElementById('statusText');

  if (mode === 'online') {
    dot.className = 'status-dot online';
    txt.textContent = 'Server Online';
  } else {
    dot.className = 'status-dot demo';
    txt.textContent = 'Mode Demo';
    showAlert('Backend belum aktif. Berjalan dalam Mode Demo (data lokal).', 'info');
  }

  await loadDashboard();
}

function setDate() {
  const el = document.getElementById('headerDate');
  const now = new Date();
  el.textContent = now.toLocaleDateString('id-ID', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });
}

// ─── NAVIGATION ───────────────────────────────────────────────────────────────

function bindNavigation() {
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(btn => {
    btn.addEventListener('click', () => {
      const tab = btn.dataset.tab;
      // Set active nav
      navItems.forEach(n => n.classList.remove('active'));
      btn.classList.add('active');
      // Set active tab
      document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
      document.getElementById(`tab-${tab}`).classList.add('active');
      // Set header title
      const titles = { dashboard: 'Dashboard', rooms: 'Manajemen Kamar', customers: 'Data Pelanggan', bookings: 'Pemesanan' };
      document.getElementById('headerTitle').textContent = titles[tab];
      // Load data
      if (tab === 'dashboard') loadDashboard();
      if (tab === 'rooms')     loadRooms();
      if (tab === 'customers') loadCustomers();
      if (tab === 'bookings')  loadBookings();
    });
  });
}

// ─── ALERT ───────────────────────────────────────────────────────────────────

function showAlert(msg, type = 'success') {
  const container = document.getElementById('alertContainer');
  const div = document.createElement('div');
  div.className = `alert alert-${type}`;
  div.textContent = msg;
  container.appendChild(div);
  setTimeout(() => div.remove(), 3500);
}

// ─── MODAL ────────────────────────────────────────────────────────────────────

function openModal(id) {
  document.getElementById(id).classList.add('open');
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
}

function bindModals() {
  // Close buttons
  document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', () => closeModal(btn.dataset.close));
  });
  // Overlay click
  document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) closeModal(overlay.id);
    });
  });

  // Open buttons
  document.getElementById('btnOpenRoomModal').addEventListener('click', () => {
    resetRoomForm();
    openModal('roomModal');
  });
  document.getElementById('btnOpenCustModal').addEventListener('click', () => {
    resetCustForm();
    openModal('custModal');
  });
  document.getElementById('btnOpenBookModal').addEventListener('click', async () => {
    resetBookForm();
    await populateBookingSelects();
    openModal('bookModal');
  });
}

// ─── FORMATTING ───────────────────────────────────────────────────────────────

function formatRupiah(n) {
  return 'Rp ' + Number(n).toLocaleString('id-ID');
}

function statusBadge(status) {
  const map = {
    'Tersedia':    ['badge-available', 'Tersedia'],
    'Dipesan':     ['badge-booked',    'Dipesan'],
    'Maintenance': ['badge-maintenance','Maintenance'],
    'Aktif':       ['badge-active',    'Aktif'],
    'Selesai':     ['badge-done',      'Selesai'],
    'Dibatalkan':  ['badge-cancelled', 'Dibatalkan'],
  };
  const [cls, label] = map[status] || ['badge-maintenance', status];
  return `<span class="badge ${cls}">${label}</span>`;
}

function memberBadge(m) {
  const cls = { Regular: 'member-regular', Silver: 'member-silver', Gold: 'member-gold', Platinum: 'member-platinum' };
  return `<span class="${cls[m] || ''}">${m || '—'}</span>`;
}

// ─── DASHBOARD ───────────────────────────────────────────────────────────────

async function loadDashboard() {
  try {
    const [rooms, customers, bookings] = await Promise.all([
      window.API.rooms.getAll(),
      window.API.customers.getAll(),
      window.API.bookings.getAll(),
    ]);

    cachedRooms     = rooms;
    cachedCustomers = customers;
    cachedBookings  = bookings;

    document.getElementById('statTotalRooms').textContent     = rooms.length;
    document.getElementById('statAvailRooms').textContent     = rooms.filter(r => r.status === 'Tersedia').length;
    document.getElementById('statTotalCustomers').textContent = customers.length;
    document.getElementById('statTotalBookings').textContent  = bookings.length;

    // Rooms preview (last 5)
    const rb = document.getElementById('dashRoomsBody');
    rb.innerHTML = rooms.slice(0, 5).map(r => `
      <tr>
        <td>${r.nomorKamar}</td>
        <td>${r.tipe}</td>
        <td>${formatRupiah(r.harga)}</td>
        <td>${statusBadge(r.status)}</td>
      </tr>`).join('') || emptyRow(4);

    // Bookings preview (last 5)
    const bb = document.getElementById('dashBookingsBody');
    bb.innerHTML = bookings.slice(0, 5).map(b => {
      const cust = customers.find(c => c.id === b.customerId);
      const room = rooms.find(r => r.id === b.roomId);
      return `<tr>
        <td>#${b.id}</td>
        <td>${cust ? cust.nama : '—'}</td>
        <td>${room ? room.nomorKamar : '—'}</td>
        <td>${formatRupiah(b.totalHarga)}</td>
      </tr>`;
    }).join('') || emptyRow(4);

  } catch (e) {
    showAlert('Gagal memuat dashboard: ' + e.message, 'error');
  }
}

function emptyRow(cols) {
  return `<tr class="empty-row"><td colspan="${cols}">Belum ada data</td></tr>`;
}

// ─── ROOMS ────────────────────────────────────────────────────────────────────

async function loadRooms() {
  try {
    const rooms = await window.API.rooms.getAll();
    cachedRooms = rooms;
    const tbody = document.getElementById('roomsBody');
    tbody.innerHTML = rooms.length ? rooms.map(r => `
      <tr>
        <td>#${r.id}</td>
        <td><strong style="color:var(--text-primary)">${r.nomorKamar}</strong></td>
        <td>${r.tipe}</td>
        <td>${formatRupiah(r.harga)}</td>
        <td>${statusBadge(r.status)}</td>
        <td>
          <div class="action-btns">
            <button class="btn-icon edit" onclick="editRoom(${r.id})" title="Edit">✎</button>
            <button class="btn-icon delete" onclick="deleteRoom(${r.id})" title="Hapus">✕</button>
          </div>
        </td>
      </tr>`).join('') : emptyRow(6);
  } catch (e) {
    showAlert('Gagal memuat data kamar: ' + e.message, 'error');
  }
}

function resetRoomForm() {
  document.getElementById('roomId').value     = '';
  document.getElementById('roomNomor').value  = '';
  document.getElementById('roomTipe').value   = '';
  document.getElementById('roomHarga').value  = '';
  document.getElementById('roomStatus').value = 'Tersedia';
  document.getElementById('roomModalTitle').textContent = 'Tambah Kamar';
}

function editRoom(id) {
  const r = cachedRooms.find(r => r.id === id);
  if (!r) return;
  document.getElementById('roomId').value     = r.id;
  document.getElementById('roomNomor').value  = r.nomorKamar;
  document.getElementById('roomTipe').value   = r.tipe;
  document.getElementById('roomHarga').value  = r.harga;
  document.getElementById('roomStatus').value = r.status;
  document.getElementById('roomModalTitle').textContent = 'Edit Kamar';
  openModal('roomModal');
}

async function deleteRoom(id) {
  if (!confirm('Hapus kamar ini?')) return;
  try {
    await window.API.rooms.delete(id);
    showAlert('Kamar berhasil dihapus.', 'success');
    loadRooms();
  } catch (e) {
    showAlert('Gagal menghapus kamar: ' + e.message, 'error');
  }
}

// ─── CUSTOMERS ───────────────────────────────────────────────────────────────

async function loadCustomers() {
  try {
    const customers = await window.API.customers.getAll();
    cachedCustomers = customers;
    const tbody = document.getElementById('customersBody');
    tbody.innerHTML = customers.length ? customers.map(c => `
      <tr>
        <td>#${c.id}</td>
        <td><strong style="color:var(--text-primary)">${c.nama}</strong></td>
        <td>${c.email}</td>
        <td>${c.telepon}</td>
        <td>${memberBadge(c.membership)}</td>
        <td>
          <div class="action-btns">
            <button class="btn-icon edit" onclick="editCustomer(${c.id})" title="Edit">✎</button>
            <button class="btn-icon delete" onclick="deleteCustomer(${c.id})" title="Hapus">✕</button>
          </div>
        </td>
      </tr>`).join('') : emptyRow(6);
  } catch (e) {
    showAlert('Gagal memuat data pelanggan: ' + e.message, 'error');
  }
}

function resetCustForm() {
  document.getElementById('custId').value      = '';
  document.getElementById('custNama').value    = '';
  document.getElementById('custEmail').value   = '';
  document.getElementById('custTelepon').value = '';
  document.getElementById('custMember').value  = 'Regular';
  document.getElementById('custModalTitle').textContent = 'Tambah Pelanggan';
}

function editCustomer(id) {
  const c = cachedCustomers.find(c => c.id === id);
  if (!c) return;
  document.getElementById('custId').value      = c.id;
  document.getElementById('custNama').value    = c.nama;
  document.getElementById('custEmail').value   = c.email;
  document.getElementById('custTelepon').value = c.telepon;
  document.getElementById('custMember').value  = c.membership;
  document.getElementById('custModalTitle').textContent = 'Edit Pelanggan';
  openModal('custModal');
}

async function deleteCustomer(id) {
  if (!confirm('Hapus pelanggan ini?')) return;
  try {
    await window.API.customers.delete(id);
    showAlert('Pelanggan berhasil dihapus.', 'success');
    loadCustomers();
  } catch (e) {
    showAlert('Gagal menghapus pelanggan: ' + e.message, 'error');
  }
}

// ─── BOOKINGS ────────────────────────────────────────────────────────────────

async function loadBookings() {
  try {
    const [bookings, customers, rooms] = await Promise.all([
      window.API.bookings.getAll(),
      window.API.customers.getAll(),
      window.API.rooms.getAll(),
    ]);
    cachedBookings  = bookings;
    cachedCustomers = customers;
    cachedRooms     = rooms;

    const tbody = document.getElementById('bookingsBody');
    tbody.innerHTML = bookings.length ? bookings.map(b => {
      const cust = customers.find(c => c.id === b.customerId);
      const room = rooms.find(r => r.id === b.roomId);
      return `<tr>
        <td>#${b.id}</td>
        <td>${cust ? cust.nama : '—'}</td>
        <td>${room ? room.nomorKamar + ' (' + room.tipe + ')' : '—'}</td>
        <td>${b.checkIn}</td>
        <td>${b.checkOut}</td>
        <td>${formatRupiah(b.totalHarga)}</td>
        <td>${statusBadge(b.status)}</td>
        <td>
          <div class="action-btns">
            <button class="btn-icon edit" onclick="editBooking(${b.id})" title="Edit">✎</button>
            <button class="btn-icon delete" onclick="deleteBooking(${b.id})" title="Hapus">✕</button>
          </div>
        </td>
      </tr>`;
    }).join('') : emptyRow(8);
  } catch (e) {
    showAlert('Gagal memuat pemesanan: ' + e.message, 'error');
  }
}

async function populateBookingSelects() {
  if (!cachedCustomers.length) cachedCustomers = await window.API.customers.getAll();
  if (!cachedRooms.length)     cachedRooms     = await window.API.rooms.getAll();

  const custSel = document.getElementById('bookCustomer');
  const roomSel = document.getElementById('bookRoom');

  custSel.innerHTML = '<option value="">Pilih Pelanggan</option>' +
    cachedCustomers.map(c => `<option value="${c.id}">${c.nama}</option>`).join('');

  const available = cachedRooms.filter(r => r.status === 'Tersedia');
  roomSel.innerHTML = '<option value="">Pilih Kamar</option>' +
    available.map(r => `<option value="${r.id}" data-harga="${r.harga}">${r.nomorKamar} — ${r.tipe} (${formatRupiah(r.harga)}/malam)</option>`).join('');
}

function resetBookForm() {
  document.getElementById('bookId').value        = '';
  document.getElementById('bookCustomer').value  = '';
  document.getElementById('bookRoom').value      = '';
  document.getElementById('bookCheckin').value   = '';
  document.getElementById('bookCheckout').value  = '';
  document.getElementById('bookStatus').value    = 'Aktif';
  document.getElementById('bookingSummary').style.display = 'none';
  document.getElementById('bookModalTitle').textContent = 'Buat Pemesanan';
}

function editBooking(id) {
  const b = cachedBookings.find(b => b.id === id);
  if (!b) return;
  populateBookingSelects().then(() => {
    document.getElementById('bookId').value        = b.id;
    document.getElementById('bookCustomer').value  = b.customerId;
    document.getElementById('bookRoom').value      = b.roomId;
    document.getElementById('bookCheckin').value   = b.checkIn;
    document.getElementById('bookCheckout').value  = b.checkOut;
    document.getElementById('bookStatus').value    = b.status;
    document.getElementById('bookModalTitle').textContent = 'Edit Pemesanan';
    calculateTotal();
    openModal('bookModal');
  });
}

async function deleteBooking(id) {
  if (!confirm('Hapus pemesanan ini?')) return;
  try {
    await window.API.bookings.delete(id);
    showAlert('Pemesanan berhasil dihapus.', 'success');
    loadBookings();
  } catch (e) {
    showAlert('Gagal menghapus pemesanan: ' + e.message, 'error');
  }
}

// ─── BOOKING CALCULATOR ───────────────────────────────────────────────────────

function bindBookingCalculator() {
  ['bookRoom', 'bookCheckin', 'bookCheckout'].forEach(id => {
    document.getElementById(id).addEventListener('change', calculateTotal);
  });
}

function calculateTotal() {
  const roomSel  = document.getElementById('bookRoom');
  const checkin  = document.getElementById('bookCheckin').value;
  const checkout = document.getElementById('bookCheckout').value;
  const summary  = document.getElementById('bookingSummary');

  if (!roomSel.value || !checkin || !checkout) { summary.style.display = 'none'; return; }

  const selectedOpt = roomSel.options[roomSel.selectedIndex];
  const harga = parseInt(selectedOpt.dataset.harga) || 0;
  const d1 = new Date(checkin), d2 = new Date(checkout);
  const durasi = Math.ceil((d2 - d1) / (1000 * 60 * 60 * 24));

  if (durasi <= 0) { summary.style.display = 'none'; return; }

  const total = harga * durasi;
  document.getElementById('sumDurasi').textContent = `${durasi} malam`;
  document.getElementById('sumHarga').textContent  = formatRupiah(harga);
  document.getElementById('sumTotal').textContent  = formatRupiah(total);
  summary.style.display = 'flex';
}

// ─── FORM SUBMISSIONS ────────────────────────────────────────────────────────

function bindForms() {

  // Room form
  document.getElementById('roomForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('roomId').value;
    const data = {
      nomorKamar: document.getElementById('roomNomor').value,
      tipe:       document.getElementById('roomTipe').value,
      harga:      parseInt(document.getElementById('roomHarga').value),
      status:     document.getElementById('roomStatus').value,
    };
    try {
      if (id) {
        await window.API.rooms.update(parseInt(id), data);
        showAlert('Kamar berhasil diperbarui!', 'success');
      } else {
        await window.API.rooms.create(data);
        showAlert('Kamar berhasil ditambahkan!', 'success');
      }
      closeModal('roomModal');
      loadRooms();
    } catch (err) {
      showAlert('Error: ' + err.message, 'error');
    }
  });

  // Customer form
  document.getElementById('custForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('custId').value;
    const data = {
      nama:       document.getElementById('custNama').value,
      email:      document.getElementById('custEmail').value,
      telepon:    document.getElementById('custTelepon').value,
      membership: document.getElementById('custMember').value,
    };
    try {
      if (id) {
        await window.API.customers.update(parseInt(id), data);
        showAlert('Pelanggan berhasil diperbarui!', 'success');
      } else {
        await window.API.customers.create(data);
        showAlert('Pelanggan berhasil ditambahkan!', 'success');
      }
      closeModal('custModal');
      loadCustomers();
    } catch (err) {
      showAlert('Error: ' + err.message, 'error');
    }
  });

  // Booking form
  document.getElementById('bookForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id       = document.getElementById('bookId').value;
    const checkin  = document.getElementById('bookCheckin').value;
    const checkout = document.getElementById('bookCheckout').value;
    const roomSel  = document.getElementById('bookRoom');
    const harga    = parseInt(roomSel.options[roomSel.selectedIndex]?.dataset.harga) || 0;
    const durasi   = Math.ceil((new Date(checkout) - new Date(checkin)) / 86400000);

    const data = {
      customerId: parseInt(document.getElementById('bookCustomer').value),
      roomId:     parseInt(roomSel.value),
      checkIn:    checkin,
      checkOut:   checkout,
      totalHarga: harga * durasi,
      status:     document.getElementById('bookStatus').value,
    };
    try {
      if (id) {
        await window.API.bookings.update(parseInt(id), data);
        showAlert('Pemesanan berhasil diperbarui!', 'success');
      } else {
        await window.API.bookings.create(data);
        showAlert('Pemesanan berhasil dibuat!', 'success');
      }
      closeModal('bookModal');
      loadBookings();
    } catch (err) {
      showAlert('Error: ' + err.message, 'error');
    }
  });
}
