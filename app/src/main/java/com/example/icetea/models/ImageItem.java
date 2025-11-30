package com.example.icetea.models;

/**
 * Represents an image stored in the application.
 * <p>
 * Each ImageItem has a type (e.g., "avatar", "event"), a unique ID,
 * and a Base64 string representing the image data.
 * </p>
 */
public class ImageItem {

    /** Type of the image (e.g., "avatar", "event") */
    private String type;

    /** Unique identifier for the image */
    private String id;

    /** Base64-encoded representation of the image */
    private String base64;

    /**
     * Default constructor required for Firestore or serialization.
     */
    public ImageItem() {
        // Required empty constructor
    }

    /**
     * Constructs a new ImageItem instance.
     *
     * @param type   the type/category of the image
     * @param id     the unique identifier of the image
     * @param base64 the Base64-encoded image data
     */
    public ImageItem(String type, String id, String base64) {
        this.type = type;
        this.id = id;
        this.base64 = base64;
    }

    /** @return the image type */
    public String getType() {
        return type;
    }

    /** @param type the image type */
    public void setType(String type) {
        this.type = type;
    }

    /** @return the unique image ID */
    public String getId() {
        return id;
    }

    /** @param id the unique image ID */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the Base64-encoded image data */
    public String getBase64() {
        return base64;
    }

    /** @param base64 the Base64-encoded image data */
    public void setBase64(String base64) {
        this.base64 = base64;
    }
}