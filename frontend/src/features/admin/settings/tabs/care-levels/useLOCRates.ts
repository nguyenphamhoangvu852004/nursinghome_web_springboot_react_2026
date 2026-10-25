import { useState, useEffect, useCallback } from 'react';
import toast from 'react-hot-toast';
import type { CareLevelRateResponse } from '../../../facilities/types';
import { careLevelApi } from './care-level-api';
import { TIERS } from '../../../facilities/utils/constants';

export const useLOCRates = () => {
  const [rates, setRates] = useState<CareLevelRateResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editData, setEditData] = useState({
    dailyRate: '',
    effectiveFrom: '',
  });

  const fetchRates = useCallback(async () => {
    try {
      setLoading(true);
      const allRates: CareLevelRateResponse[] = [];
      
      for (let i = 1; i <= 4; i++) {
        const response = await careLevelApi.getCareLevelRates(i);
        if (response && response.length > 0) {
          allRates.push(response[0]);
        } else {
          // Create default rate if none exists
          const createResponse = await careLevelApi.createCareLevelRate({
            care_level_id: i,
            facility_id: 1,
            daily_rate: parseFloat(TIERS[i - 1].defaultRate),
            effective_from: new Date().toISOString().split('T')[0],
          });
          allRates.push(createResponse);
        }
      }
      
      setRates(allRates);
    } catch (error) {
      console.error('Error fetching rates:', error);
      toast.error('Failed to load LOC rates');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchRates();
  }, [fetchRates]);

  const handleEdit = (rate: CareLevelRateResponse) => {
    setEditingId(rate.id);
    setEditData({
      dailyRate: rate.daily_rate.toString(),
      effectiveFrom: rate.effective_from,
    });
  };

  const handleCancel = () => {
    setEditingId(null);
    setEditData({ dailyRate: '', effectiveFrom: '' });
  };

  const handleSave = async (rateId: number) => {
    try {
      const updateData = {
        care_level_id: rates.find(r => r.id === rateId)?.care_level_id,
        daily_rate: parseFloat(editData.dailyRate),
        effective_from: editData.effectiveFrom,
        effective_to: null,
      };

      await careLevelApi.updateCareLevelRate(rateId, updateData);
      
      setRates(rates.map(rate => 
        rate.id === rateId 
          ? { 
              ...rate, 
              daily_rate: parseFloat(editData.dailyRate),
              effective_from: editData.effectiveFrom,
            }
          : rate
      ));
      
      setEditingId(null);
      toast.success('Rate updated successfully!');
    } catch (error) {
      console.error('Error updating rate:', error);
      toast.error('Failed to update rate');
    }
  };

  const handleSaveAll = async () => {
    try {
      // Check if there are any unsaved edits
      if (editingId) {
        await handleSave(editingId);
      }
      toast.success('✅ Changes saved successfully!');
    } catch (error) {
      toast.error('Failed to save changes');
    }
  };

  return {
    rates,
    loading,
    editingId,
    editData,
    TIERS,
    setEditData,
    handleEdit,
    handleSave,
    handleCancel,
    handleSaveAll,
    fetchRates,
  };
};
