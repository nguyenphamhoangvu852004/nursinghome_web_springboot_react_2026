import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { 
  Info, 
  Edit, 
  Building2,
  Calendar,
  DollarSign
} from "lucide-react";
import { careLevelApi } from "../services/care-level-api";
import { facilitiesApi } from "@/services/facilities-api";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHead,
  TableCell,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import type { CareLevelResponse } from "../types";

// Helper mappings for the table matching the mockup
const getScoreRange = (code: string) => {
  switch (code) {
    case 'INDEPENDENT_LIVING': return '0 - 8';
    case 'ASSISTED_LIVING': return '9 - 16';
    case 'MEMORY_CARE': return '17 - 24';
    case 'SKILLED_NURSING': return '25 - 32';
    case 'HOSPICE': return '33 - 40';
    default: return 'N/A';
  }
};

const getLevelTierName = (code: string, name: string) => {
  switch (code) {
    case 'INDEPENDENT_LIVING': return 'Tier 1 – Low';
    case 'ASSISTED_LIVING': return 'Tier 2 – Moderate';
    case 'MEMORY_CARE': return 'Tier 3 – High';
    case 'SKILLED_NURSING': return 'Tier 4 – Total';
    case 'HOSPICE': return 'Tier 5 – Hospice';
    default: return name;
  }
};

