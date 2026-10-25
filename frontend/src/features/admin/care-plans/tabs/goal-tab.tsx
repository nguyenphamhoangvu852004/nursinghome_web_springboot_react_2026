import React, { useState } from 'react';
import type { Goal } from '../types';
import { Plus, Pencil, Trash2 } from 'lucide-react';

interface GoalTabProps {
  carePlanId: number;
}

const GoalTab: React.FC<GoalTabProps> = ({ carePlanId }) => {
  const [goals, setGoals] = useState<Goal[]>([
    {
      id: 1,
      carePlanId: carePlanId,
      description: 'Improve mobility and independence',
      targetDate: '2026-12-31',
      status: 'IN_PROGRESS',
      progress: 60,
    },
    {
      id: 2,
      carePlanId: carePlanId,
      description: 'Enhance nutritional intake',
      targetDate: '2026-10-15',
      status: 'PENDING',
      progress: 20,
    },
  ]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACHIEVED':
        return 'bg-green-100 text-green-800';
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800';
      case 'PENDING':
        return 'bg-gray-100 text-gray-800';
      case 'NOT_ACHIEVED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Goals</h2>
        <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition flex items-center gap-2">
          <Plus className="w-4 h-4" />
          Add Goal
        </button>
      </div>

      <div className="space-y-4">
        {goals.map((goal) => (
          <div key={goal.id} className="bg-white border border-gray-200 rounded-xl p-4">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <h3 className="font-medium text-lg">{goal.description}</h3>
                <div className="flex items-center gap-4 mt-2 text-sm">
                  <span className="text-gray-500">Target: {goal.targetDate}</span>
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(goal.status)}`}>
                    {goal.status.replace('_', ' ')}
                  </span>
                </div>
                <div className="mt-3">
                  <div className="flex items-center gap-2">
                    <span className="text-sm text-gray-600">Progress: {goal.progress}%</span>
                    <div className="flex-1 h-2 bg-gray-200 rounded-full">
                      <div
                        className="h-2 bg-blue-600 rounded-full transition-all duration-300"
                        style={{ width: `${goal.progress}%` }}
                      />
                    </div>
                  </div>
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

export default GoalTab;