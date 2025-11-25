const API_BASE = '/api/logs';
const AUTO_REFRESH_INTERVAL = 5000;

const dom = {
    totalLogs: document.getElementById('total-logs'),
    errorCount: document.getElementById('error-count'),
    lastUpdated: document.getElementById('last-updated'),
    status: document.getElementById('status-indicator'),
    tableBody: document.getElementById('logs-body'),
    emptyState: document.getElementById('logs-empty'),
    toast: document.getElementById('toast'),
    refreshBtn: document.getElementById('refresh-btn'),
    autoRefreshBtn: document.getElementById('auto-refresh-btn'),
    filtersForm: document.getElementById('filters'),
    query: document.getElementById('query'),
    level: document.getElementById('level'),
    instance: document.getElementById('instance'),
    size: document.getElementById('size')
};

let autoRefreshHandle = null;
let autoRefreshEnabled = true;

const formatTimestamp = (isoString) => {
    if (!isoString) {
        return '—';
    }
    try {
        return new Date(isoString).toLocaleString();
    } catch {
        return isoString;
    }
};

const setStatus = (state) => {
    dom.status.textContent = state === 'loading' ? 'Loading…' : state === 'ready' ? 'Live' : 'Idle';
    dom.status.className = `status ${state === 'ready' ? 'online' : 'offline'}`;
};

const showToast = (message) => {
    dom.toast.textContent = message;
    dom.toast.classList.remove('hidden');
    setTimeout(() => dom.toast.classList.add('hidden'), 4000);
};

const fetchJSON = async (url) => {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
    }
    return response.json();
};

const refreshStats = async () => {
    try {
        const stats = await fetchJSON(`${API_BASE}/stats`);
        dom.totalLogs.textContent = stats.totalLogs ?? '0';
        dom.errorCount.textContent = stats.errorCount ?? '0';
    } catch (err) {
        showToast(`Stats error: ${err.message}`);
    }
};

const buildQueryString = () => {
    const params = new URLSearchParams();

    if (dom.query.value.trim()) {
        params.set('query', dom.query.value.trim());
    }

    if (dom.level.value) {
        params.set('level', dom.level.value);
    }

    if (dom.instance.value) {
        params.set('instance', dom.instance.value);
    }

    if (dom.size.value) {
        params.set('size', dom.size.value);
    }

    return params.toString() ? `?${params.toString()}` : '';
};

const renderRows = (logs) => {
    if (!logs.length) {
        dom.emptyState.classList.remove('hidden');
        dom.tableBody.innerHTML = '';
        return;
    }
    dom.emptyState.classList.add('hidden');
    dom.tableBody.innerHTML = logs.map((log) => `
        <tr>
            <td>${formatTimestamp(log.timestamp)}</td>
            <td><span class="badge ${log.level ?? 'INFO'}">${log.level ?? 'INFO'}</span></td>
            <td>${log.instance ?? '—'}</td>
            <td>${log.message ?? '—'}</td>
            <td>${log.correlationId ?? '—'}</td>
        </tr>
    `).join('');
};

const refreshLogs = async () => {
    setStatus('loading');
    try {
        const query = buildQueryString();
        const logs = await fetchJSON(`${API_BASE}/search${query}`);
        renderRows(logs);
        dom.lastUpdated.textContent = new Date().toLocaleTimeString();
        setStatus('ready');
    } catch (err) {
        renderRows([]);
        setStatus('idle');
        showToast(`Log fetch failed: ${err.message}`);
    }
};

const stopAutoRefresh = () => {
    if (autoRefreshHandle) {
        clearInterval(autoRefreshHandle);
        autoRefreshHandle = null;
    }
};

const startAutoRefresh = () => {
    stopAutoRefresh();
    autoRefreshHandle = setInterval(() => {
        refreshStats();
        refreshLogs();
    }, AUTO_REFRESH_INTERVAL);
};

const updateAutoRefreshButton = () => {
    dom.autoRefreshBtn.textContent = autoRefreshEnabled
        ? `Auto Refresh: On (${AUTO_REFRESH_INTERVAL / 1000}s)`
        : 'Auto Refresh: Off';
    dom.autoRefreshBtn.classList.toggle('primary', autoRefreshEnabled);
    dom.autoRefreshBtn.classList.toggle('ghost', !autoRefreshEnabled);
};

const toggleAutoRefresh = () => {
    autoRefreshEnabled = !autoRefreshEnabled;
    if (autoRefreshEnabled) {
        startAutoRefresh();
        refreshStats();
        refreshLogs();
    } else {
        stopAutoRefresh();
    }
    updateAutoRefreshButton();
};

const init = () => {
    dom.filtersForm.addEventListener('submit', (event) => {
        event.preventDefault();
        refreshLogs();
        refreshStats();
    });

    dom.refreshBtn.addEventListener('click', () => {
        refreshLogs();
        refreshStats();
    });

    dom.autoRefreshBtn.addEventListener('click', toggleAutoRefresh);

    updateAutoRefreshButton();
    refreshStats();
    refreshLogs();
    startAutoRefresh();
};

document.addEventListener('DOMContentLoaded', init);

