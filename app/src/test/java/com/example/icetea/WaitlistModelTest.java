package com.example.icetea;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

/**
 * Unit tests for WaitlistDB logic.
 *
 * Covers:
 * - US 01.01.01: Test joining a waitlist (addToWaitlist)
 * - US 01.01.02: Test leaving a waitlist (removeFromWaitlist)
 */
@RunWith(MockitoJUnitRunner.class)
public class WaitlistModelTest {

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockEventsCollection;
    @Mock
    private CollectionReference mockWaitlistCollection;
    @Mock
    private DocumentReference mockEventDocRef;
    @Mock
    private DocumentReference mockWaitlistDocRef;
    @Mock
    private DocumentSnapshot mockEventSnapshot;
    @Mock
    private Task<DocumentSnapshot> mockGetEventTask;
    @Mock
    private Task<Void> mockSetWaitlistTask;
    @Mock
    private Task<Void> mockUpdateEventTask;
    @Mock
    private Task<Void> mockDeleteWaitlistTask;
    @Mock
    private OnCompleteListener<Void> mockListener;

    @Captor
    private ArgumentCaptor<Waitlist> waitlistCaptor;
    @Captor
    private ArgumentCaptor<OnSuccessListener<DocumentSnapshot>> getSuccessListenerCaptor;
    @Captor
    private ArgumentCaptor<OnSuccessListener<Void>> setSuccessListenerCaptor;
    @Captor
    private ArgumentCaptor<OnSuccessListener<Void>> updateSuccessListenerCaptor;

    private WaitlistDB waitlistDB;
    private MockedStatic<FirebaseFirestore> firestoreStatic;
    private MockedStatic<FieldValue> fieldValueStatic;

    private static final String TEST_EVENT_ID = "event123";
    private static final String TEST_USER_ID = "user123";
    private static final String TEST_DOC_ID = "event123_user123";

    @Before
    public void setUp() throws Exception {
        // Reset the singleton instance using reflection
        Field instanceField = WaitlistDB.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        firestoreStatic = Mockito.mockStatic(FirebaseFirestore.class);
        firestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);

        fieldValueStatic = Mockito.mockStatic(FieldValue.class);
        fieldValueStatic.when(() -> FieldValue.increment(1)).thenReturn(mock(FieldValue.class));
        fieldValueStatic.when(() -> FieldValue.increment(-1)).thenReturn(mock(FieldValue.class));

        when(mockFirestore.collection("events")).thenReturn(mockEventsCollection);
        when(mockFirestore.collection("waitlist")).thenReturn(mockWaitlistCollection);
        when(mockEventsCollection.document(TEST_EVENT_ID)).thenReturn(mockEventDocRef);
        when(mockWaitlistCollection.document(TEST_DOC_ID)).thenReturn(mockWaitlistDocRef);

        waitlistDB = WaitlistDB.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        if (firestoreStatic != null) firestoreStatic.close();
        if (fieldValueStatic != null) fieldValueStatic.close();

        // Reset singleton after test
        Field instanceField = WaitlistDB.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    // ==================== US 01.01.01: Add to Waitlist Tests ====================

