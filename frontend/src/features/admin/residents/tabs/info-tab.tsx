import { useState, useEffect } from "react";
import { residentService, type ResidentInfo } from "@/services/resident/residentService";
import { User, MapPin, Calendar, Heart, ShieldAlert, Home, Activity } from "lucide-react";

interface InfoTabProps {
  residentId: string;
}

export const InfoTab = ({ residentId }: InfoTabProps) => {
  const [info, setInfo] = useState<ResidentInfo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInfo = async () => {
      if (!residentId) return;
      try {
        setLoading(true);
        const data = await residentService.getResidentInfo(residentId);
        setInfo(data);
      } catch (err) {
        console.error("Failed to load resident info:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchInfo();
  }, [residentId]);

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="text-slate-400 dark:text-slate-500 animate-pulse text-sm font-medium">
          Loading info...
        </div>
      </div>
    );
  }

  if (!info) {
    return (
      <div className="text-slate-400 dark:text-slate-500 p-8 text-center text-sm">
        No information available for this resident.
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* Demographics Card */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-xl p-5 shadow-xs space-y-4">
        <div className="flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
          <User className="w-5 h-5 text-primary" />
          <h3 className="font-bold text-base text-slate-850 dark:text-white">Demographics</h3>
        </div>
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">First Name</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{info.firstName}</span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Middle Name</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{info.middleName || "—"}</span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Last Name</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{info.lastName}</span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Date of Birth</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5 text-slate-400" />
              {info.dateOfBirth}
            </span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Gender</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{info.gender}</span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Marital Status</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{info.maritalStatus || "—"}</span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Religion Preference</span>
            <span className="font-semibold text-slate-800 dark:text-slate-200">{info.religionPreference || "—"}</span>
          </div>
          <div>
            <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Chart Status</span>
            <span className={`inline-flex items-center gap-1 text-xs font-semibold mt-1 px-2.5 py-0.5 rounded-full border ${
              info.isChartLocked 
                ? 'bg-rose-50 dark:bg-rose-950/20 text-rose-700 dark:text-rose-400 border-rose-100 dark:border-rose-900/30' 
                : 'bg-emerald-50 dark:bg-emerald-950/20 text-emerald-700 dark:text-emerald-400 border-emerald-100 dark:border-emerald-900/30'
            }`}>
              <ShieldAlert className="w-3.5 h-3.5" />
              {info.isChartLocked ? "Locked" : "Unlocked"}
            </span>
          </div>
        </div>
      </div>

      {/* Bed & Facility Location Card */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-xl p-5 shadow-xs space-y-4">
        <div className="flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
          <Home className="w-5 h-5 text-primary" />
          <h3 className="font-bold text-base text-slate-850 dark:text-white">Facility & Bed Allocation</h3>
        </div>
        {info.bed ? (
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Facility Name</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.bed.facilityName}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Bed Number</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.bed.bedNumber}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Room Number</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">Room {info.bed.roomNumber}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Room Type</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.bed.roomType}</span>
            </div>
            <div className="col-span-2">
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Bed Status</span>
              <span className={`inline-flex items-center gap-1 text-xs font-semibold mt-1 px-2.5 py-0.5 rounded-full border ${
                info.bed.status === 'OCCUPIED'
                  ? 'bg-amber-50 dark:bg-amber-950/20 text-amber-700 dark:text-amber-400 border-amber-100 dark:border-amber-900/30'
                  : 'bg-emerald-50 dark:bg-emerald-950/20 text-emerald-700 dark:text-emerald-400 border-emerald-100 dark:border-emerald-900/30'
              }`}>
                <Activity className="w-3.5 h-3.5" />
                {info.bed.status}
              </span>
            </div>
          </div>
        ) : (
          <p className="text-sm text-slate-500 dark:text-slate-400 italic">No bed or facility assigned yet.</p>
        )}
      </div>

      {/* Address Card */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-xl p-5 shadow-xs space-y-4 md:col-span-2">
        <div className="flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
          <MapPin className="w-5 h-5 text-primary" />
          <h3 className="font-bold text-base text-slate-850 dark:text-white">Resident Address</h3>
        </div>
        {info.address ? (
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
            <div className="sm:col-span-2">
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Street Line 1</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.address.streetLine1}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Street Line 2</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.address.streetLine2 || "—"}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">City</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.address.city}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">State</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.address.state}</span>
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Zip Code</span>
              <span className="font-semibold text-slate-800 dark:text-slate-200">{info.address.zipCode}</span>
            </div>
          </div>
        ) : (
          <p className="text-sm text-slate-500 dark:text-slate-400 italic">No address registered.</p>
        )}
      </div>
    </div>
  );
};
