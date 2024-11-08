package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class EventCreationUnitTest {

    @Test
    public void testEventWithPosterUrl() {
        String eventName = "Test_Event";
        String facilityName = "Testing_Facility1";
        String location = "435 Grove Road";
        String dateTime = "2024-01-01 10:00 AM";
        String description = "Testing event creation description.";
        int maxAttendees = 50;
        boolean geolocationRequirement = true;
        boolean notifPreference = false;
        String posterUrl = "http://example.com/poster.jpg";
        String deviceId = "testing_device123";

        Event event = new Event(eventName, facilityName, location, dateTime, description,
                maxAttendees, geolocationRequirement, notifPreference, posterUrl, deviceId);

        assertEquals(eventName, event.getEventName());
        assertEquals(facilityName, event.getFacilityName());
        assertEquals(location, event.getLocation());
        assertEquals(dateTime, event.getDateTime());
        assertEquals(description, event.getDescription());
        assertEquals(maxAttendees, event.getMaxAttendees());
        assertTrue(event.isGeolocationRequirement());
        assertFalse(event.isNotifPreference());
        assertEquals(posterUrl, event.getPosterUrl());
        assertEquals(deviceId, event.getDeviceId());
    }

    @Test
    public void testEventWithoutPoster() {
        String eventName = "Test_Event";
        String facilityName = "Testing_Facility1";
        String location = "435 Grove Road";
        String dateTime = "2024-01-01 10:00 AM";
        String description = "Testing event creation description.";
        int maxAttendees = 50;
        boolean geolocationRequirement = true;
        boolean notifPreference = false;
        String deviceId = "testing_device123";

        Event event = new Event(eventName, facilityName, location, dateTime, description,
                maxAttendees, geolocationRequirement, notifPreference, deviceId);

        assertEquals(eventName, event.getEventName());
        assertEquals(facilityName, event.getFacilityName());
        assertEquals(location, event.getLocation());
        assertEquals(dateTime, event.getDateTime());
        assertEquals(description, event.getDescription());
        assertEquals(maxAttendees, event.getMaxAttendees());
        assertTrue(event.isGeolocationRequirement());
        assertFalse(event.isNotifPreference());
        assertEquals(deviceId, event.getDeviceId());
    }
}
