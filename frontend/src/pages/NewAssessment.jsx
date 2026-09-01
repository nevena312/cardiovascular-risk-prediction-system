import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../services/api.js';
import ContributionList from '../components/ContributionList.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { ErrorState } from '../components/LoadingError.jsx';
import { patientName, percent } from '../utils/format.js';

const initialInputs = {
  sex: 1,
  age: 51,
  hyperlipidemia: 1,
  smoker: 0,
  diabetes: 1,
  obesity: 1,
  hypertension: 1
};

const binaryFields = [
  ['hyperlipidemia', 'Hiperlipidemija'],
  ['smoker', 'Pušač'],
  ['diabetes', 'Dijabetes'],
  ['obesity', 'Gojaznost'],
  ['hypertension', 'Hipertenzija']
];

export default function NewAssessment() {
  const [patients, setPatients] = useState([]);
  const [selectedPatientId, setSelectedPatientId] = useState('');
  const [createNew, setCreateNew] = useState(false);
  const [newPatient, setNewPatient] = useState({ firstName: '', lastName: '' });
  const [inputs, setInputs] = useState(initialInputs);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showAll, setShowAll] = useState(false);

  useEffect(() => {
    api.patients().then((items) => {
      setPatients(items);
      if (items[0]) setSelectedPatientId(String(items[0].id));
    }).catch((err) => setError(err.message));
  }, []);

  const selectedPatient = useMemo(
    () => patients.find((patient) => String(patient.id) === String(selectedPatientId)),
    [patients, selectedPatientId]
  );

  async function submit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);
    try {
      let patientId = selectedPatientId;
      if (createNew) {
        const created = await api.createPatient(newPatient);
        patientId = created.id;
        setPatients((items) => [created, ...items]);
        setSelectedPatientId(String(created.id));
        setCreateNew(false);
      }
      if (!patientId) throw new Error('Izaberite ili kreirajte pacijenta.');
      const saved = await api.createAssessment(patientId, inputs);
      setResult(saved);
      setShowAll(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Nova procena</h1>
          <p>Unose se isključivo vrednosti koje koristi sačuvani DeepNetts model.</p>
        </div>
      </div>
      {error && <ErrorState message={error} />}
      <form className="form-grid" onSubmit={submit}>
        <div className="panel">
          <h2>Pacijent</h2>
          <label className="checkbox-line">
            <input type="checkbox" checked={createNew} onChange={(event) => setCreateNew(event.target.checked)} />
            Kreiraj novog pacijenta
          </label>
          {createNew ? (
            <div className="two-col">
              <label>Ime<input value={newPatient.firstName} onChange={(event) => setNewPatient({ ...newPatient, firstName: event.target.value })} /></label>
              <label>Prezime<input value={newPatient.lastName} onChange={(event) => setNewPatient({ ...newPatient, lastName: event.target.value })} /></label>
            </div>
          ) : (
            <label>Postojeći pacijent
              <select value={selectedPatientId} onChange={(event) => setSelectedPatientId(event.target.value)}>
                <option value="">Izaberite pacijenta</option>
                {patients.map((patient) => <option key={patient.id} value={patient.id}>{patient.patientCode} - {patientName(patient)}</option>)}
              </select>
            </label>
          )}
          {!createNew && selectedPatient && <p className="muted">Izabran pacijent: {selectedPatient.patientCode}</p>}
        </div>

        <div className="panel">
          <h2>Parametri modela</h2>
          <div className="two-col">
            <label>Pol
              <select value={inputs.sex} onChange={(event) => setInputs({ ...inputs, sex: Number(event.target.value) })}>
                <option value={1}>Muško</option>
                <option value={0}>Žensko</option>
              </select>
            </label>
            <label>Starost
              <input type="number" min="18" max="110" value={inputs.age} onChange={(event) => setInputs({ ...inputs, age: Number(event.target.value) })} />
            </label>
          </div>
          {binaryFields.map(([key, label]) => (
            <BinaryChoice key={key} label={label} value={inputs[key]} onChange={(value) => setInputs({ ...inputs, [key]: value })} />
          ))}
          <button className="primary-button full-width" disabled={loading}>{loading ? 'Čuvanje procene...' : 'Pokreni procenu modela'}</button>
        </div>
      </form>

      {result && (
        <div className="panel result-panel">
          <div className="result-head">
            <div>
              <h2>Procena modela</h2>
              <p>{result.patient.patientCode} - {patientName(result.patient)}</p>
            </div>
            <Link to={`/assessments/${result.id}`}>Detalji procene</Link>
          </div>
          <div className="result-summary">
            <strong>{percent(result.predictedProbability)}</strong>
            <StatusBadge value={result.predictedClass} />
          </div>
          <h3>Faktori sa najvećim uticajem na procenu modela</h3>
          <ContributionList contributions={result.contributions} limit={3} />
          <button type="button" className="text-button" onClick={() => setShowAll(!showAll)}>
            {showAll ? 'Sakrij sve faktore' : 'Prikaži sve faktore'}
          </button>
          {showAll && <ContributionList contributions={result.contributions} />}
        </div>
      )}
    </section>
  );
}

function BinaryChoice({ label, value, onChange }) {
  return (
    <div className="binary-row">
      <span>{label}</span>
      <div className="segmented">
        <button type="button" className={value === 1 ? 'active' : ''} onClick={() => onChange(1)}>Da</button>
        <button type="button" className={value === 0 ? 'active' : ''} onClick={() => onChange(0)}>Ne</button>
      </div>
    </div>
  );
}
