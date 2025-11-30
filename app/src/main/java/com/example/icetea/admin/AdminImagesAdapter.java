package com.example.icetea.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.ImageItem;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a grid of images in the Admin section.
 * Each image has a delete button to remove it from the collection.
 */
public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ImageViewHolder> {

    /**
     * Interface to handle actions on image items, such as deletion.
     */
    public interface ActionListener {
        /**
         * Called when the delete button of an image item is clicked.
         *
         * @param item the ImageItem associated with the clicked delete button
         */
        void onDeleteClick(ImageItem item);
    }

    private final Context context;
    private final List<ImageItem> images;
    private final ActionListener listener;

    /**
     * Constructor for AdminImagesAdapter.
     *
     * @param context  the context in which the adapter is used
     * @param images   the list of ImageItem objects to display
     * @param listener the listener to handle delete actions
     */
    public AdminImagesAdapter(Context context, List<ImageItem> images, ActionListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates the ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new ImageViewHolder instance
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_grid, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Binds the image data to the ViewHolder and sets the delete button listener.
     *
     * @param holder   the ImageViewHolder to bind data to
     * @param position the position of the item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem item = images.get(position);

        // Decode Base64 image
        if (item.getBase64() != null && !item.getBase64().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(item.getBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.image.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.image.setImageResource(R.drawable.default_poster);
            }
        } else {
            holder.image.setImageResource(R.drawable.default_poster);
        }

        // Set delete button listener
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    /**
     * Returns the number of items in the dataset.
     *
     * @return the size of the images list
     */
    @Override
    public int getItemCount() {
        return images.size();
    }

    /**
     * ViewHolder class for holding and caching views for each image item.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView image;
        ImageButton deleteButton;

        /**
         * Constructor for ImageViewHolder.
         *
         * @param itemView the root view of the image item layout
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageItem);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
