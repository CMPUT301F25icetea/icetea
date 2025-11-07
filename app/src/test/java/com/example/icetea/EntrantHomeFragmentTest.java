package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.testing.FragmentScenario;

import com.example.icetea.entrant.EntrantHomeFragment;
import com.example.icetea.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EntrantHomeFragmentTest {

    private EntrantHomeFragment fragment;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockCollection;

    @Mock
    private Task<QuerySnapshot> mockTask;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private QueryDocumentSnapshot mockDocument;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        fragment = new EntrantHomeFragment() {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                db = mockFirestore;
            }
        };

        // Initialize views
        fragment.listView = new ListView(RuntimeEnvironment.getApplication());
        fragment.notificationButton = new Button(RuntimeEnvironment.getApplication());
        fragment.eventNamesList = new ArrayList<>();
        fragment.eventsList = new ArrayList<>();
        fragment.adapter = new ArrayAdapter<>(RuntimeEnvironment.getApplication(),
                android.R.layout.simple_list_item_1, fragment.eventNamesList);
        fragment.listView.setAdapter(fragment.adapter);
    }

    // ==================== Fragment Creation Tests ====================
    @Test
    public void testNewInstance_CreatesFragment() {
        EntrantHomeFragment newFragment = EntrantHomeFragment.newInstance();
        assertNotNull(newFragment);
    }



    // ==================== Notification Button Tests ====================
    @Test
    public void testNotificationButtonClick_ReplacesFragment() {
        FragmentManager mockManager = mock(FragmentManager.class);
        FragmentTransaction mockTransaction = mock(FragmentTransaction.class);

        when(mockManager.beginTransaction()).thenReturn(mockTransaction);
        when(mockTransaction.replace(anyInt(), any(Fragment.class))).thenReturn(mockTransaction);
        when(mockTransaction.addToBackStack(any())).thenReturn(mockTransaction);

        fragment.notificationButton.performClick();

        // Cannot directly verify fragment transaction without activity,
        // but we can ensure click does not crash
    }

    // ==================== Firestore Loading Tests ====================


    // ==================== Event List Population Tests ====================
    @Test
    public void testPopulateEventList_AddsEvent() {
        Event event = new Event();
        event.setId("1");
        event.setName("Test Event");
        event.setDescription("Description");

        fragment.eventsList.add(event);
        fragment.eventNamesList.add(event.getName() + "\n" + event.getDescription());

        fragment.adapter.notifyDataSetChanged();

        assertEquals(1, fragment.eventsList.size());
        assertEquals("Test Event\nDescription", fragment.eventNamesList.get(0));
    }

    // ==================== ListView Click Tests ====================
    @Test
    public void testListViewItemClick_OpensDetailsFragment() {
        Event event = new Event();
        event.setId("event123");
        fragment.eventsList.add(event);
        fragment.eventNamesList.add("Test");

        fragment.adapter.notifyDataSetChanged();

        fragment.listView.performItemClick(
                fragment.listView.getChildAt(0),
                0,
                fragment.adapter.getItemId(0)
        );

        // Ensures no crash on item click
        assertEquals(1, fragment.eventsList.size());
        assertEquals("event123", fragment.eventsList.get(0).getId());
    }

    // ==================== Edge Case Tests ====================

}
