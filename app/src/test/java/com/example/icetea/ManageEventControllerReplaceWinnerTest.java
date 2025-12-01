package com.example.icetea;

import static org.mockito.Mockito.*;

import com.example.icetea.home.ManageEventController;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
//THERE WAS USE OF LLM IN THE BELOW CODE
/**
 * US 01.05.01
 * As an entrant, I want another chance to be chosen from the waiting list
 * if a selected user declines an invitation.
 */
public class ManageEventControllerReplaceWinnerTest {

    private ManageEventController controller;

    private WaitlistDB mockWaitlistDB;
    private EventDB mockEventDB;
    private FirebaseFirestore mockFirestore;
    private WriteBatch mockBatch;
    private Task<Void> mockCommitTask;

    private MockedStatic<WaitlistDB> waitlistDBStatic;
    private MockedStatic<EventDB> eventDBStatic;
    private MockedStatic<FirebaseFirestore> firestoreStatic;

    @Before
    public void setUp() {
        controller = new ManageEventController();

        mockWaitlistDB = mock(WaitlistDB.class);
        mockEventDB = mock(EventDB.class);
        mockFirestore = mock(FirebaseFirestore.class);
        mockBatch = mock(WriteBatch.class);
        mockCommitTask = mock(Task.class);

        // ---- static singletons ----
        waitlistDBStatic = Mockito.mockStatic(WaitlistDB.class);
        waitlistDBStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);

        eventDBStatic = Mockito.mockStatic(EventDB.class);
        eventDBStatic.when(EventDB::getInstance).thenReturn(mockEventDB);

        firestoreStatic = Mockito.mockStatic(FirebaseFirestore.class);
        firestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);

        when(mockFirestore.batch()).thenReturn(mockBatch);

        doReturn(mockBatch)
                .when(mockBatch)
                .update(any(), anyString(), any());
        doReturn(mockBatch)
                .when(mockBatch)
                .update(any(), anyString(), any(), any());

        when(mockBatch.commit()).thenReturn(mockCommitTask);
        doReturn(mockCommitTask)
                .when(mockCommitTask)
                .addOnSuccessListener(any());
        doReturn(mockCommitTask)
                .when(mockCommitTask)
                .addOnFailureListener(any());
    }

    @After
    public void tearDown() {
        waitlistDBStatic.close();
        eventDBStatic.close();
        firestoreStatic.close();
    }

    @Test
    public void waitingEntrantGetsAnotherChanceWhenWinnerDeclines() {

        String eventId = "event123";
        String declinedUserId = "userOld";
        String newUserId = "userNew";


        DocumentSnapshot declinedEntry = mock(DocumentSnapshot.class);
        when(declinedEntry.exists()).thenReturn(true);
        when(declinedEntry.getString("status"))
                .thenReturn(Waitlist.STATUS_DECLINED);

        Task<DocumentSnapshot> declinedTask = mock(Task.class);
        when(declinedTask.isSuccessful()).thenReturn(true);
        when(declinedTask.getResult()).thenReturn(declinedEntry);

        doAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener =
                    invocation.getArgument(2);
            listener.onComplete(declinedTask);
            return null;
        }).when(mockWaitlistDB)
                .getWaitlistEntry(eq(declinedUserId), eq(eventId), any());

        DocumentSnapshot waitingEntry = mock(DocumentSnapshot.class);
        when(waitingEntry.getString("userId")).thenReturn(newUserId);

        QuerySnapshot waitingSnapshot = mock(QuerySnapshot.class);
        when(waitingSnapshot.isEmpty()).thenReturn(false);
        when(waitingSnapshot.getDocuments())
                .thenReturn(Collections.singletonList(waitingEntry));

        Task<QuerySnapshot> waitingTask = mock(Task.class);
        when(waitingTask.isSuccessful()).thenReturn(true);
        when(waitingTask.getResult()).thenReturn(waitingSnapshot);

        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener =
                    invocation.getArgument(2);
            listener.onComplete(waitingTask);
            return null;
        }).when(mockWaitlistDB)
                .getEntrantsByStatus(eq(eventId), eq(Waitlist.STATUS_WAITING), any());
        DocumentSnapshot eventDoc = mock(DocumentSnapshot.class);
        when(eventDoc.exists()).thenReturn(true);
        when(eventDoc.getString("name")).thenReturn("Test Event");

        Task<DocumentSnapshot> eventTask = mock(Task.class);
        when(eventTask.isSuccessful()).thenReturn(true);
        when(eventTask.getResult()).thenReturn(eventDoc);

        doAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener =
                    invocation.getArgument(1);
            listener.onComplete(eventTask);
            return null;
        }).when(mockEventDB).getEvent(eq(eventId), any());

        controller.replaceWinner(declinedUserId, eventId, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                //...
            }

            @Override
            public void onFailure(Exception e) {
                throw new AssertionError("Replacement should succeed", e);
            }
        });
    }
}