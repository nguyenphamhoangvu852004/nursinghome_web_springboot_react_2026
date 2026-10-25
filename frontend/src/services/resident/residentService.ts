import { apiClient } from "@/lib/api-client"

export interface ResidentListItem {
  id: number
  firstName: string
  lastName: string
  currentCareLevel: string
}

export interface ResidentListItemFE {
  id: number
  name: string
  room: string
  status: string
  dob: string
  age: number
  payerSource: string
  referralSource: string
}

export interface ResidentDetail {
  id: number
  name: string
  initials: string
  room: string
  status: string
  dob: string
  age: number
  badges: Array<{ text: string; type: string }>
  demographics: {
    legalName: string
    address: string
    dob: string
    admissionDate: string
    ssn: string
    roomBed: string
    gender: string
    referralSource: string
    maritalStatus: string
    emergencyContact: string
    phone: string
    payerSource: string
  }
  poa: {
    name: string
    relationship: string
    contact: string
    dnrFlag: string
  }
  diagnoses: string[]
  allergies: string[]
  insurance: {
    medicareNum: string
    provider: string
    payerName: string
    authNum: string
    authStartEnd: string
  }
  locSummary: {
    level: string
    adlScore: string
  }
}

export interface ResidentSaveRequest {
  firstName: string
  lastName: string
  status: string
  dob: string
  gender: string
  referralSource: string
  ssn: string
  maritalStatus: string
  referringFacility: string
  phone: string
  address: string
  emergencyContact: string
  emergencyPhone: string
  poaOnFile: boolean
  poaName: string
  poaRelationship: string
  payerSource: string
  payerType: string
  medicareNum: string
  insuranceProvider: string
  authStartDate: string
  authEndDate: string
  dnrActive: boolean
}

interface ApiResponse<T> {
  statusCode: number
  message: string
  data: T
}

interface ResidentListResponseContainerDto {
  residents: ResidentListItem[]
  meta: {
    total: number
    page: number
    pageSize: number
  }
}

interface ResidentResponseDto {
  id: number
  firstName: string
  middleName?: string
  lastName: string
  dateOfBirth: string
  gender: string
  status: string
  bedId?: number
  isChartLocked: boolean
  maritalStatus?: string
}

function calculateAge(dobString: string): number {
  if (!dobString) return 75
  try {
    const birthDate = new Date(dobString)
    const difference = Date.now() - birthDate.getTime()
    const ageDate = new Date(difference)
    return Math.abs(ageDate.getUTCFullYear() - 1970)
  } catch {
    return 75
  }
}

export const residentService = {
  getResidents: async (search?: string, status?: string, referral?: string): Promise<ResidentListItemFE[]> => {
    // If status is "All", pass undefined to backend
    const statusParam = status === 'All' ? undefined : status
    const response = await apiClient.get<ApiResponse<ResidentListResponseContainerDto>>('/residents', {
      params: { search, status: statusParam, page_size: 100 },
    })
    
    const container = response.data.data
    if (!container || !container.residents) return []

    // Fetch full detail for each resident in parallel to extract status and bed/room info
    const detailed = await Promise.all(
      container.residents.map(async (r) => {
        try {
          const detailRes = await apiClient.get<ApiResponse<{ resident: ResidentResponseDto }>>(`/residents/${r.id}`)
          const detail = detailRes.data.data.resident
          return {
            id: r.id,
            name: `${r.firstName} ${r.lastName}`,
            room: detail.bedId ? `Room ${detail.bedId}` : '—',
            status: detail.status === 'ACTIVE' ? 'Active' : (detail.status === 'PENDING' ? 'Pending' : 'Discharged'),
            dob: detail.dateOfBirth || '—',
            age: calculateAge(detail.dateOfBirth),
            payerSource: 'Medicare',
            referralSource: 'Private',
          }
        } catch {
          return {
            id: r.id,
            name: `${r.firstName} ${r.lastName}`,
            room: r.currentCareLevel ? `Care Level: ${r.currentCareLevel}` : '—',
            status: 'Active',
            dob: '—',
            age: 75,
            payerSource: 'Medicare',
            referralSource: 'Private',
          }
        }
      })
    )

    return detailed
  },

  getResidentDetail: async (id: number | string): Promise<ResidentDetail> => {
    const response = await apiClient.get<ApiResponse<ResidentDetail>>(`/residents/${id}/detail`)
    return response.data.data
  },

  createResident: async (data: ResidentSaveRequest): Promise<void> => {
    const payload = {
      firstName: data.firstName,
      middleName: '',
      lastName: data.lastName,
      dateOfBirth: data.dob,
      gender: data.gender,
      maritalStatus: data.maritalStatus,
      religionPreference: 'None',
      addressId: null
    }
    await apiClient.post('/residents', payload)
  },

  updateResident: async (id: number | string, data: ResidentSaveRequest): Promise<void> => {
    await apiClient.patch(`/residents/${id}`, data)
  },

  deleteResident: async (id: number | string): Promise<void> => {
    await apiClient.delete(`/residents/${id}`)
  },

  getResidentInfo: async (id: number | string): Promise<ResidentInfo> => {
    const response = await apiClient.get<ApiResponse<ResidentInfo>>(`/residents/${id}/info`)
    return response.data.data
  },

  getResidentContacts: async (id: number | string): Promise<ResidentContact[]> => {
    const response = await apiClient.get<ApiResponse<ResidentContact[]>>(`/residents/${id}/contacts`)
    return response.data.data
  },

  getResidentCareLevelHistory: async (id: number | string): Promise<ResidentCareLevelHistory[]> => {
    const response = await apiClient.get<ApiResponse<ResidentCareLevelHistory[]>>(`/residents/${id}/care-level-history`)
    return response.data.data
  },

  getResidentSensitiveInfo: async (id: number | string): Promise<ResidentSensitiveInfo> => {
    const response = await apiClient.get<ApiResponse<ResidentSensitiveInfo>>(`/residents/${id}/sensitive-info`)
    return response.data.data
  },
}

export interface ResidentInfo {
  id: number
  firstName: string
  middleName?: string
  lastName: string
  dateOfBirth: string
  gender: string
  maritalStatus?: string
  religionPreference?: string
  status: string
  isChartLocked: boolean
  createdAt: string
  updatedAt: string
  address?: {
    id: number
    streetLine1: string
    streetLine2?: string
    city: string
    state: string
    zipCode: string
    addressType: string
  }
  bed?: {
    id: number
    bedNumber: string
    status: string
    roomId: number
    roomNumber: string
    roomType: string
    facilityId: number
    facilityName: string
  }
}

export interface ResidentContact {
  id: number
  firstName: string
  middleName?: string
  lastName: string
  relationshipType: string
  isGuarantor: boolean
  isEmergencyContact: boolean
  isPrimary: boolean
  financialResponsibilityPct?: number
  phonePrimary: string
  phoneSecondary?: string
  email?: string
  address?: {
    id: number
    streetLine1: string
    streetLine2?: string
    city: string
    state: string
    zipCode: string
    addressType: string
  }
}

export interface ResidentCareLevelHistory {
  id: number
  startDate: string
  endDate?: string
  careLevelId: number
  careLevelCode: string
  careLevelName: string
}

export interface ResidentSensitiveInfo {
  id: number
  residentId: number
  ssnEncrypted?: string
  medicalRecordNumberEncrypted?: string
  primaryInsuranceIdEncrypted?: string
  bankAccountEncrypted?: string
}
