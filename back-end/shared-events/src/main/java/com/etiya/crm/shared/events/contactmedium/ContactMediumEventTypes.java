package com.etiya.crm.shared.events.contactmedium;

/** "contact-medium-events" topic'inde tasinan event'lerin "type" degerleri. */
public final class ContactMediumEventTypes {

	public static final String CONTACT_MEDIUM_CREATED = "ContactMediumCreated";
	public static final String CONTACT_MEDIUM_UPDATED = "ContactMediumUpdated";
	public static final String CONTACT_MEDIUM_DEACTIVATED = "ContactMediumDeactivated";

	private ContactMediumEventTypes() {
	}
}
