import React, { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { api } from '../services/api.js';
import { binaryLabel, dateTime, patientName, percent } from '../utils/format.js';
import StatusBadge from '../components/StatusBadge.jsx';
import { ErrorState, Loading } from '../components/LoadingError.jsx';

export default function PatientDetails() {
  const { id } = useParams();
  const [patient, setPatient] = useState(null);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([api.patient(id), api.patientAssessments(id)])
      .then(([patientData, historyData]) => {
        setPatient(patientData);
        setHistory(historyData);
      })
      .catch((err) => setError(err.message));
  }, [id]);

  const chartData = useMemo(() => [...history].reverse().map((item) => ({
    label: dateTime(item.createdAt),
    probability: Number((item.predictedProbability * 100).toFixed(2))
  })), [history]);

  if (error) return <ErrorState message={error} />;
  if (!patient) return <Loading />;

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>{patient.patientCode}</h1>
          <p>{patientName(patient)} · Kreiran: {dateTime(patient.createdAt)}</p>
        </div>
        <Link className="primary-button" to="/assessments/new">Nova procena</Link>
      </div>

      {patient.latestAssessment && (
        <div className="metric-grid">
          <Metric label="Poslednja verovatnoća modela" value={percent(patient.latestAssessment.predictedProbability)} />
          <Metric label="Poslednja klasifikacija" value={<StatusBadge value={patient.latestAssessment.predictedClass} />} />
          <Metric label="Datum poslednje procene" value={dateTime(patient.latestAssessment.createdAt)} />
        </div>
      )}

      <div className="panel">
        <h2>Istorija procena modela</h2>
        <ResponsiveContainer width="100%" height={260}>
          <LineChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="label" hide={chartData.length > 5} />
            <YAxis domain={[0, 100]} unit="%" />
            <Tooltip />
            <Line type="monotone" dataKey="probability" stroke="#2563eb" strokeWidth={3} />
          </LineChart>
        </ResponsiveContainer>
      </div>

      <div className="panel">
        <h2>Kompletna istorija</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Datum</th><th>Verovatnoća</th><th>Klasifikacija</th><th>Ulazne vrednosti</th><th></th></tr>
            </thead>
            <tbody>
              {history.map((item) => (
                <tr key={item.id}>
                  <td>{dateTime(item.createdAt)}</td>
                  <td>{percent(item.predictedProbability)}</td>
                  <td><StatusBadge value={item.predictedClass} /></td>
                  <td>Pol {item.sex === 1 ? 'muško' : 'žensko'}, {item.age} god, HL {binaryLabel(item.hyperlipidemia)}, pušač {binaryLabel(item.smoker)}, dijabetes {binaryLabel(item.diabetes)}, gojaznost {binaryLabel(item.obesity)}, HTA {binaryLabel(item.hypertension)}</td>
                  <td><Link to={`/assessments/${item.id}`}>Otvori</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}

function Metric({ label, value }) {
  return <div className="metric"><span>{label}</span><strong>{value}</strong></div>;
}
