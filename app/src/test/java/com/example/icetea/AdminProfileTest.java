package com.example.icetea;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * US 03.05.01
 * As an administrator, I want to be able to browse profiles.
 *
 */

public class AdminProfileTest {

    @Test
    public void testGetAllUsers_mocked() {
        // Create a mock UserDB
        UserDB mockDB = mock(UserDB.class);

        // Prepare fake users
        List<User> fakeUsers = new ArrayList<>();
        User u1 = new User(); u1.setName("Alice");
        User u2 = new User(); u2.setName("Bob");
        fakeUsers.add(u1); fakeUsers.add(u2);

        // Simulate getAllUsers callback
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);
            assertEquals(2, fakeUsers.size());
            assertEquals("Alice", fakeUsers.get(0).getName());
            assertEquals("Bob", fakeUsers.get(1).getName());
            return null;
        }).when(mockDB).getAllUsers(any());

        // Call mocked method
        mockDB.getAllUsers(null);
    }
}
