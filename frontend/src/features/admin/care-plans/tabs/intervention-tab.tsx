import React, { useState } from 'react';
import type { Intervention } from '../types';
import { Plus, Pencil, Trash2 } from 'lucide-react';

interface InterventionTabProps {
  carePlanId: number;
}

const InterventionTab: React.FC<InterventionTabProps> = ({ carePlanId }) => {
  const [interventions, setInterventions] = useState<Intervention[]>([
    {
      id: 1,
      goalId: 1,
      description: 'Physical therapy sessions twice daily',
      frequency: 'Daily',
      notes: 'Focus on lower body strength',
    },
    {
      id: 2,
      goalId: 1,
      description: 'Assisted walking with support',
      frequency: '3 times daily',
      notes: 'Use walking frame',
    },
  ]);

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Interventions</h2>
        <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition flex items-center gap-2">
          <Plus className="w-4 h-4" />
          Add Intervention
        </button>
      </div>

      <div className="grid grid-cols-1 gap-4">
        {interventions.map((intervention) => (
          <div key={intervention.id} className="bg-white border border-gray-200 rounded-xl p-4">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <h3 className="font-medium">{intervention.description}</h3>
                <div className="flex flex-col gap-1 mt-2 text-sm text-gray-600">
                  <div>
                    <span className="font-medium">Frequency:</span> {intervention.frequency}
                  </div>
                  {intervention.notes && (
                    <div>
                      <span className="font-medium">Notes:</span> {intervention.notes}
                    </div>
                  )}
                </div>
              </div>
              <div className="flex gap-2">
                <button className="text-blue-600 hover:text-blue-700 p-1">
                  <Pencil className="w-4 h-4" />
                </button>
                <button className="text-red-600 hover:text-red-700 p-1">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default InterventionTab;