    /**
     * Tests US 01.01.01: Successfully joining a waitlist that is not full.
     */
    @Test
    public void testAddToWaitlist_Success() {
        // Arrange - Setup event snapshot
        when(mockEventSnapshot.exists()).thenReturn(true);
        when(mockEventSnapshot.getLong("capacity")).thenReturn(10L);
        when(mockEventSnapshot.getLong("waitlistCount")).thenReturn(5L);

        // Mock the get() method and its chain
        when(mockEventDocRef.get()).thenReturn(mockGetEventTask);
        when(mockGetEventTask.addOnSuccessListener(any())).thenReturn(mockGetEventTask);
        when(mockGetEventTask.addOnFailureListener(any())).thenReturn(mockGetEventTask);

        // Mock the set() method and its chain
        when(mockWaitlistDocRef.set(any(Waitlist.class))).thenReturn(mockSetWaitlistTask);
        when(mockSetWaitlistTask.addOnSuccessListener(any())).thenReturn(mockSetWaitlistTask);
        when(mockSetWaitlistTask.addOnFailureListener(any())).thenReturn(mockSetWaitlistTask);

        // Mock the update() method and its chain
        when(mockEventDocRef.update(eq("waitlistCount"), any(FieldValue.class))).thenReturn(mockUpdateEventTask);
        when(mockUpdateEventTask.addOnSuccessListener(any())).thenReturn(mockUpdateEventTask);
        when(mockUpdateEventTask.addOnFailureListener(any())).thenReturn(mockUpdateEventTask);

        // Act
        waitlistDB.addToWaitlist(TEST_EVENT_ID, TEST_USER_ID, mockListener);

        // Simulate the async callbacks in order
        // 1. Trigger the get() success listener
        verify(mockGetEventTask).addOnSuccessListener(getSuccessListenerCaptor.capture());
        OnSuccessListener<DocumentSnapshot> getListener = getSuccessListenerCaptor.getValue();
        getListener.onSuccess(mockEventSnapshot);

        // 2. After get succeeds, the set() is called - capture and trigger it
        verify(mockWaitlistDocRef).set(waitlistCaptor.capture());
        verify(mockSetWaitlistTask).addOnSuccessListener(setSuccessListenerCaptor.capture());
        OnSuccessListener<Void> setListener = setSuccessListenerCaptor.getValue();
        setListener.onSuccess(null);

        // 3. After set succeeds, the update() is called - capture and trigger it
        verify(mockEventDocRef).update(eq("waitlistCount"), any(FieldValue.class));
        verify(mockUpdateEventTask).addOnSuccessListener(updateSuccessListenerCaptor.capture());
        OnSuccessListener<Void> updateListener = updateSuccessListenerCaptor.getValue();
        updateListener.onSuccess(null);

        // Assert
        assertEquals(TEST_EVENT_ID, waitlistCaptor.getValue().getEventId());
        assertEquals(TEST_USER_ID, waitlistCaptor.getValue().getUserId());
        assertEquals("waiting", waitlistCaptor.getValue().getStatus());
        verify(mockListener).onComplete(null);
    }

    /**
     * Tests US 01.01.01: Failing to join a waitlist because it is full.
     */
    @Test
    public void testAddToWaitlist_FailsWhenFull() {
        // Arrange - Setup event snapshot at capacity
        when(mockEventSnapshot.exists()).thenReturn(true);
        when(mockEventSnapshot.getLong("capacity")).thenReturn(10L);
        when(mockEventSnapshot.getLong("waitlistCount")).thenReturn(10L);

        // Mock the get() method and its chain
        when(mockEventDocRef.get()).thenReturn(mockGetEventTask);
        when(mockGetEventTask.addOnSuccessListener(any())).thenReturn(mockGetEventTask);
        when(mockGetEventTask.addOnFailureListener(any())).thenReturn(mockGetEventTask);

        // Act
        waitlistDB.addToWaitlist(TEST_EVENT_ID, TEST_USER_ID, mockListener);

        // Simulate the async callback
        verify(mockGetEventTask).addOnSuccessListener(getSuccessListenerCaptor.capture());
        getSuccessListenerCaptor.getValue().onSuccess(mockEventSnapshot);

        // Assert - Should not proceed with adding to waitlist
        verify(mockWaitlistDocRef, never()).set(any(Waitlist.class));
        verify(mockEventDocRef, never()).update(anyString(), any());
        verify(mockListener).onComplete(null);
    }

    // ==================== US 01.01.02: Remove from Waitlist Tests ====================

    /**
     * Tests US 01.01.02: Successfully leaving a waitlist.
     */
    @Test
    public void testRemoveFromWaitlist_Success() {
        // Arrange
        when(mockWaitlistDocRef.delete()).thenReturn(mockDeleteWaitlistTask);
        when(mockDeleteWaitlistTask.isSuccessful()).thenReturn(true);
        when(mockDeleteWaitlistTask.addOnCompleteListener(any())).thenReturn(mockDeleteWaitlistTask);
        when(mockEventDocRef.update(eq("waitlistCount"), any(FieldValue.class))).thenReturn(mockUpdateEventTask);
        when(mockUpdateEventTask.addOnCompleteListener(any())).thenReturn(mockUpdateEventTask);

        // Act
        waitlistDB.removeFromWaitlist(TEST_EVENT_ID, TEST_USER_ID, mockListener);

        // Simulate the async callbacks
        ArgumentCaptor<OnCompleteListener<Void>> deleteListenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(mockDeleteWaitlistTask).addOnCompleteListener(deleteListenerCaptor.capture());
        deleteListenerCaptor.getValue().onComplete(mockDeleteWaitlistTask);

        ArgumentCaptor<OnCompleteListener<Void>> updateListenerCaptor = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(mockUpdateEventTask).addOnCompleteListener(updateListenerCaptor.capture());
        updateListenerCaptor.getValue().onComplete(mockUpdateEventTask);

        // Assert
        verify(mockWaitlistDocRef).delete();
        verify(mockEventDocRef).update(eq("waitlistCount"), any(FieldValue.class));
        verify(mockListener).onComplete(mockUpdateEventTask);
    }
}