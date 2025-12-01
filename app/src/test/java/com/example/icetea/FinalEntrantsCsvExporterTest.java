package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Mocked test for CSV export of final entrants
 * US 02.06.05 – As an organizer, I want to export
 * a final list of entrants who enrolled for the event
 * into a CSV file.
 *
 * This test checks that from a mocked set of entrant data,
 * we can correctly build a CSV string that includes:
 *  - Name
 *  - Email
 *  - Registration Date
 */

public class FinalEntrantsCsvExporterTest {
    static class MockUser {
        String name;
        String email;

        MockUser(String name, String email) {
            this.name = name;
            this.email = email;
        }

        String getString(String field) {
            if ("name".equals(field)) return name;
            if ("email".equals(field)) return email;
            return null;
        }
    }

    /**
     * Utility method matching FinalEntrantsCsvExporter.buildCsv()
     */
    private String buildCsv(List<MockUser> users, List<Date> joinedDates) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name,Email,Registration Date\n");

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        for (int i = 0; i < users.size(); i++) {
            MockUser user = users.get(i);
            Date date = joinedDates.get(i);

            String name = (user != null && user.name != null) ? user.name : "N/A";
            String email = (user != null && user.email != null) ? user.email : "N/A";
            String dateStr = (date != null) ? dateFormat.format(date) : "N/A";

            sb.append(name).append(",")
                    .append(email).append(",")
                    .append(dateStr).append("\n");
        }

        return sb.toString().trim();
    }

    @Test
    public void testCsvBuildsCorrectlyForMultipleEntrants() {
        List<MockUser> users = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        users.add(new MockUser("John Doe", "john@example.com"));
        users.add(new MockUser("Sarah Lee", "sarah@example.com"));

        // Fixed timestamps so the test is stable
        dates.add(new Date(1700000000000L)); // Example date
        dates.add(new Date(1700100000000L)); // Example date

        String csv = buildCsv(users, dates);
        String[] lines = csv.split("\n");

        // Header + 2 rows
        assertEquals(3, lines.length);

        // Header
        assertEquals("Name,Email,Registration Date", lines[0]);

        // First row
        assertTrue(lines[1].startsWith("John Doe,john@example.com,"));
        // Second row
        assertTrue(lines[2].startsWith("Sarah Lee,sarah@example.com,"));
    }

    @Test
    public void testCsvBuildsCorrectlyForEmptyList() {
        List<MockUser> users = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        String csv = buildCsv(users, dates);

        // Only header should appear
        assertEquals("Name,Email,Registration Date", csv);
    }
}
