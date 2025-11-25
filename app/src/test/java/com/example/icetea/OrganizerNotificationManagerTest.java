package com.example.icetea;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.example.icetea.organizer.OrganizerNotificationManager;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Map;

/**
 * Mocked test for sending notifications to entrants
 * US 02.07.01 US 02.07.02 US 02.07.03
 * As an organizer I want to send notifications to all entrants on the waiting list.
 * As an organizer I want to send notifications to all selected entrants.
 * As an organizer I want to send a notification to all cancelled entrants.
 * All 3 of these user stories utilize the same notification method, unsure else I'd test them
 */

public class OrganizerNotificationManagerTest {

    private OrganizerNotificationManager manager;
    private CollectionReference mockCollection;
    private Task<DocumentReference> mockTask;

    @Before
    public void setUp() {
        // Mock CollectionReference
        mockCollection = mock(CollectionReference.class);

        // Mock Task<DocumentReference>
        mockTask = mock(Task.class);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            // call the success listener immediately
            ((com.google.android.gms.tasks.OnSuccessListener<DocumentReference>) invocation.getArgument(0))
                    .onSuccess(mock(DocumentReference.class));
            return mockTask; // allow chaining
        });
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask); // allow chaining
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mock(DocumentReference.class));

        // Make add() return the mocked Task
        when(mockCollection.add(any(Map.class))).thenReturn(mockTask);

        // Mock FirebaseFirestore.getInstance().collection("Notification")
        MockedStatic<FirebaseFirestore> firestoreMock = mockStatic(FirebaseFirestore.class);
        FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);
        firestoreMock.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);
        when(mockFirestore.collection("Notification")).thenReturn(mockCollection);

        // Create the manager (it uses mocked Firestore)
        manager = new OrganizerNotificationManager();
    }

    @Test
    public void testSendNotification_CallsAddWithCorrectData() {
        String userId = "user123";
        String eventId = "event456";
        String eventName = "Test Event";
        String type = "generic";
        String message = "You have a new notification!";

        // Call sendNotification
        manager.sendNotification(userId, eventId, eventName, type, message);

        // Verify add() was called with a Map containing the correct values
        verify(mockCollection, times(1)).add(argThat((Map<String, Object> map) ->
                userId.equals(map.get("userId")) &&
                        eventId.equals(map.get("eventId")) &&
                        eventName.equals(map.get("eventName")) &&
                        type.equals(map.get("type")) &&
                        message.equals(map.get("message")) &&
                        map.get("timestamp") instanceof Timestamp
        ));

        verify(mockTask, times(1)).addOnSuccessListener(any());
        verify(mockTask, times(1)).addOnFailureListener(any());
    }
}
