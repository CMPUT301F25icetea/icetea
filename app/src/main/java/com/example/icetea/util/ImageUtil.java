package com.example.icetea.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for image conversions and manipulations.
 * <p>
 * Provides methods to convert between {@link Bitmap} and Base64 strings,
 * as well as converting image {@link Uri}s to Base64 with size checks.
 */
public class ImageUtil {

    /**
     * Converts a Base64-encoded string to a {@link Bitmap}.
     *
     * @param base64Str The Base64 string representing the image.
     * @return A {@link Bitmap} decoded from the Base64 string, or {@code null} if the string is null or empty.
     */
    public static Bitmap base64ToBitmap(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Converts an image {@link Uri} to a Base64-encoded string.
     * <p>
     * The image will be resized to a maximum dimension of 512 pixels and compressed
     * to JPEG format with 50% quality to limit the Base64 string size.
     * Throws {@link ImageTooLargeException} if the resulting Base64 exceeds 500 KB.
     *
     * @param context The context to access content resolver.
     * @param uri     The {@link Uri} of the image to convert.
     * @return A Base64-encoded string of the image.
     * @throws IOException               If the image cannot be loaded from the given {@link Uri}.
     * @throws ImageTooLargeException    If the resulting Base64 string exceeds 500 KB.
     */
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

    /**
     * Exception thrown when an image is too large to convert to Base64 safely.
     */
    public static class ImageTooLargeException extends Exception {
        /**
         * Constructs a new exception with the specified detail message.
         *
         * @param message The detail message.
         */
        public ImageTooLargeException(String message) {
            super(message);
        }
    }
}
