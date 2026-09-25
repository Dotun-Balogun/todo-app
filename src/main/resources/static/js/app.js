// Dark mode toggle. Applies immediately client-side, and saves the
// preference server-side (per user) via a small POST so it's remembered
// on their next login from any device.
function initThemeToggle() {
    const toggle = document.getElementById('theme-toggle');
    if (!toggle) return;

    toggle.addEventListener('click', () => {
        const root = document.documentElement;
        const isDark = root.getAttribute('data-theme') === 'dark';
        const next = isDark ? 'light' : 'dark';
        root.setAttribute('data-theme', next);
        toggle.setAttribute('aria-pressed', String(!isDark));

        fetch('/profile/theme?darkMode=' + (next === 'dark'), {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        }).catch(() => {
            // Non-fatal: the toggle still works for this session even if saving the
            // preference fails (e.g. brief network hiccup). Nothing to surface to the user.
        });
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initThemeToggle();
});
