import ResidentsList from './residents-list'
import { useNavigate } from 'react-router'

export default function ResidentsFeature() {
  const navigate = useNavigate()

  const handleViewDetail = (id: string) => {
    navigate(`/admin/residents/${id}`)
  }

  return (
    <div className="w-full">
      <ResidentsList 
        onViewDetail={handleViewDetail} 
      />
    </div>
  )
}
