const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    let message = 'Zahtev nije uspeo.';
    try {
      const body = await response.json();
      message = body.details?.[0] || body.error || message;
    } catch {
      message = response.statusText || message;
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export const api = {
  dashboard: () => request('/dashboard'),
  patients: (search = '') => request(`/patients${search ? `?search=${encodeURIComponent(search)}` : ''}`),
  createPatient: (payload) => request('/patients', { method: 'POST', body: JSON.stringify(payload) }),
  patient: (id) => request(`/patients/${id}`),
  patientAssessments: (id) => request(`/patients/${id}/assessments`),
  createAssessment: (patientId, payload) => request(`/patients/${patientId}/assessments`, { method: 'POST', body: JSON.stringify(payload) }),
  assessment: (id) => request(`/assessments/${id}`),
  assessments: () => request('/assessments')
};
