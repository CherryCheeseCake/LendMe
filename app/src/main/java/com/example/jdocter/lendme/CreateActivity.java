package com.example.jdocter.lendme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jdocter.lendme.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    String launchCamera = "launchcamera";
    private Bitmap bitmapPostImage;

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    public String photoFileName = "photo.jpg";
    private ImageView ivPostImage;
    private CheckedTextView ctSunday;
    private CheckedTextView ctMonday;
    private CheckedTextView ctTuesday;
    private CheckedTextView ctWednesday;
    private CheckedTextView ctThursday;
    private CheckedTextView ctFriday;
    private CheckedTextView ctSaturday;
    private Button btnLend;
    private ImageButton ibCamera;
    private EditText etDescription;
    private EditText etItemName;
    private EditText etPrice;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        ctSunday = findViewById(R.id.ctSunday);
        ctMonday = findViewById(R.id.ctMonday);
        ctTuesday = findViewById(R.id.ctTuesday);
        ctWednesday = findViewById(R.id.ctWednesday);
        ctThursday = findViewById(R.id.ctThursday);
        ctFriday = findViewById(R.id.ctFriday);
        ctSaturday = findViewById(R.id.ctSaturday);
        btnLend = findViewById(R.id.btnLend);
        ibCamera = findViewById(R.id.ibCamera);
        ivPostImage = findViewById(R.id.ivPostImage);
        etDescription = findViewById(R.id.etDescription);
        etItemName = findViewById(R.id.etItemName);
        etPrice = findViewById(R.id.etPrice);

        // launch camera at appropriate time (i.e. directly from the create post button
        if (true == getIntent().getExtras().getBoolean(launchCamera)) {
            openCameraIntent();
        } else {

            // set onclick listener for image if camera was not originally launched
            ivPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCameraIntent();
                }
            });

            // hide little camera image
            ibCamera.setBackgroundResource(R.color.transparent);
        }


        // set onclick listener for little camera
        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraIntent();
            }
        });

        // create new post when button is clicked
        btnLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File file;
                final ParseUser user = ParseUser.getCurrentUser();
                final String description = etDescription.getText().toString();
                final String itemName = etItemName.getText().toString();
                final Float price = Float.valueOf(etPrice.getText().toString());
                List<Integer> availableDays = new ArrayList<>();
                CheckedTextView[] ctArray = new CheckedTextView[]{ctSunday,ctMonday,ctTuesday,ctWednesday,ctThursday,ctFriday,ctSaturday};

                for (int i = 0; i<7;i++) {
                    CheckedTextView x = ctArray[i];
                    if (x.isChecked()== true) {
                        availableDays.add(i+1);
                    }
                }


                // try converting bitmap to parsefile
                try {
                    file = getImageFile(bitmapPostImage);
                    final ParseFile parseFile = new ParseFile(file);
                    // create post
                    createPost(parseFile,user,description,itemName,price,availableDays);
                } catch (IOException e) {
                    Log.e("MainActivity","Bit map could not be converted to ParseFile");
                    e.printStackTrace();
                }
            }
        });

    }

    // resizes bitmap and writes to file
    public File getImageFile(Bitmap bitmap) throws IOException {
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        File resizedUri = createImageFile(photoFileName + "_resized");
        File resizedFile = new File(resizedUri.getPath());
        resizedFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(resizedFile);
        // Write the bytes of the bitmap to file
        fos.write(bytes.toByteArray());
        fos.close();
        return resizedFile;
    }


    public void onDayCheckboxClicked(View view) {
        if (false == ((CheckedTextView) view).isChecked()) {
            ((CheckedTextView) view).setChecked(true);
            ((CheckedTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.checkboxbutton_checked,0,0);
        } else {
            ((CheckedTextView) view).setChecked(false);
            ((CheckedTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.checkboxbutton_unchecked,0,0);
        }
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile(photoFileName);
            } catch (IOException ex) {
                Log.e("CreateActivity","createImageFile Failed");
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile(String filename) throws IOException {
        // create file name by date
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir.getPath() + File.separator + filename);

        imageFilePath = file.getAbsolutePath(); // used on activity result
        return file;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            bitmapPostImage = BitmapFactory.decodeFile(imageFilePath);

            // RESIZE BITMAP, see section below
            BitmapScaler.scaleToFitWidth(bitmapPostImage, 300);
            BitmapScaler.scaleToFitHeight(bitmapPostImage, 100);

            ivPostImage.setImageBitmap(bitmapPostImage);
            ivPostImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ibCamera.setBackgroundResource(R.drawable.camera);

            // turn off big camera onclick
            ivPostImage.setOnClickListener(null);
        } else { // Result was a failure
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            Log.e("CreateActivity", "Picture wasn't taken");
        }
    }

    private void createPost(ParseFile parseFile,ParseUser user,String description,String item, Float price,List<Integer> availableDays) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setUser(user);
        newPost.setImage(parseFile);
        newPost.setPrice(price);
        newPost.setItem(item);
        newPost.setAvailableDays(availableDays);
        newPost.setLocation();

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    Log.d("CreateActivity", "Post success");
                    Intent i = new Intent(CreateActivity.this,MainActivity.class);
                    startActivity(i);
                } else {
                    Log.e("CreateActivity", "Post Failure");
                    e.printStackTrace();
                }
            }
        });

    }

}
