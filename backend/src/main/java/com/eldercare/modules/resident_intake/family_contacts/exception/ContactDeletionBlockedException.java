package com.eldercare.modules.resident_intake.family_contacts.exception;

public class ContactDeletionBlockedException extends RuntimeException {

    public ContactDeletionBlockedException(Long contactId) {
        super(
                "Contact cannot be deleted because it is currently the guarantor "
                        + "of an ACTIVE resident. Contact id: "
                        + contactId);
    }

    public ContactDeletionBlockedException(String message) {
        super(message);
    }
}