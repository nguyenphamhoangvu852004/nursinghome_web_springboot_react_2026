package com.eldercare.modules.resident_intake.family_contacts.exception;

public class ContactEmailAlreadyExistsException extends RuntimeException {

    public ContactEmailAlreadyExistsException(String email) {
        super("Contact email already exists: " + email);
    }

    public ContactEmailAlreadyExistsException() {
        super("Contact email already exists.");
    }
}