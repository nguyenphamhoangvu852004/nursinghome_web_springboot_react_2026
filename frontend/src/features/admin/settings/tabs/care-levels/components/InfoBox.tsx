import React from 'react';
import { Info } from 'lucide-react';

const InfoBox: React.FC = () => {
  return (
    <div className="mt-6 bg-blue-50 border border-blue-200 rounded-2xl p-4 flex gap-3">
      <div className="text-blue-500 mt-0.5">
        <Info className="w-5 h-5" />
      </div>
      <div className="text-sm text-blue-700">
        4 tiers are fixed by the scoring model and cannot be added or removed. 
        <strong> Only Daily Rate and Effective Date are editable.</strong>
      </div>
    </div>
  );
};

export default InfoBox;
