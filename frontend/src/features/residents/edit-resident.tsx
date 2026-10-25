import { useState, useEffect } from 'react'
import { residentService } from '../../services/resident/residentService'
import {
  LayoutDashboard,
  Users,
  ClipboardList,
  Pill,
  ShieldAlert,
  BarChart3,
  LogOut,
  Menu,
  Bell,
  HelpCircle,
  ChevronDown,
  ChevronRight,
  Sun,
  Moon,
  X,
  AlertTriangle,
  ArrowLeft
} from 'lucide-react'

interface EditResidentProps {
  residentId: string
  onBack: () => void
}

export default function EditResident({ residentId, onBack }: EditResidentProps) {
  // Theme and sidebar states removed for integration with global layout

  // Personal Information fields state
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [status, setStatus] = useState('Pending')
  const [dob, setDob] = useState('')
  const [gender, setGender] = useState('Female')
  const [referralSource, setReferralSource] = useState('Private')
  const [ssn, setSsn] = useState('')
  const [maritalStatus, setMaritalStatus] = useState('Single')
  const [referringFacility, setReferringFacility] = useState('101-A')

  // Contact & Address fields state
  const [phone, setPhone] = useState('')
  const [address, setAddress] = useState('')
  const [emergencyContact, setEmergencyContact] = useState('')
  const [emergencyPhone, setEmergencyPhone] = useState('')

  // POA state
  const [poaOnFile, setPoaOnFile] = useState(false)
  const [poaName, setPoaName] = useState('')
  const [poaRelationship, setPoaRelationship] = useState('')

  // Insurance state
  const [payerSource, setPayerSource] = useState('Medicare')
  const [payerType, setPayerType] = useState('Government')
  const [medicareNum, setMedicareNum] = useState('')
  const [insuranceProvider, setInsuranceProvider] = useState('Medicare')
  const [authStartDate, setAuthStartDate] = useState('')
  const [authEndDate, setAuthEndDate] = useState('')

  // DNR state
  const [dnrActive, setDnrActive] = useState(false)

  // Validation checkbox states
  const [validationReqComplete, setValidationReqComplete] = useState(true)
  const [validationSsnValid, setValidationSsnValid] = useState(true)
  const [validationContactReq, setValidationContactReq] = useState(true)

  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        setLoading(true)
        const data = await residentService.getResidentDetail(residentId)
        const names = data.name.trim().split(' ')
        setFirstName(names[0] || '')
        setLastName(names.length > 1 ? names[names.length - 1] : '')
        setStatus(data.status)
        setDob(data.dob || '')
        setGender(data.demographics.gender || 'Female')
        setReferralSource(data.demographics.referralSource || 'Private')
        setSsn(data.demographics.ssn || '')
        setMaritalStatus(data.demographics.maritalStatus || 'Single')
        setReferringFacility(data.room || '101-A')
        setPhone(data.demographics.phone || '')
        setAddress(data.demographics.address || '')

        // Emergency contact name + relationship parsing
        if (data.demographics.emergencyContact && data.demographics.emergencyContact !== '—') {
          const rawContact = data.demographics.emergencyContact
          const idx = rawContact.indexOf(' (')
          if (idx !== -1) {
            setEmergencyContact(rawContact.substring(0, idx))
          } else {
            setEmergencyContact(rawContact)
          }
        } else {
          setEmergencyContact('')
        }
        setEmergencyPhone(data.demographics.phone || '')

        setPoaOnFile(data.poa && data.poa.name !== '—')
        setPoaName(data.poa && data.poa.name !== '—' ? data.poa.name : '')
        setPoaRelationship(data.poa && data.poa.relationship !== '—' ? data.poa.relationship : '')

        setPayerSource(data.insurance.provider || 'Medicare')
        setMedicareNum(data.insurance.medicareNum || '')
        setInsuranceProvider(data.insurance.provider || 'Medicare')
        setDnrActive(data.poa && data.poa.dnrFlag === 'Yes')
      } catch (err) {
        console.error('Failed to load resident details for editing:', err)
      } finally {
        setLoading(false)
      }
    }
    if (residentId) {
      fetchDetail()
    }
  }, [residentId])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const payload = {
      firstName,
      lastName,
      status,
      dob,
      gender,
      referralSource,
      ssn,
      maritalStatus,
      referringFacility,
      phone,
      address,
      emergencyContact,
      emergencyPhone,
      poaOnFile,
      poaName,
      poaRelationship,
      payerSource,
      payerType,
      medicareNum,
      insuranceProvider,
      authStartDate,
      authEndDate,
      dnrActive
    }

    try {
      if (residentId) {
        await residentService.updateResident(residentId, payload)
      } else {
        await residentService.createResident(payload)
      }
      alert('Resident saved successfully!')
      onBack()
    } catch (err) {
      console.error('Failed to save resident:', err)
      alert('Failed to save resident details')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center p-12 text-slate-800 dark:text-slate-100">
        <span className="font-semibold text-lg text-slate-400">Loading form details...</span>
      </div>
    )
  }

  return (
    <div className="flex-1 flex flex-col justify-between overflow-hidden">
      <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6">
        {/* Breadcrumbs */}
        <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-medium">
          <button type="button" onClick={onBack} className="hover:text-blue-600 dark:hover:text-blue-400 transition-all flex items-center gap-1">
            <ArrowLeft className="size-3" />
            <span>Residents</span>
          </button>
          <ChevronRight className="size-3 text-slate-300 dark:text-slate-700" />
          <span className="text-slate-600 dark:text-slate-300">Edit Resident</span>
        </div>

        {/* Header Block */}
        <div className="flex items-center gap-3">
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Edit Resident</h1>
          <span className="inline-flex px-2.5 py-0.5 bg-amber-50 dark:bg-amber-950/20 text-amber-700 dark:text-amber-400 border border-amber-100 dark:border-amber-900/30 text-xs font-semibold rounded-full">
            Pending
          </span>
        </div>
        <p className="text-xs sm:text-sm text-slate-400 dark:text-slate-500 font-semibold -mt-4">
          Elena Ramos &middot; Room 106-A &middot; ID: {residentId || 'RES-00089'}
        </p>

        {/* TWO-COLUMN FORM LAYOUT */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start pb-10">

          {/* LEFT COLUMN: Input Fields (2/3 width) */}
          <div className="lg:col-span-2 space-y-6">

            {/* Personal Information */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs relative">
              {/* Initials Badge Avatar in top right */}
              <div className="absolute top-6 right-6 size-12 bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400 border border-slate-250 dark:border-slate-700 rounded-xl flex items-center justify-center font-bold text-sm shadow-xs">
                ER
              </div>

              <h2 className="text-base font-bold text-slate-950 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 mb-4">Personal Information</h2>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {/* First Name */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">First Name *</label>
                  <input
                    required
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Last Name */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Last Name *</label>
                  <input
                    required
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Date of Birth */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Date of Birth *</label>
                  <input
                    required
                    type="date"
                    value={dob}
                    onChange={(e) => setDob(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Gender */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Gender</label>
                  <input
                    type="text"
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Status */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Status</label>
                  <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden"
                  >
                    <option value="Pending">Pending</option>
                    <option value="Active">Active</option>
                    <option value="Discharged">Discharged</option>
                  </select>
                </div>
                {/* Referral Source */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Referral Source *</label>
                  <input
                    required
                    type="text"
                    value={referralSource}
                    onChange={(e) => setReferralSource(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* SSN */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">SSN *</label>
                  <input
                    required
                    type="text"
                    value={ssn}
                    onChange={(e) => setSsn(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Marital Status */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Marital Status</label>
                  <input
                    type="text"
                    value={maritalStatus}
                    onChange={(e) => setMaritalStatus(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Referring Facility */}
                <div className="sm:col-span-2">
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Referring Facility</label>
                  <input
                    type="text"
                    value={referringFacility}
                    onChange={(e) => setReferringFacility(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
              </div>
            </div>

            {/* Contact & Address */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs">
              <h2 className="text-base font-bold text-slate-955 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 mb-4">Contact & Address</h2>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {/* Phone */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Phone *</label>
                  <input
                    required
                    type="text"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Address */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Address</label>
                  <input
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Emergency Contact */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Emergency Contact *</label>
                  <input
                    required
                    type="text"
                    value={emergencyContact}
                    onChange={(e) => setEmergencyContact(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Emergency Phone */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Emergency Phone *</label>
                  <input
                    required
                    type="text"
                    value={emergencyPhone}
                    onChange={(e) => setEmergencyPhone(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
              </div>
            </div>

            {/* Authorized Representative / POA */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs space-y-4">
              <div className="flex items-center justify-between border-b border-slate-100 dark:border-slate-800 pb-2">
                <h2 className="text-base font-bold text-slate-950 dark:text-white">Authorized Representative / POA</h2>
                {/* POA Switch */}
                <div className="flex items-center gap-2">
                  <span className="text-xs font-semibold text-slate-400">POA on file</span>
                  <button
                    type="button"
                    onClick={() => setPoaOnFile(!poaOnFile)}
                    className={`relative inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-hidden ${poaOnFile ? 'bg-blue-600' : 'bg-slate-200 dark:bg-slate-800'
                      }`}
                  >
                    <span
                      className={`pointer-events-none inline-block size-4 transform rounded-full bg-white shadow-sm ring-0 transition duration-200 ease-in-out ${poaOnFile ? 'translate-x-4' : 'translate-x-0'
                        }`}
                    />
                  </button>
                </div>
              </div>

              {poaOnFile && (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 animate-fade-in">
                  {/* POA Name */}
                  <div>
                    <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">POA Name</label>
                    <input
                      type="text"
                      value={poaName}
                      onChange={(e) => setPoaName(e.target.value)}
                      className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                    />
                  </div>
                  {/* Relationship */}
                  <div>
                    <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Relationship</label>
                    <input
                      type="text"
                      value={poaRelationship}
                      onChange={(e) => setPoaRelationship(e.target.value)}
                      className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                    />
                  </div>
                </div>
              )}
            </div>

            {/* Insurance / Payer */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs">
              <h2 className="text-base font-bold text-slate-955 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 mb-4">Insurance / Payer</h2>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {/* Payer Source */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Payer Source *</label>
                  <input
                    required
                    type="text"
                    value={payerSource}
                    onChange={(e) => setPayerSource(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Payer Type */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Payer Type</label>
                  <input
                    type="text"
                    value={payerType}
                    onChange={(e) => setPayerType(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Medicare Number */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Medicare Number</label>
                  <input
                    type="text"
                    value={medicareNum}
                    onChange={(e) => setMedicareNum(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Insurance Provider */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Insurance Provider</label>
                  <input
                    type="text"
                    value={insuranceProvider}
                    onChange={(e) => setInsuranceProvider(e.target.value)}
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Auth Start Date */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Auth Start Date</label>
                  <input
                    type="text"
                    value={authStartDate}
                    onChange={(e) => setAuthStartDate(e.target.value)}
                    placeholder="—"
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
                {/* Auth End Date */}
                <div>
                  <label className="block text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider mb-1">Auth End Date</label>
                  <input
                    type="text"
                    value={authEndDate}
                    onChange={(e) => setAuthEndDate(e.target.value)}
                    placeholder="—"
                    className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-800 dark:text-slate-100 focus:outline-hidden focus:border-blue-500 focus:bg-white dark:focus:bg-slate-900"
                  />
                </div>
              </div>
            </div>

            {/* Do Not Resuscitate (DNR) */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs">
              <h2 className="text-base font-bold text-slate-955 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 mb-4">Do Not Resuscitate (DNR)</h2>
              <div className="flex items-center gap-3">
                <button
                  type="button"
                  onClick={() => setDnrActive(!dnrActive)}
                  className={`relative inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-hidden ${dnrActive ? 'bg-blue-600' : 'bg-slate-200 dark:bg-slate-800'
                    }`}
                >
                  <span
                    className={`pointer-events-none inline-block size-4 transform rounded-full bg-white shadow-sm ring-0 transition duration-200 ease-in-out ${dnrActive ? 'translate-x-4' : 'translate-x-0'
                      }`}
                  />
                </button>
                <span className="text-sm font-semibold text-slate-600 dark:text-slate-350">
                  {dnrActive ? 'Yes — DNR is active' : 'No — DNR not active'}
                </span>
              </div>
            </div>

          </div>

          {/* RIGHT COLUMN: Validations & Role Info (1/3 width) */}
          <div className="space-y-6">

            {/* Warning Alert Banner */}
            <div className="bg-amber-50 dark:bg-amber-950/20 border border-amber-100 dark:border-amber-900/30 rounded-xl p-4 flex gap-3 shadow-xs">
              <AlertTriangle className="size-5 text-amber-650 dark:text-amber-400 shrink-0" />
              <div className="space-y-0.5">
                <h4 className="text-xs font-bold text-amber-900 dark:text-amber-300">Similar resident name exists.</h4>
                <p className="text-[11px] font-semibold text-amber-700/90 dark:text-amber-400/80">Please verify before saving.</p>
              </div>
            </div>

            {/* Validation Checklist */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs space-y-4">
              <h2 className="text-base font-bold text-slate-950 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2">Validation</h2>

              <div className="space-y-3">
                <label className="flex items-center gap-3 cursor-pointer select-none">
                  <input
                    type="checkbox"
                    checked={validationReqComplete}
                    onChange={(e) => setValidationReqComplete(e.target.checked)}
                    className="rounded border-slate-300 text-blue-600 focus:ring-blue-500 size-4 dark:border-slate-850 dark:bg-slate-950"
                  />
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-300">Required fields complete</span>
                </label>

                <label className="flex items-center gap-3 cursor-pointer select-none">
                  <input
                    type="checkbox"
                    checked={validationSsnValid}
                    onChange={(e) => setValidationSsnValid(e.target.checked)}
                    className="rounded border-slate-300 text-blue-600 focus:ring-blue-500 size-4 dark:border-slate-850 dark:bg-slate-950"
                  />
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-300">SSN format valid</span>
                </label>

                <label className="flex items-center gap-3 cursor-pointer select-none">
                  <input
                    type="checkbox"
                    checked={validationContactReq}
                    onChange={(e) => setValidationContactReq(e.target.checked)}
                    className="rounded border-slate-300 text-blue-600 focus:ring-blue-500 size-4 dark:border-slate-850 dark:bg-slate-950"
                  />
                  <span className="text-xs font-semibold text-slate-700 dark:text-slate-300">Emergency contact required</span>
                </label>
              </div>
            </div>

            {/* Role Card */}
            <div className="bg-white dark:bg-slate-900 rounded-xl border border-slate-200/80 dark:border-slate-800 p-6 shadow-xs space-y-2">
              <h2 className="text-base font-bold text-slate-950 dark:text-white border-b border-slate-100 dark:border-slate-800 pb-2 mb-3">Role</h2>
              <h3 className="text-sm font-bold text-slate-800 dark:text-slate-200">Admission Staff</h3>
              <p className="text-xs font-medium text-slate-400 dark:text-slate-550 leading-relaxed">
                Can create/edit intake fields. Clinical fields are read-only here.
              </p>
            </div>

          </div>

        </div>
      </form>

      {/* BOTTOM ACTIONS BAR - Sticky style footer */}
      <footer className="bg-white dark:bg-slate-900 border-t border-slate-200 dark:border-slate-800 px-6 py-4 flex flex-col sm:flex-row gap-4 items-stretch sm:items-center justify-between shrink-0">
        {/* Left Actions */}
        <div className="flex items-center gap-2">
          <button
            type="button"
            className="flex-1 sm:flex-none px-4 py-2 border border-slate-200 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-400 rounded-lg text-sm font-semibold transition-all cursor-pointer"
          >
            Discharge
          </button>
          <button
            type="button"
            className="flex-1 sm:flex-none px-4 py-2 border border-slate-200 dark:border-slate-800 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-400 rounded-lg text-sm font-semibold transition-all cursor-pointer"
          >
            Change Status
          </button>
        </div>

        {/* Right Actions */}
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={onBack}
            className="flex-1 sm:flex-none px-5 py-2 border border-slate-250 dark:border-slate-850 hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300 rounded-lg text-sm font-semibold transition-all cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            className="flex-1 sm:flex-none px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-semibold transition-all shadow-xs cursor-pointer active:scale-98"
          >
            Save Resident
          </button>
        </div>
      </footer>
    </div>
  )
}
