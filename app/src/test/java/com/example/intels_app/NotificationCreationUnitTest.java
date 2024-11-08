package com.example.intels_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class NotificationCreationUnitTest {
    @Test
    public void testNotificationNoArgument() {
        Notification notification = new Notification();

        assertNull(notification.getTitle());
        assertNull(notification.getMessage());
    }

    @Test
    public void testNotification() {
        String title = "Aayushi's Event";
        String message = "Excited to join!";

        Notification notification = new Notification(title, message);

        assertEquals(title, notification.getTitle());
        assertEquals(message, notification.getMessage());
    }
}
