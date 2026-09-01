import React, { useEffect, useState } from 'react';
import { Bar, BarChart, CartesianGrid, Cell, Line, LineChart, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Link } from 'react-router-dom';
import { api } from '../services/api.js';
import { dateTime, percent } from '../utils/format.js';
import StatusBadge from '../components/StatusBadge.jsx';
import { ErrorState, Loading } from '../components/LoadingError.jsx';

const COLORS = ['#2563eb', '#dc2626', '#0f766e', '#7c3aed', '#ea580c'];

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.dashboard().then(setData).catch((err) => setError(err.message));
  }, []);

  if (error) return <ErrorState message={error} />;
  if (!data) return <Loading />;

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Kontrolna tabla</h1>
          <p>Statistika je zasnovana na procenama sačuvanim u ovoj lokalnoj aplikaciji.</p>
        </div>
      </div>

      <div className="metric-grid">
        <Metric label="Ukupno pacijenata" value={data.totalPatients} />
        <Metric label="Ukupno procena" value={data.totalAssessments} />
        <Metric label="Pacijenti sa pozitivnom poslednjom klasifikacijom" value={data.latestPositivePatients} />
        <Metric label="Udeo pozitivnih poslednjih klasifikacija" value={`${data.latestPositivePercentage.toFixed(1)}%`} />
        <Metric label="Prosečna verovatnoća poslednjih procena" value={percent(data.latestAverageProbability)} />
      </div>

      <div className="chart-grid">
        <div className="panel">
          <h2>Raspodela poslednjih klasifikacija</h2>
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={data.classificationDistribution} dataKey="value" nameKey="label" outerRadius={92} label>
                {data.classificationDistribution.map((entry, index) => <Cell key={entry.label} fill={COLORS[index]} />)}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="panel">
          <h2>Raspodela poslednjih verovatnoća modela</h2>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={data.probabilityBuckets}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="label" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="value" fill="#0f766e" />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="panel panel-wide">
          <h2>Broj procena kroz vreme</h2>
          <ResponsiveContainer width="100%" height={260}>
            <LineChart data={data.assessmentsOverTime}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="label" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Line type="monotone" dataKey="value" stroke="#2563eb" strokeWidth={3} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="panel">
        <h2>Skorašnje procene</h2>
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Pacijent</th><th>Datum</th><th>Verovatnoća</th><th>Klasifikacija</th></tr>
            </thead>
            <tbody>
              {data.recentAssessments.map((item) => (
                <tr key={item.id}>
                  <td><Link to={`/patients/${item.patientId}`}>{item.patientCode}</Link></td>
                  <td>{dateTime(item.createdAt)}</td>
                  <td>{percent(item.predictedProbability)}</td>
                  <td><StatusBadge value={item.predictedClass} /></td>
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
