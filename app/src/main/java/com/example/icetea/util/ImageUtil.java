package com.example.icetea.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImageUtil {
    public static Bitmap base64ToBitmap(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String uriToBase64(Context context, Uri uri) throws IOException, ImageTooLargeException {
        if (uri == null) {
            throw new IOException("URI is null");
        }
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            throw new IOException("Failed to load bitmap", e);
        }

        int maxDimension = 512;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = Math.min((float) maxDimension / width, (float) maxDimension / height);
        if (scale < 1f) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * scale), (int) (height * scale), true);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        int base64Size = Base64.encodeToString(imageBytes, Base64.DEFAULT).getBytes(StandardCharsets.UTF_8).length;
        if (base64Size > 500 * 1024) {
            throw new ImageTooLargeException("Image is too large to upload");
        }
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }
    public static class ImageTooLargeException extends Exception {
        public ImageTooLargeException(String message) { super(message); }
    }
}
