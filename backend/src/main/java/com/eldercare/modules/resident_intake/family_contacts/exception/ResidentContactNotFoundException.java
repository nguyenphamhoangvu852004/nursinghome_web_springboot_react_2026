package com.eldercare.modules.resident_intake.family_contacts.exception;

public class ResidentContactNotFoundException extends RuntimeException {

    public ResidentContactNotFoundException() {
        super("Resident contact relationship not found.");
    }

    public ResidentContactNotFoundException(Long residentContactId) {
        super("Resident contact relationship not found with id: " + residentContactId);
    }

    public ResidentContactNotFoundException(String message) {
        super(message);
    }
}