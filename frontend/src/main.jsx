import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom';
import { Activity, ClipboardPlus, LayoutDashboard, Users } from 'lucide-react';
import Dashboard from './pages/Dashboard.jsx';
import Patients from './pages/Patients.jsx';
import NewAssessment from './pages/NewAssessment.jsx';
import PatientDetails from './pages/PatientDetails.jsx';
import AssessmentDetails from './pages/AssessmentDetails.jsx';
import './styles.css';

function AppShell() {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <aside className="sidebar">
          <div className="brand">
            <Activity size={28} aria-hidden="true" />
            <div>
              <strong>Koronarna klasifikacija</strong>
              <span>DeepNetts lokalna aplikacija</span>
            </div>
          </div>
          <nav className="nav">
            <NavLink to="/" end><LayoutDashboard size={18} />Kontrolna tabla</NavLink>
            <NavLink to="/patients"><Users size={18} />Pacijenti</NavLink>
            <NavLink to="/assessments/new"><ClipboardPlus size={18} />Nova procena</NavLink>
          </nav>
          <p className="disclaimer">
            Ovaj sistem je razvijen u istraživačke i akademske svrhe. Rezultat predstavlja izlaz prediktivnog modela i ne predstavlja medicinsku dijagnozu niti zamenu za procenu zdravstvenog stručnjaka.
          </p>
        </aside>
        <main className="main">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/patients" element={<Patients />} />
            <Route path="/patients/:id" element={<PatientDetails />} />
            <Route path="/assessments/new" element={<NewAssessment />} />
            <Route path="/assessments/:id" element={<AssessmentDetails />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<AppShell />);
