import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { Search } from 'lucide-react';
import { api } from '../services/api.js';
import { dateTime, patientName, percent } from '../utils/format.js';
import StatusBadge from '../components/StatusBadge.jsx';
import { ErrorState, Loading } from '../components/LoadingError.jsx';

export default function Patients() {
  const [patients, setPatients] = useState(null);
  const [search, setSearch] = useState('');
  const [sort, setSort] = useState('latestDate');
  const [error, setError] = useState('');

  useEffect(() => {
    api.patients(search).then(setPatients).catch((err) => setError(err.message));
  }, [search]);

  const sorted = useMemo(() => {
    if (!patients) return [];
    return [...patients].sort((a, b) => {
      const ap = a.latestAssessment;
      const bp = b.latestAssessment;
      if (sort === 'probDesc') return (bp?.predictedProbability ?? -1) - (ap?.predictedProbability ?? -1);
      if (sort === 'probAsc') return (ap?.predictedProbability ?? 2) - (bp?.predictedProbability ?? 2);
      if (sort === 'name') return patientName(a).localeCompare(patientName(b), 'sr');
      if (sort === 'code') return a.patientCode.localeCompare(b.patientCode, 'sr');
      return new Date(bp?.createdAt || 0) - new Date(ap?.createdAt || 0);
    });
  }, [patients, sort]);

  if (error) return <ErrorState message={error} />;
  if (!patients) return <Loading />;

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Pacijenti</h1>
          <p>Pretraga i pregled poslednje sačuvane procene za svakog pacijenta.</p>
        </div>
        <Link className="primary-button" to="/assessments/new">Nova procena</Link>
      </div>
      <div className="toolbar">
        <label className="search-box">
          <Search size={18} />
          <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Pretraga po kodu ili imenu" />
        </label>
        <select value={sort} onChange={(event) => setSort(event.target.value)}>
          <option value="latestDate">Poslednja procena</option>
          <option value="probDesc">Verovatnoća opadajuće</option>
          <option value="probAsc">Verovatnoća rastuće</option>
          <option value="code">Šifra pacijenta</option>
          <option value="name">Ime</option>
        </select>
      </div>
      <div className="panel">
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Šifra</th><th>Ime</th><th>Poslednja procena</th><th>Verovatnoća</th><th>Klasifikacija</th></tr>
            </thead>
            <tbody>
              {sorted.map((patient) => (
                <tr key={patient.id}>
                  <td><Link to={`/patients/${patient.id}`}>{patient.patientCode}</Link></td>
                  <td>{patientName(patient)}</td>
                  <td>{dateTime(patient.latestAssessment?.createdAt)}</td>
                  <td>{patient.latestAssessment ? percent(patient.latestAssessment.predictedProbability) : '-'}</td>
                  <td>{patient.latestAssessment ? <StatusBadge value={patient.latestAssessment.predictedClass} /> : '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
