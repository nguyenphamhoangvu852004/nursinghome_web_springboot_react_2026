package com.eldercare.modules.resident_intake.family_contacts.exception;

public class ContactNotFoundException extends RuntimeException {

    public ContactNotFoundException(Long id) {
        super("Contact not found with id: " + id);
    }

    public ContactNotFoundException(String message) {
        super(message);
    }
}