package com.example.fyptest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class ActivityCamera extends AppCompatActivity {

    View v;
    ImageView iv;
    Button bCapture;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //v = inflater.inflate(R.layout.activity_camera, container, false);
        setContentView(R.layout.activity_camera);
        iv = (ImageView) findViewById(R.id.im_camera);
        bCapture = (Button) findViewById(R.id.button_capture);
        bCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPictureTakerAction();
                //s.setClickable(true);
            }
        });
        bCapture.setClickable(true);
    }

    public void dispatchPictureTakerAction(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /**if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }else{
            Toast.makeText(this, "Camera cannot be opened", Toast.LENGTH_LONG).show();
        }**/
        try{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            iv.setImageBitmap(imageBitmap);
            //imageBitmap.recycle();
            /**if (!database.finishPicture(byteArray)) {
                Toast.makeText(this, "Photo cannot be registered", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "You can start ride now", Toast.LENGTH_LONG).show();
            }**/
            //byte[] bimage = database.getPic(0);
            //Bitmap bitmap = BitmapFactory.decodeByteArray(bimage, 0, bimage.length);
            //iv.setImageBitmap(bitmap);
        }
    }

}
