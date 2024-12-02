package com.example.intels_app;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventDataClassUnitTest {

    private Event event;

    @Before
    public void setUp() {
        event = new Event();
    }

    @Test
    public void testGetEventName() {
        event.setEventName("Tech Conference");
        assertEquals("Tech Conference", event.getEventName());
    }

    @Test
    public void testSetEventName() {
        event.setEventName("Tech Conference");
        assertEquals("Tech Conference", event.getEventName());
    }

    @Test
    public void testGetFacilityName() {
        event.setFacilityName("Tech Hub");
        assertEquals("Tech Hub", event.getFacilityName());
    }

    @Test
    public void testSetFacilityName() {
        event.setFacilityName("Tech Hub");
        assertEquals("Tech Hub", event.getFacilityName());
    }

    @Test
    public void testGetLocation() {
        event.setLocation("New York");
        assertEquals("New York", event.getLocation());
    }

    @Test
    public void testSetLocation() {
        event.setLocation("New York");
        assertEquals("New York", event.getLocation());
    }

    @Test
    public void testGetDateTime() {
        event.setDateTime("2024-12-15T10:00:00");
        assertEquals("2024-12-15T10:00:00", event.getDateTime());
    }

    @Test
    public void testSetDateTime() {
        event.setDateTime("2024-12-15T10:00:00");
        assertEquals("2024-12-15T10:00:00", event.getDateTime());
    }

    @Test
    public void testGetDescription() {
        event.setDescription("A conference on technology");
        assertEquals("A conference on technology", event.getDescription());
    }

    @Test
    public void testSetDescription() {
        event.setDescription("A conference on technology");
        assertEquals("A conference on technology", event.getDescription());
    }

    @Test
    public void testGetMaxAttendees() {
        event.setMaxAttendees(500);
        assertEquals(500, event.getMaxAttendees());
    }

    @Test
    public void testSetMaxAttendees() {
        event.setMaxAttendees(500);
        assertEquals(500, event.getMaxAttendees());
    }

    @Test
    public void testIsGeolocationRequirement() {
        event.setGeolocationRequirement(true);
        assertTrue(event.isGeolocationRequirement());
    }

    @Test
    public void testSetGeolocationRequirement() {
        event.setGeolocationRequirement(true);
        assertTrue(event.isGeolocationRequirement());
    }

    @Test
    public void testGetPosterUrl() {
        event.setPosterUrl("https://example.com/poster.jpg");
        assertEquals("https://example.com/poster.jpg", event.getPosterUrl());
    }

    @Test
    public void testSetPosterUrl() {
        event.setPosterUrl("https://example.com/poster.jpg");
        assertEquals("https://example.com/poster.jpg", event.getPosterUrl());
    }

    @Test
    public void testGetQrCodeUrl() {
        event.setQrCodeUrl("https://example.com/qr.png");
        assertEquals("https://example.com/qr.png", event.getQrCodeUrl());
    }

    @Test
    public void testSetQrCodeUrl() {
        event.setQrCodeUrl("https://example.com/qr.png");
        assertEquals("https://example.com/qr.png", event.getQrCodeUrl());
    }

    @Test
    public void testGetDeviceId() {
        event.setDeviceId("device123");
        assertEquals("device123", event.getDeviceId());
    }

    @Test
    public void testSetDeviceId() {
        event.setDeviceId("device123");
        assertEquals("device123", event.getDeviceId());
    }

    @Test
    public void testGetId() {
        event.setId("event123");
        assertEquals("event123", event.getId());
    }

    @Test
    public void testSetId() {
        event.setId("event123");
        assertEquals("event123", event.getId());
    }
}
