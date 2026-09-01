export const percent = (value) => `${(Number(value || 0) * 100).toFixed(1)}%`;
export const pp = (value) => `${value >= 0 ? '+' : ''}${(Number(value || 0) * 100).toFixed(1)} pp`;

export function dateTime(value) {
  if (!value) return 'Nema procene';
  return new Intl.DateTimeFormat('sr-RS', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value));
}

export function classLabel(value) {
  return Number(value) === 1 ? 'Pozitivna klasifikacija' : 'Negativna klasifikacija';
}

export function patientName(patient) {
  const name = [patient?.firstName, patient?.lastName].filter(Boolean).join(' ');
  return name || 'Bez imena';
}

export function featureValueLabel(featureName, value) {
  const numeric = Number(value);
  const labels = {
    Sex: numeric === 1 ? 'Pol: muško' : 'Pol: žensko',
    Age: `Starost: ${Math.round(numeric)} godina`,
    Hyperlipidemia: numeric === 1 ? 'Prisustvo hiperlipidemije' : 'Odsustvo hiperlipidemije',
    Smoker: numeric === 1 ? 'Pušač' : 'Nepušač',
    Diabetes: numeric === 1 ? 'Prisustvo dijabetesa' : 'Odsustvo dijabetesa',
    Obesity: numeric === 1 ? 'Prisustvo gojaznosti' : 'Odsustvo gojaznosti',
    Hypertension: numeric === 1 ? 'Prisustvo hipertenzije' : 'Odsustvo hipertenzije'
  };
  return labels[featureName] || featureName;
}

export function binaryLabel(value) {
  return Number(value) === 1 ? 'Da' : 'Ne';
}
