import React from 'react';
import { ArrowDown, ArrowUp } from 'lucide-react';
import { featureValueLabel, pp } from '../utils/format.js';

export default function ContributionList({ contributions = [], limit }) {
  const visible = limit ? contributions.slice(0, limit) : contributions;
  return (
    <div className="contribution-list">
      {visible.map((item) => {
        const positive = item.contribution >= 0;
        return (
          <div className="contribution-row" key={`${item.rank}-${item.featureName}`}>
            <span className={`impact-icon ${positive ? 'up' : 'down'}`}>
              {positive ? <ArrowUp size={16} /> : <ArrowDown size={16} />}
            </span>
            <span>{featureValueLabel(item.featureName, item.originalValue)}</span>
            <strong className={positive ? 'positive-text' : 'negative-text'}>{pp(item.contribution)}</strong>
          </div>
        );
      })}
    </div>
  );
}
