package com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO;

public class MarkSignificantChangeResponseDTO {
    public int id;
    public Boolean significantChangeFlag;
    public String updatedAt;
    public MarkSignificantChangeResponseDTO(int id, Boolean significantChangeFlag, String updatedAt) {
        this.id = id;
        this.significantChangeFlag = significantChangeFlag;
        this.updatedAt = updatedAt;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Boolean getSignificantChangeFlag() {
        return significantChangeFlag;
    }
    public void setSignificantChangeFlag(Boolean significantChangeFlag) {
        this.significantChangeFlag = significantChangeFlag;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    

}
