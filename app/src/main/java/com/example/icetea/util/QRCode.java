package com.example.icetea.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.OutputStream;

/**
 * Utility class for generating and saving QR codes.
 * <p>
 * Provides methods to generate a QR code bitmap from a string and display it in an ImageView,
 * as well as to save that QR code to the device's storage.
 */
public class QRCode {

    /**
     * Generates a QR code from the given text and sets it to an ImageView.
     *
     * @param qrText      The text to encode into the QR code.
     * @param qrImageView The ImageView in which to display the generated QR code.
     */
    public static void generateQRCode(String qrText, ImageView qrImageView) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrText, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            qrImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the QR code displayed in an ImageView to the device's Pictures/EventQRCodes folder.
     *
     * @param context     Context used to access the ContentResolver.
     * @param qrImageView ImageView containing the QR code bitmap.
     * @param callback    Callback to notify success or failure of the save operation.
     */
    public static void downloadQrCode(Context context, ImageView qrImageView, Callback<Void> callback) {

        BitmapDrawable drawable = (BitmapDrawable) qrImageView.getDrawable();
        if (drawable == null) {
            callback.onFailure(new Exception("No QR code to save"));
            return;
        }

        Bitmap bitmap = drawable.getBitmap();

        String fileName = "EventQR_" + System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EventQRCodes");

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri != null) {
                OutputStream outStream = resolver.openOutputStream(uri);
                if (outStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.close();
                    callback.onSuccess(null);
                }
            }
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
