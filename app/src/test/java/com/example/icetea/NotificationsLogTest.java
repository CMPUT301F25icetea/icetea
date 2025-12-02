package com.example.icetea;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.Arrays;

public class NotificationsLogTest {

    static class FakeNotificationsLogDB {
        List<String> getAllNotifications() {
            return Arrays.asList("notif1", "notif2");
        }
    }

    @Test
    public void testGetAllNotifications() {
        FakeNotificationsLogDB db = new FakeNotificationsLogDB();
        List<String> notifications = db.getAllNotifications();

        assertEquals(2, notifications.size());
        assertEquals("notif1", notifications.get(0));
        assertEquals("notif2", notifications.get(1));
    }
}