export const LOCRatesPage = () => {
  const queryClient = useQueryClient();
  const [selectedFacilityId, setSelectedFacilityId] = useState<number>(1);
  const [editingRate, setEditingRate] = useState<{
    careLevelId: number;
    careLevelName: string;
    rateId?: number;
    dailyRate: number;
    effectiveFrom: string;
  } | null>(null);

  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [createNewRatePeriod, setCreateNewRatePeriod] = useState(false);

  // Queries
  const { data: careLevels = [], isLoading: isCareLevelsLoading } = useQuery({
    queryKey: ["careLevels"],
    queryFn: () => careLevelApi.getCareLevels(),
  });

  const { data: allRates = [], isLoading: isRatesLoading } = useQuery({
    queryKey: ["careLevelRates"],
    queryFn: () => careLevelApi.getCareLevelRates(),
  });

  const { data: facilitiesData, isLoading: isFacilitiesLoading } = useQuery({
    queryKey: ["facilities"],
    queryFn: () => facilitiesApi.getFacilities(0, 100),
  });

  const facilities = facilitiesData?.data || [];

  // Mutations
  const updateCareLevelStatusMutation = useMutation({
    mutationFn: ({ careLevelId, is_deleted }: { careLevelId: number; is_deleted: boolean }) =>
      careLevelApi.updateCareLevel(careLevelId, { is_deleted }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["careLevels"] });
      toast.success("Care level status updated successfully.");
    },
    onError: (err: any) => {
      toast.error(err.message || "Failed to update care level status.");
    }
  });

  const createRateMutation = useMutation({
    mutationFn: careLevelApi.createCareLevelRate,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["careLevelRates"] });
      toast.success("Care level rate created successfully.");
      setIsEditModalOpen(false);
    },
    onError: (err: any) => {
      toast.error(err.message || "Failed to create care level rate.");
    }
  });

  const updateRateMutation = useMutation({
    mutationFn: ({ rateId, data }: { rateId: number; data: any }) =>
      careLevelApi.updateCareLevelRate(rateId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["careLevelRates"] });
      toast.success("Care level rate updated successfully.");
      setIsEditModalOpen(false);
    },
    onError: (err: any) => {
      toast.error(err.message || "Failed to update care level rate.");
    }
  });

  const seedRatesMutation = useMutation({
    mutationFn: careLevelApi.seedSampleRates,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["careLevelRates"] });
      toast.success("Sample rate data seeded successfully.");
    },
    onError: (err: any) => {
      toast.error(err.message || "Failed to seed sample rate data.");
    }
  });

  // Filter rates for the selected facility
  const facilityRates = allRates.filter(
    (rate) => rate.facility_id === selectedFacilityId
  );

  // Helper to find the active rate for a care level
  const getActiveRateForLevel = (careLevelId: number) => {
    const levelRates = facilityRates.filter(
      (r) => r.care_level_id === careLevelId
    );
    if (levelRates.length === 0) return null;

    // Find active rate by checking date ranges, fallback to the latest rate
    const today = new Date().toISOString().split("T")[0];
    const activeRate = levelRates.find(
      (r) => r.effective_from <= today && (!r.effective_to || r.effective_to >= today)
    );
    
    if (activeRate) return activeRate;

    // Fallback: sort by effective_from descending and take the first one
    return [...levelRates].sort(
      (a, b) => new Date(b.effective_from).getTime() - new Date(a.effective_from).getTime()
    )[0];
  };

  const handleEditRateClick = (careLevelId: number, levelName: string) => {
    const activeRate = getActiveRateForLevel(careLevelId);
    setCreateNewRatePeriod(false);
    if (activeRate) {
      setEditingRate({
        careLevelId,
        careLevelName: levelName,
        rateId: activeRate.id,
        dailyRate: activeRate.daily_rate,
        effectiveFrom: activeRate.effective_from,
      });
    } else {
      // Default initial date is today
      const today = new Date().toISOString().split("T")[0];
      setEditingRate({
        careLevelId,
        careLevelName: levelName,
        dailyRate: 0,
        effectiveFrom: today,
      });
    }
    setIsEditModalOpen(true);
  };

  const handleSaveRate = async () => {
    if (!editingRate) return;

    if (editingRate.dailyRate <= 0) {
      toast.error("Please enter a value greater than 0.");
      return;
    }

    if (!editingRate.effectiveFrom) {
      toast.error("Please select an effective date.");
      return;
    }

    if (createNewRatePeriod || !editingRate.rateId) {
      // Create new rate record
      await createRateMutation.mutateAsync({
        care_level_id: editingRate.careLevelId,
        facility_id: selectedFacilityId,
        daily_rate: editingRate.dailyRate,
        effective_from: editingRate.effectiveFrom,
      });
    } else {
      // Update existing rate record
      await updateRateMutation.mutateAsync({
        rateId: editingRate.rateId,
        data: {
          care_level_id: editingRate.careLevelId,
          facility_id: selectedFacilityId,
          daily_rate: editingRate.dailyRate,
          effective_from: editingRate.effectiveFrom,
        },
      });
    }
  };

  const handleToggleCareLevelStatus = (careLevel: CareLevelResponse) => {
    updateCareLevelStatusMutation.mutate({
      careLevelId: careLevel.id,
      is_deleted: !careLevel.is_deleted
    });
  };

  const handleSeedSampleData = () => {
    seedRatesMutation.mutate();
  };

  const isLoading = isCareLevelsLoading || isRatesLoading || isFacilitiesLoading;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
        <span className="ml-3 text-lg font-medium text-muted-foreground">Loading LOC rates data...</span>
      </div>
    );
  }

  // Filter out care levels that are soft deleted in rates page, but keep them for settings
  const activeCareLevels = careLevels.filter((lvl) => !lvl.is_deleted);

  return (
    <div className="space-y-6">
      {/* Top Header & Facility Switcher */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-border pb-5">
        <div>
          <div className="text-sm text-muted-foreground font-medium flex items-center gap-1.5">
            Admin <span className="text-xs">/</span> LOC Rates
          </div>
          <h1 className="text-3xl font-semibold tracking-tight mt-1">LOC (Level of Care) Rates Table</h1>
        </div>

        <div className="flex items-center gap-3">
          {facilities.length > 0 && (
            <div className="flex items-center gap-2">
              <Building2 className="w-5 h-5 text-muted-foreground" />
              <Select
                value={String(selectedFacilityId)}
                onValueChange={(val) => setSelectedFacilityId(Number(val))}
              >
                <SelectTrigger className="w-[240px] bg-card border-input">
                  <SelectValue placeholder="Select Facility" />
                </SelectTrigger>
                <SelectContent>
                  {facilities.map((fac) => (
                    <SelectItem key={fac.id} value={String(fac.id)}>
                      {fac.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          )}
          {facilityRates.length === 0 && (
            <Button variant="outline" size="sm" onClick={handleSeedSampleData} disabled={seedRatesMutation.isPending}>
              {seedRatesMutation.isPending ? "Seeding..." : "Seed Sample Rate Data"}
            </Button>
          )}
        </div>
      </div>

      <Tabs defaultValue="rates" className="space-y-6">
        <TabsList className="grid w-full grid-cols-2 max-w-[400px]">
          <TabsTrigger value="rates">Care Rates</TabsTrigger>
          <TabsTrigger value="settings">Care Level Settings</TabsTrigger>
        </TabsList>

        <TabsContent value="rates" className="space-y-6">
          {/* Informational Alert (matches mockup) */}
          <div className="flex items-start gap-3 p-4 bg-[#eff6ff] text-[#1e40af] border border-[#bfdbfe] rounded-xl">
            <Info className="w-5 h-5 shrink-0 mt-0.5" />
            <div className="text-sm font-medium leading-normal">
              These {activeCareLevels.length} levels are fixed according to the clinical scoring model and cannot be added or removed manually. Only the Daily Rate and Effective Date fields are adjustable.
            </div>
          </div>

          {/* LOC Rates Table */}
          <div className="bg-card rounded-2xl border border-border shadow-sm overflow-hidden">
            <Table>
              <TableHeader className="bg-muted/40">
                <TableRow>
                  <TableHead className="w-[250px] font-semibold text-muted-foreground">Care Level</TableHead>
                  <TableHead className="w-[180px] font-semibold text-muted-foreground text-center">Score Range</TableHead>
                  <TableHead className="w-[200px] font-semibold text-muted-foreground text-right">Daily Rate</TableHead>
                  <TableHead className="w-[180px] font-semibold text-muted-foreground text-center">Effective Date</TableHead>
                  <TableHead className="w-[250px] font-semibold text-muted-foreground">Updated By</TableHead>
                  <TableHead className="w-[120px] text-right"></TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {activeCareLevels.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="text-center py-10 text-muted-foreground font-medium">
                      No care levels are currently active. Please go to the Settings tab to enable them.
                    </TableCell>
                  </TableRow>
                ) : (
                  activeCareLevels.map((lvl) => {
                    const activeRate = getActiveRateForLevel(lvl.id);
                    return (
                      <TableRow key={lvl.id} className="hover:bg-muted/30 transition-colors">
                        <TableCell className="font-semibold text-foreground py-4">
                          <div className="flex flex-col">
                            <span className="text-[15px]">
                              {getLevelTierName(lvl.level_code, lvl.level_name)}
                            </span>
                            <span className="text-[11px] text-muted-foreground mt-0.5">
                              {lvl.level_name}
                            </span>
                          </div>
                        </TableCell>
                        <TableCell className="text-center font-medium text-muted-foreground">
                          {getScoreRange(lvl.level_code)}
                        </TableCell>
                        <TableCell className="text-right font-bold text-foreground text-base">
                          {activeRate ? (
                            `$${activeRate.daily_rate.toFixed(2)}`
                          ) : (
                            <span className="text-sm font-normal text-destructive">Rate not set</span>
                          )}
                        </TableCell>
                        <TableCell className="text-center font-medium text-muted-foreground">
                          {activeRate ? activeRate.effective_from : "—"}
                        </TableCell>
                        <TableCell className="text-muted-foreground text-sm font-medium">
                          Victor Alvarez, Admin
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="link"
                            className="text-primary hover:text-primary/80 font-semibold text-sm p-0 flex items-center gap-1.5 ml-auto"
                            onClick={() => handleEditRateClick(lvl.id, lvl.level_name)}
                          >
                            <Edit className="w-3.5 h-3.5" /> Edit Rate
                          </Button>
                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
              </TableBody>
            </Table>
          </div>
        </TabsContent>

        <TabsContent value="settings" className="space-y-6">
          <div className="bg-card rounded-2xl border border-border shadow-sm p-6 space-y-4">
            <div>
              <h2 className="text-lg font-semibold text-foreground">Care Level System List</h2>
              <p className="text-sm text-muted-foreground mt-1">
                Enable or disable care levels based on operational requirements (Database lookup).
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
              {careLevels.map((lvl) => (
                <div 
                  key={lvl.id} 
                  className={`flex items-center justify-between p-4 border rounded-xl transition-all duration-200 ${
                    lvl.is_deleted 
                      ? "border-border bg-muted/20 opacity-70" 
                      : "border-primary/20 bg-primary/5"
                  }`}
                >
                  <div className="space-y-1">
                    <div className="font-semibold text-foreground">
                      {getLevelTierName(lvl.level_code, lvl.level_name)}
                    </div>
                    <div className="text-xs text-muted-foreground font-medium">
                      Code: {lvl.level_code}
                    </div>
                    <div className="text-[11px] text-muted-foreground font-normal">
                      Database Name: {lvl.level_name}
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
                      lvl.is_deleted 
                        ? "bg-destructive/10 text-destructive" 
                        : "bg-green-100 text-green-800"
                    }`}>
                      {lvl.is_deleted ? "Inactive" : "Active"}
                    </span>

                    <button
                      onClick={() => handleToggleCareLevelStatus(lvl)}
                      className={`relative inline-flex h-6 w-11 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                        !lvl.is_deleted ? "bg-primary" : "bg-muted"
                      }`}
                      aria-label="Toggle Care Level Status"
                    >
                      <span
                        className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow-sm ring-0 transition duration-200 ease-in-out ${
                          !lvl.is_deleted ? "translate-x-5" : "translate-x-0"
                        }`}
                      />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </TabsContent>
      </Tabs>

      {/* Edit Rate Dialog */}
      <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle className="text-xl font-semibold flex items-center gap-2">
              Edit Care Rate
            </DialogTitle>
          </DialogHeader>
          
          {editingRate && (
            <div className="grid gap-5 py-4">
              <div className="space-y-1 bg-muted/40 p-3.5 rounded-xl border border-border">
                <Label className="text-[11px] font-bold text-muted-foreground uppercase tracking-wider">Care Level</Label>
                <div className="font-semibold text-foreground text-[15px]">{editingRate.careLevelName}</div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="dailyRate" className="font-semibold text-sm flex items-center gap-1.5">
                  <DollarSign className="w-4 h-4 text-muted-foreground" /> Daily Rate ($)
                </Label>
                <Input
                  id="dailyRate"
                  type="number"
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                  value={editingRate.dailyRate || ""}
                  onChange={(e) =>
                    setEditingRate({
                      ...editingRate,
                      dailyRate: Number(e.target.value),
                    })
                  }
                  className="bg-card border-input focus:ring-primary"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="effectiveFrom" className="font-semibold text-sm flex items-center gap-1.5">
                  <Calendar className="w-4 h-4 text-muted-foreground" /> Effective Start Date
                </Label>
                <Input
                  id="effectiveFrom"
                  type="date"
                  value={editingRate.effectiveFrom}
                  onChange={(e) =>
                    setEditingRate({
                      ...editingRate,
                      effectiveFrom: e.target.value,
                    })
                  }
                  className="bg-card border-input focus:ring-primary"
                />
              </div>

              {editingRate.rateId && (
                <div className="flex items-center space-x-2.5 pt-2 border-t border-border mt-1">
                  <input
                    type="checkbox"
                    id="newPeriod"
                    checked={createNewRatePeriod}
                    onChange={(e) => setCreateNewRatePeriod(e.target.checked)}
                    className="w-4 h-4 text-primary border-input rounded focus:ring-primary"
                  />
                  <Label htmlFor="newPeriod" className="font-medium text-xs text-muted-foreground cursor-pointer select-none">
                    Create a new rate period instead of directly updating the existing record
                  </Label>
                </div>
              )}
            </div>
          )}

          <DialogFooter className="gap-2 sm:gap-0">
            <Button
              type="button"
              variant="outline"
              onClick={() => setIsEditModalOpen(false)}
            >
              Cancel
            </Button>
            <Button 
              type="button" 
              onClick={handleSaveRate}
              disabled={createRateMutation.isPending || updateRateMutation.isPending}
            >
              {createRateMutation.isPending || updateRateMutation.isPending ? "Saving..." : "Save Changes"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};
export default LOCRatesPage;
