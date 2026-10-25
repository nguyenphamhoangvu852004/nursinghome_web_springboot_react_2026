import { useParams, useNavigate } from "react-router";
import EditResident from "@/features/residents/edit-resident";

const ResidentEditPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  return (
    <EditResident
      residentId={id || ""}
      onBack={() => navigate(`/admin/residents/${id}`)}
    />
  );
};

export default ResidentEditPage;
