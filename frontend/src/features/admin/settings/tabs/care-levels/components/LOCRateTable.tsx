import React from 'react';
import type { CareLevelRateResponse } from '../../../../facilities/types';
import { Pencil, Save, X } from 'lucide-react';
import { TIERS, CURRENT_USER } from '../../../../facilities/utils/constants';

interface LOCRateTableProps {
  rates: CareLevelRateResponse[];
  editingId: number | null;
  editData: { dailyRate: string; effectiveFrom: string };
  onEdit: (rate: CareLevelRateResponse) => void;
  onSave: (rateId: number) => void;
  onCancel: () => void;
  onEditDataChange: (data: { dailyRate: string; effectiveFrom: string }) => void;
}

const LOCRateTable: React.FC<LOCRateTableProps> = ({
  rates,
  editingId,
  editData,
  onEdit,
  onSave,
  onCancel,
  onEditDataChange,
}) => {
  return (
    <div className="mt-8 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      <table className="w-full">
        <thead>
          <tr className="border-b bg-gray-50">
            <th className="text-left py-4 px-6 font-medium text-gray-600">Level</th>
            <th className="text-left py-4 px-6 font-medium text-gray-600">Score Range</th>
            <th className="text-left py-4 px-6 font-medium text-gray-600">Daily Rate</th>
            <th className="text-left py-4 px-6 font-medium text-gray-600">Effective Date</th>
            <th className="text-left py-4 px-6 font-medium text-gray-600">Last Updated By</th>
            <th className="w-32"></th>
          </tr>
        </thead>
        <tbody className="divide-y">
          {rates.map((rate) => {
            const tierInfo = TIERS.find(t => t.id === rate.care_level_id) || TIERS[0];
            const isEditing = editingId === rate.id;
            
            return (
              <tr key={rate.id} className="hover:bg-gray-50">
                <td className="px-6 py-5">
                  <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                    {tierInfo.name}
                  </span>
                </td>
                <td className="px-6 py-5 text-gray-700">{tierInfo.range}</td>
                <td className="px-6 py-5">
                  {isEditing ? (
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      value={editData.dailyRate}
                      onChange={(e) => onEditDataChange({ 
                        ...editData, 
                        dailyRate: e.target.value 
                      })}
                      className="w-32 px-3 py-1 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  ) : (
                    <span className="font-semibold text-emerald-600">
                      ${rate.daily_rate.toFixed(2)}
                    </span>
                  )}
                </td>
                <td className="px-6 py-5">
                  {isEditing ? (
                    <input
                      type="date"
                      value={editData.effectiveFrom}
                      onChange={(e) => onEditDataChange({ 
                        ...editData, 
                        effectiveFrom: e.target.value 
                      })}
                      className="px-3 py-1 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    />
                  ) : (
                    <span className="text-gray-700">{rate.effective_from}</span>
                  )}
                </td>
                <td className="px-6 py-5 text-gray-700">
                  {CURRENT_USER.name}, {CURRENT_USER.role}
                </td>
                <td className="px-6 py-5 text-right">
                  {isEditing ? (
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => onSave(rate.id)}
                        className="text-green-600 hover:text-green-700 font-medium text-sm flex items-center gap-1"
                      >
                        <Save className="w-4 h-4" />
                        Save
                      </button>
                      <button
                        onClick={onCancel}
                        className="text-gray-500 hover:text-gray-700 font-medium text-sm flex items-center gap-1"
                      >
                        <X className="w-4 h-4" />
                        Cancel
                      </button>
                    </div>
                  ) : (
                    <button
                      onClick={() => onEdit(rate)}
                      className="text-blue-600 hover:text-blue-700 font-medium text-sm flex items-center gap-1 ml-auto"
                    >
                      <Pencil className="w-4 h-4" />
                      Edit Rate
                    </button>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default LOCRateTable;
