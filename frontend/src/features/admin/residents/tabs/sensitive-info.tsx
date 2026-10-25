import { useState, useEffect } from "react";
import { residentService, type ResidentSensitiveInfo } from "@/services/resident/residentService";
import { Eye, EyeOff, ShieldCheck, CreditCard, Landmark, FileText, Lock } from "lucide-react";

interface SensitiveInfoTabProps {
  residentId: string;
}

export const SensitiveInfoTab = ({ residentId }: SensitiveInfoTabProps) => {
  const [sensitive, setSensitive] = useState<ResidentSensitiveInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [showSSN, setShowSSN] = useState(false);
  const [showBankAccount, setShowBankAccount] = useState(false);

  useEffect(() => {
    const fetchSensitive = async () => {
      if (!residentId) return;
      try {
        setLoading(true);
        const data = await residentService.getResidentSensitiveInfo(residentId);
        setSensitive(data);
      } catch (err) {
        console.error("Failed to load resident sensitive info:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchSensitive();
  }, [residentId]);

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="text-slate-400 dark:text-slate-500 animate-pulse text-sm font-medium">
          Loading sensitive info...
        </div>
      </div>
    );
  }

  if (!sensitive) {
    return (
      <div className="text-slate-400 dark:text-slate-500 p-8 text-center text-sm">
        No sensitive information registered for this resident.
      </div>
    );
  }

  const formatMasked = (value: string | undefined, show: boolean, type: "ssn" | "bank" | "other") => {
    if (!value) return "—";
    if (show) return value;
    
    if (type === "ssn") {
      // SSN is typically 9 digits, e.g. 123-456-7890 or 999999999
      const clean = value.replace(/[^0-9]/g, "");
      if (clean.length >= 4) {
        return `XXX-XX-${clean.slice(-4)}`;
      }
      return "XXX-XX-XXXX";
    }
    
    if (type === "bank") {
      const clean = value.trim();
      if (clean.length >= 4) {
        return `•••• •••• •••• ${clean.slice(-4)}`;
      }
      return "•••• •••• •••• ••••";
    }

    return "••••••••";
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-xl p-5 shadow-xs space-y-6">
      <div className="flex items-center gap-2 border-b border-slate-100 dark:border-slate-800 pb-3">
        <Lock className="w-5 h-5 text-rose-500" />
        <div>
          <h3 className="font-bold text-base text-slate-850 dark:text-white">Protected Health Information (PHI)</h3>
          <p className="text-xs text-slate-400 dark:text-slate-500 font-medium">Access to this information is logged for compliance auditing.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* SSN */}
        <div className="bg-slate-50 dark:bg-slate-950/40 rounded-xl p-4 border border-slate-200/40 dark:border-slate-850 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-rose-50 dark:bg-rose-950/20 text-rose-600 dark:text-rose-400 rounded-lg flex items-center justify-center">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Social Security Number (SSN)</span>
              <span className="font-mono font-semibold text-sm text-slate-800 dark:text-slate-200">
                {formatMasked(sensitive.ssnEncrypted, showSSN, "ssn")}
              </span>
            </div>
          </div>
          {sensitive.ssnEncrypted && (
            <button 
              onClick={() => setShowSSN(!showSSN)} 
              className="text-slate-400 dark:text-slate-500 hover:text-slate-600 dark:hover:text-slate-300 transition-colors p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg"
              title={showSSN ? "Hide SSN" : "Show SSN"}
            >
              {showSSN ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          )}
        </div>

        {/* MRN */}
        <div className="bg-slate-50 dark:bg-slate-950/40 rounded-xl p-4 border border-slate-200/40 dark:border-slate-850 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-50 dark:bg-blue-950/20 text-blue-600 dark:text-blue-400 rounded-lg flex items-center justify-center">
              <FileText className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Medical Record Number (MRN)</span>
              <span className="font-mono font-semibold text-sm text-slate-800 dark:text-slate-200">
                {sensitive.medicalRecordNumberEncrypted || "—"}
              </span>
            </div>
          </div>
        </div>

        {/* Primary Insurance ID */}
        <div className="bg-slate-50 dark:bg-slate-950/40 rounded-xl p-4 border border-slate-200/40 dark:border-slate-850 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-emerald-50 dark:bg-emerald-950/20 text-emerald-600 dark:text-emerald-400 rounded-lg flex items-center justify-center">
              <CreditCard className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Primary Insurance ID</span>
              <span className="font-mono font-semibold text-sm text-slate-800 dark:text-slate-200">
                {sensitive.primaryInsuranceIdEncrypted || "—"}
              </span>
            </div>
          </div>
        </div>

        {/* Bank Account */}
        <div className="bg-slate-50 dark:bg-slate-950/40 rounded-xl p-4 border border-slate-200/40 dark:border-slate-850 flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-amber-50 dark:bg-amber-950/20 text-amber-600 dark:text-amber-400 rounded-lg flex items-center justify-center">
              <Landmark className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs text-slate-400 dark:text-slate-500 font-bold uppercase tracking-wider block">Bank Account Number</span>
              <span className="font-mono font-semibold text-sm text-slate-800 dark:text-slate-200">
                {formatMasked(sensitive.bankAccountEncrypted, showBankAccount, "bank")}
              </span>
            </div>
          </div>
          {sensitive.bankAccountEncrypted && (
            <button 
              onClick={() => setShowBankAccount(!showBankAccount)} 
              className="text-slate-400 dark:text-slate-500 hover:text-slate-600 dark:hover:text-slate-300 transition-colors p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg"
              title={showBankAccount ? "Hide Bank Account" : "Show Bank Account"}
            >
              {showBankAccount ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
