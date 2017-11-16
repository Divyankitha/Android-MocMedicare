package com.manage.hospital.hmapp.model;


import com.manage.hospital.hmapp.utility.ConfigConstant;

public class EmergencyContact {
    private int dependent_id;
    private String firstname;
    private String lastname;
    private String emailId;
    private String contact;
    private String address;
    private String relation;

    public EmergencyContact(int dependent_idIn, String firstnameIn, String lastnameIn, String emailIdIn, String contactIn, String addressIn, String relationIn) {
        dependent_id = dependent_idIn;
        firstname = firstnameIn;
        lastname = lastnameIn;
        emailId = emailIdIn;
        contact = contactIn;
        address = addressIn;
        relation = relationIn;
    }

    public EmergencyContact(String firstnameIn, String lastnameIn, String emailIdIn, String contactIn, String addressIn, String relationIn) {
        dependent_id = ConfigConstant.INVALID_ID;
        firstname = firstnameIn;
        lastname = lastnameIn;
        emailId = emailIdIn;
        contact = contactIn;
        address = addressIn;
        relation = relationIn;
    }

    public EmergencyContact (String firstnameIn, String lastnameIn, String emailIdIn, String contactIn, String addressIn, String relationIn, int patient_IdIn) {
        this(ConfigConstant.INVALID_ID, firstnameIn, lastnameIn, emailIdIn, contactIn, addressIn, relationIn);
    }

    public EmergencyContact clone(EmergencyContact other) {
        EmergencyContact newEmergencyContact = new EmergencyContact(other.dependent_id, other.firstname, other.lastname, other.emailId,
                other.contact, other.address, other.relation);
        return newEmergencyContact;
    }

    public int getId() { return dependent_id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmailId() { return emailId; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }
    public String getRelation() { return relation; }


}
