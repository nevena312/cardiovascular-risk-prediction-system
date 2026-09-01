import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../services/api.js';
import ContributionList from '../components/ContributionList.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { ErrorState, Loading } from '../components/LoadingError.jsx';
import { binaryLabel, dateTime, patientName, percent } from '../utils/format.js';

export default function AssessmentDetails() {
  const { id } = useParams();
  const [assessment, setAssessment] = useState(null);
  const [showAll, setShowAll] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    api.assessment(id).then(setAssessment).catch((err) => setError(err.message));
  }, [id]);

  if (error) return <ErrorState message={error} />;
  if (!assessment) return <Loading />;

  const rows = [
    ['Pol', assessment.sex === 1 ? 'Muško' : 'Žensko'],
    ['Starost', `${assessment.age} godina`],
    ['Hiperlipidemija', binaryLabel(assessment.hyperlipidemia)],
    ['Pušač', binaryLabel(assessment.smoker)],
    ['Dijabetes', binaryLabel(assessment.diabetes)],
    ['Gojaznost', binaryLabel(assessment.obesity)],
    ['Hipertenzija', binaryLabel(assessment.hypertension)]
  ];

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Detalji procene</h1>
          <p>{dateTime(assessment.createdAt)}</p>
        </div>
        <Link className="secondary-button" to={`/patients/${assessment.patient.id}`}>Pacijent</Link>
      </div>

      <div className="metric-grid">
        <Metric label="Pacijent" value={`${assessment.patient.patientCode} · ${patientName(assessment.patient)}`} />
        <Metric label="Verovatnoća modela" value={percent(assessment.predictedProbability)} />
        <Metric label="Klasifikacija modela" value={<StatusBadge value={assessment.predictedClass} />} />
      </div>

      <div className="content-grid">
        <div className="panel">
          <h2>Ulazne vrednosti</h2>
          <dl className="definition-list">
            {rows.map(([label, value]) => (
              <div key={label}><dt>{label}</dt><dd>{value}</dd></div>
            ))}
          </dl>
        </div>

        <div className="panel">
          <h2>Faktori sa najvećim uticajem na procenu modela</h2>
          <ContributionList contributions={assessment.contributions} limit={3} />
          <p className="muted">Vrednosti predstavljaju lokalnu osetljivost modela u procentnim poenima i nisu medicinski uzročni efekti.</p>
          <button type="button" className="text-button" onClick={() => setShowAll(!showAll)}>
            {showAll ? 'Sakrij sve faktore' : 'Prikaži sve faktore'}
          </button>
          {showAll && <ContributionList contributions={assessment.contributions} />}
        </div>
      </div>
    </section>
  );
}

function Metric({ label, value }) {
  return <div className="metric"><span>{label}</span><strong>{value}</strong></div>;
}
