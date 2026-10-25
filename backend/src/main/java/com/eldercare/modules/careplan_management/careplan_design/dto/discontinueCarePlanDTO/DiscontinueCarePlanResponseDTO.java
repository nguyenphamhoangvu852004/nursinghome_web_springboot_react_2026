package com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO;

public class DiscontinueCarePlanResponseDTO {
    public int id;
    public String status;
    public String updatedAt;

   public DiscontinueCarePlanResponseDTO(int id, String status, String updatedAt) {
    this.id = id;
    this.status = status;
    this.updatedAt = updatedAt;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
