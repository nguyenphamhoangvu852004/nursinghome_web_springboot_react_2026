import React from 'react';
import { Save } from 'lucide-react';
import { useLOCRates } from './care-levels/useLOCRates';
import InfoBox from './care-levels/components/InfoBox';
import LOCRateTable from './care-levels/components/LOCRateTable';

export const CareLevelTab: React.FC = () => {
  const {
    rates,
    loading,
    editingId,
    editData,
    setEditData,
    handleEdit,
    handleSave,
    handleCancel,
    handleSaveAll,
  } = useLOCRates();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Loading...</div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto">
      {/* Breadcrumb */}
      <div className="text-sm text-gray-500 mb-2">
        Admin &gt; LOC Rates
      </div>
      
      <h1 className="text-3xl font-semibold mb-1">LOC Rate Table</h1>
      <p className="text-gray-600">
        Daily rate per Level of Care tier — score ranges are fixed by the clinical scoring model ($5E)
      </p>

      {/* Info Box */}
      <InfoBox />

      {/* Table */}
      <LOCRateTable
        rates={rates}
        editingId={editingId}
        editData={editData}
        onEdit={handleEdit}
        onSave={handleSave}
        onCancel={handleCancel}
        onEditDataChange={setEditData}
      />

      {/* Footer Note */}
      <div className="mt-6 text-sm text-gray-500 max-w-3xl">
        Levels cannot be added or deleted here — score-range thresholds are defined by the ADL/IADL scoring model (Master Plan $5E)
        and match M1-US-08 LOC Classification.
      </div>

      {/* Action Buttons */}
      <div className="flex justify-end gap-4 mt-10">
        <button
          onClick={() => window.location.reload()}
          className="px-8 py-3 border border-gray-300 rounded-2xl font-medium hover:bg-gray-50 transition"
        >
          Cancel
        </button>
        <button
          onClick={handleSaveAll}
          className="px-8 py-3 bg-blue-600 text-white rounded-2xl font-semibold hover:bg-blue-700 transition flex items-center gap-2"
        >
          <Save className="w-5 h-5" />
          Save Changes
        </button>
      </div>
    </div>
  );
};
