const VALID_USER = 'admin';
const VALID_PASS = 'admin';
const SESSION_KEY = 'gnh_logged_in';

function checkSession() {
    if (sessionStorage.getItem(SESSION_KEY) === 'true') {
    document.getElementById('loginOverlay').style.display = 'none';
    }
}

function handleLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('loginError');
    const btn = document.getElementById('loginBtn');

    if (username === VALID_USER && password === VALID_PASS) {
        errorEl.style.display = 'none';
        btn.classList.add('loading');
        btn.textContent = 'Memverifikasi...';
        setTimeout(() => {
        sessionStorage.setItem(SESSION_KEY, 'true');
        const overlay = document.getElementById('loginOverlay');
        overlay.style.opacity = '0';
        overlay.style.transition = 'opacity 0.4s ease';
        setTimeout(() => overlay.style.display = 'none', 400);
        }, 700);
    } else {
        errorEl.style.display = 'block';
        document.getElementById('loginPassword').value = '';
        const wrap = document.querySelector('.login-card');
        wrap.classList.add('shake');
        setTimeout(() => wrap.classList.remove('shake'), 500);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    checkSession();
    ['loginUsername','loginPassword'].forEach(id => {
        document.getElementById(id).addEventListener('keydown', e => {
            if (e.key === 'Enter') handleLogin();
        });
    });
});