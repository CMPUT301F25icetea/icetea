package com.example.icetea;

import com.example.icetea.models.User;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Combined test cases for:
 *
 * US 01.02.01 – As an entrant, I want to provide my personal information
 *               such as name, email, and optional phone number in the app.
 *
 * US 01.02.02 – As an entrant, I want to update information such as
 *               name, email, and contact information on my profile.
 * US 01.02.04 As an entrant, I want to delete my profile if I no longer wish to use the app.
 *
 * US 03.03.01 As an administrator, I want to be able to remove images.
 *
 * US 03.05.01 As an administrator, I want to be able to browse profiles.
 *
 * US 03.02.01 As an administrator, I want to be able to remove profiles.
 *
 * US 03.07.01 As an administrator I want to remove organizers that violate app policy.
 *
 */
public class UserProfileTests {

    // --------------------------------------------------------------
    // US 01.02.01 — PROVIDE PERSONAL INFORMATION
    // --------------------------------------------------------------

    /**
     * US 01.02.01:
     * Ensures that a User object can be created with name, email,
     * and optional phone number.
     */
    @Test
    public void testUserCreationWithPersonalInformation() {
        User user = new User();

        user.setId("user123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhone("555-1234"); // optional field

        // Assertions
        assertEquals("user123", user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("555-1234", user.getPhone());

        // Optional phone number can be null
        user.setPhone(null);
        assertNull(user.getPhone());
    }

    // --------------------------------------------------------------
    // US 01.02.02 — UPDATE PERSONAL INFORMATION
    // --------------------------------------------------------------

    /**
     * US 01.02.02:
     * Ensures that existing user profile information can be updated properly.
     */
    @Test
    public void testUserProfileUpdate() {
        User user = new User();

        // Initial profile info
        user.setId("user123");
        user.setName("Old Name");
        user.setEmail("old@example.com");
        user.setPhone("111-1111");

        // Update profile info
        user.setName("New Name");
        user.setEmail("new@example.com");
        user.setPhone("222-2222");

        // Assertions — verify updates took effect
        assertEquals("New Name", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("222-2222", user.getPhone());

        // Ensure ID remains unchanged
        assertEquals("user123", user.getId());
    }
    // --------------------------------------------------------------
    // US 01.02.04 — DELETE USER PROFILE
    // --------------------------------------------------------------

    /**
     * US 01.02.04:
     * As an entrant, I want to delete my profile if I no longer wish to use the app.
     *
     * For model-level testing, deleting a profile is simulated by "clearing"
     * all personally identifiable fields.
     */
    @Test
    public void testUserProfileDeletion() {
        User user = new User();

        // Initial profile info
        user.setId("user123");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhone("555-1234");

        // Simulate delete: clear personal info
        user.setName(null);
        user.setEmail(null);
        user.setPhone(null);
        user.setAvatar(null);

        // userId often remains for logging or auth, so we don't erase it here

        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPhone());
        assertNull(user.getAvatar());
    }
}
