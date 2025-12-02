package com.example.icetea;

import static org.junit.Assert.*;

import com.example.icetea.models.ImageItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ImagesTest {

    @Test
    public void testImageItemDeletion() {
        // Setup fake images
        ImageItem userAvatar = new ImageItem("user", "user123", "base64Avatar");
        ImageItem eventPoster = new ImageItem("event", "event456", "base64Poster");

        List<ImageItem> images = new ArrayList<>();
        images.add(userAvatar);
        images.add(eventPoster);

        // Check initial state
        assertEquals(2, images.size());

        // Simulate deleting user avatar
        images.remove(userAvatar);
        assertEquals(1, images.size());
        assertFalse(images.contains(userAvatar));

        // Simulate deleting event poster
        images.remove(eventPoster);
        assertTrue(images.isEmpty());
    }
}
