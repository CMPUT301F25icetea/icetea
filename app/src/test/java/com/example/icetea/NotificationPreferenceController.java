package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.models.UserDB;
import com.example.icetea.profile.ProfileController;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;

/**
 * US 01.04.03
 * As an entrant, I want to opt out of receiving notifications.
 *
 * Verifies that opting out sets notifications = false in the users collection.
 */
public class NotificationPreferenceController {

    private ProfileController controller;
    private UserDB mockUserDB;
    private MockedStatic<UserDB> userDBStatic;

    @Before
    public void setUp() {
        controller = new ProfileController();
        mockUserDB = mock(UserDB.class);

        userDBStatic = Mockito.mockStatic(UserDB.class);
        userDBStatic.when(UserDB::getInstance).thenReturn(mockUserDB);
    }

    @After
    public void tearDown() {
        userDBStatic.close();
    }

    @Test
    public void optOut_setsNotificationsFieldToFalse() {

        String userId = "user123";

        // Capture the updates map passed to UserDB
        ArgumentCaptor<HashMap<String, Object>> updateCaptor =
                ArgumentCaptor.forClass(HashMap.class);

        // Mock successful Firestore update
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(2);
            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            listener.onComplete(mockTask);
            return null;
        }).when(mockUserDB).updateUser(eq(userId), updateCaptor.capture(), any());

        // --- Act: user opts out ---
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("notifications", false);

        controller.updateProfile(userId, updates, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {}
            @Override
            public void onFailure(Exception e) {
                fail("Update should succeed");
            }
        });

        // --- Assert ---
        HashMap<String, Object> capturedUpdates = updateCaptor.getValue();

        assertNotNull(capturedUpdates);
        assertTrue(capturedUpdates.containsKey("notifications"));
        assertEquals(false, capturedUpdates.get("notifications"));
    }
}
