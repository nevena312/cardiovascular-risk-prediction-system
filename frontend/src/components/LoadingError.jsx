import React from 'react';

export function Loading() {
  return <div className="state">Učitavanje podataka...</div>;
}

export function ErrorState({ message }) {
  return <div className="state state-error">{message || 'Došlo je do greške.'}</div>;
}
