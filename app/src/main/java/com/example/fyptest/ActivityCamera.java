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

import com.example.fyptest.ui.tflite.Classifier;
import com.example.fyptest.ui.tflite.TensorFlowImageClassifier;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActivityCamera extends AppCompatActivity {

    View v;
    ImageView iv;
    Button bCapture;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private static final String MODEL_PATH = "emotion.tflite";
    private static final String LABEL_PATH = "dict.txt";
    private static final int INPUT_SIZE = 48;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();

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

        initTensorFlowAndLoadModel();
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
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, INPUT_SIZE, INPUT_SIZE, false);
            final List<Classifier.Recognition> results = classifier.recognizeImage(imageBitmap);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            iv.setImageBitmap(imageBitmap);
            String topResult = results.get(0).getTitle(); // highest precision result(label)
            Float topPrecision = results.get(0).getConfidence();

            Toast.makeText(this, topResult + ", " + topPrecision, Toast.LENGTH_LONG).show();
        }
    }

    /**public TensorBuffer convertImage(byte[] input){
        try {
            Model model = Model.newInstance(v.getContext());
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{32, 28, 28}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
            return outputFeature0;
        } catch (IOException e) {
            // TODO Handle the exception
            return null;
        }
    }**/

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }


}
