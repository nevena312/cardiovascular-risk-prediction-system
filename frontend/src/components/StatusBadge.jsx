import React from 'react';
import { classLabel } from '../utils/format.js';

export default function StatusBadge({ value }) {
  const positive = Number(value) === 1;
  return <span className={`badge ${positive ? 'badge-positive' : 'badge-negative'}`}>{classLabel(value)}</span>;
}
