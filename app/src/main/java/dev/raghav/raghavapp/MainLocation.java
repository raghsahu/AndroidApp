package dev.raghav.raghavapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class MainLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

        private static final String TAG = "MainLocation";
    private static final int REQUEST_TAKE_GALLERY_VIDEO =2 ;
    private GoogleApiClient mGoogleApiClient;
        private Location mLocation;
        private LocationManager mLocationManager;
    ProgressDialog dialog;

        private LocationRequest mLocationRequest;
        private com.google.android.gms.location.LocationListener listener;
        private long UPDATE_INTERVAL = 5 * 1000;
        private long FASTEST_INTERVAL = 5000;
        private LocationManager locationManager;

    EditText mLatitudeTextView;
    EditText mLongitudeTextView;
    EditText mAddress;
    EditText category, description, poc, mobile, placename;

    String Category, Description, Poc, Mobile, Placename, Lat,Long,Address,City;

    Spinner spinCity;
    ArrayList<String> ChooseCity=new ArrayList<>();
    private ArrayAdapter<String> CityAdapter;
    private ArrayList<CityModel> CityList=new ArrayList<>();

    HashMap<Integer, CityModel> CityHashMap=new HashMap<Integer, CityModel>();

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
     Button btnSelect, btn_submit, btn_video_select;
     TextView text_video_path;
     ImageView ivImage,iv_logout;
     String userChoosenTask;
    String output;
     File destination, vid;
     SessionManager manager;
   String filemanagerstring;
     String selectedVideoPath;
    public Activity donut_progress;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            getSupportActionBar().hide();
            manager=new SessionManager(MainLocation.this);

            TextView t2 = (TextView) findViewById(R.id.link_tv);
            t2.setText(Html.fromHtml("<a href=\"https://www.infocentroid.com\">Developed by InfoCentroid</a>"));
            t2.setMovementMethod(LinkMovementMethod.getInstance());

            mLatitudeTextView =  findViewById((R.id.lat1));
            mLongitudeTextView =  findViewById((R.id.long1));
            mAddress =  findViewById((R.id.address));
            ivImage = (ImageView) findViewById(R.id.post_image);
            iv_logout = (ImageView) findViewById(R.id.logout);
            btnSelect = (Button) findViewById(R.id.btn_img);
            btn_submit = (Button) findViewById(R.id.btn_submit);
            btn_video_select=findViewById(R.id.btn_img1);
            text_video_path=findViewById(R.id.post_video);
            spinCity=findViewById(R.id.spin_city);
           category =  findViewById((R.id.category));
           description =  findViewById((R.id.description));
           poc =  findViewById((R.id.poc));
           mobile =  findViewById((R.id.mobile));
           placename =  findViewById((R.id.place));

           // new GetCityExecuteTask().execute();

            btnSelect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
            //*******************video upload*********************
            btn_video_select.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    selectVideo();
                }
            });
            //***********************************************
            iv_logout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MainLocation.this).setTitle("VETS")
                            .setMessage("Are you sure, you want to logout this app");

                    dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            exitLauncher();
                        }

                        private void exitLauncher() {
                            manager.logoutUser();
                            Intent intent = new Intent(MainLocation.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    final android.app.AlertDialog alert = dialog.create();
                    alert.show();

                }




            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    City=spinCity.getSelectedItem().toString();
                    Category=category.getText().toString();
                    Description=description.getText().toString();
                    Poc=poc.getText().toString();
                    Mobile=mobile.getText().toString();
                    Placename=placename.getText().toString();
                    Lat=mLatitudeTextView.getText().toString();
                    Long=mLongitudeTextView.getText().toString();
                    Address=mAddress.getText().toString();

                    if (!Poc.isEmpty() && !Lat.isEmpty() && !Long.isEmpty() ) {
                        try {
//                            if (destination != null) {
                                if (Connectivity.isNetworkAvailable(MainLocation.this)) {
                                   // vid = new File(selectedVideoPath);
                                    new SurveyExecuteTask().execute();
                                } else {
                                    Toast.makeText(MainLocation.this, "No Internet", Toast.LENGTH_SHORT).show();
                                }
                           // }
//                            else {
//                                if (selectedVideoPath !=null){
//                                    new SurveyExecuteTask().execute();
//                                }else {
//                                    Toast.makeText(MainLocation.this, "Image not found", Toast.LENGTH_SHORT).show();
//                                }


                           // }
                        }
                          catch (Exception e){
                        System.out.print(e);

                           // Toast.makeText(MainLocation.this, "Please select image", Toast.LENGTH_SHORT).show();

                        }

                        }else{
                            Toast.makeText(MainLocation.this, "Please enter Name/POC", Toast.LENGTH_SHORT).show();
                        }


                }
            });

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

            checkLocation(); //check whether location service is enable or not in your  phone
            //************************************************************************************
        }

    private void selectVideo() {

//        Intent intent = new Intent();
//        intent.setType("video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_TAKE_GALLERY_VIDEO);

       //
        // startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }



//***********************************************************************************************
    @Override
        public void onConnected(Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            startLocationUpdates();

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if(mLocation == null){
                startLocationUpdates();
            }
            if (mLocation != null) {

                // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
                //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
            } else {
                Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Connection Suspended");
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        }

        @Override
        protected void onStart() {
            super.onStart();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        protected void startLocationUpdates() {
            // Create the location request
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);
            // Request location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
            Log.d("reque", "--->>>>");
        }

        @Override
        public void onLocationChanged(Location location) {

            String msg = "Updated Location: " +
                    Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());
            mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
           // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            // You can now create a LatLng Object for use with maps
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //******************address

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude,
                        getApplicationContext(), new GeocoderHandler());
            }

        }

        private boolean checkLocation() {
            if(!isLocationEnabled())
                showAlert();
            return isLocationEnabled();
        }

        private void showAlert() {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Enable Location")
                    .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                            "use this app")
                    .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        }
                    });
            dialog.show();
        }

        private boolean isLocationEnabled() {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }


            private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            mAddress.setText(locationAddress);
           // Toast.makeText(MainLocation.this, ""+locationAddress, Toast.LENGTH_SHORT).show();
        }
    }
    //************************************************************************
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainLocation.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainLocation.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
//        intent.addCategory(Intent.CATEGORY_APP_GALLERY);
//        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
        startActivityForResult(i, SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                try {
                    onSelectFromGalleryResult(data);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
        //**************************video
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedVideoUri = data.getData();
                // OI FILE Manager
                filemanagerstring = selectedVideoUri.getPath();

                // MEDIA GALLERY
                selectedVideoPath = new String(getPath(selectedVideoUri));
                if (selectedVideoPath != null) {
                    Toast.makeText(this, "video path- "+selectedVideoPath, Toast.LENGTH_SHORT).show();
                    text_video_path.setText( selectedVideoPath);
//                    Intent intent = new Intent(MainLocation.this,
//                            VideoplayAvtivity.class);
//                    intent.putExtra("path", selectedImagePath);
//                    startActivity(intent);
                }
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

         destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            if(destination !=null)
            {
               // Toast.makeText(this, "path is"+destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "something wrong", Toast.LENGTH_SHORT).show();
            }
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) throws URISyntaxException {

        Bitmap bm=null;
        if (data != null) {

            Uri pickedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            destination = new File(cursor.getString(cursor.getColumnIndex(filePath[0])));
            cursor.close();

            //Toast.makeText(this, ""+destination, Toast.LENGTH_SHORT).show();
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }


//***************************************************
    private class GetCityExecuteTask extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(MainLocation.this);
            dialog.setMessage("processing");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            String sever_url = "http://ihisaab.in/vets/Api/get_city";
            output = HttpHandler.makeServiceCall(sever_url);
            System.out.println("getcomment_url" + output);
            return output;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();

                try {
                    //Toast.makeText(Registration_activity.this, "result is" + result, Toast.LENGTH_SHORT).show();
                    JSONObject object = new JSONObject(result);
                    String res = object.getString("responce");
                    if (res.equals("true")) {

                       // Toast.makeText(MainLocation.this, "success", Toast.LENGTH_SHORT).show();

                        JSONArray jsonArray = object.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            String masterdata_id = jsonObject1.getString("masterdata_id");
                            String masterdata_name = jsonObject1.getString("masterdata_name");
                            String type = jsonObject1.getString("type");

                            CityList.add(new CityModel(masterdata_id,masterdata_name));
                            ChooseCity.add(masterdata_name);
                            CityHashMap.put(i, new CityModel(masterdata_id, masterdata_name));

                        }

                        CityAdapter = new ArrayAdapter<String>(MainLocation.this, android.R.layout.simple_spinner_dropdown_item, ChooseCity);
                        CityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinCity.setAdapter(CityAdapter);


                    } else {

                        Toast.makeText(MainLocation.this, "No city found", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SurveyExecuteTask extends AsyncTask<Void, Long, String> {


        String result = "";

         double mFileLen;

//        public SurveyExecuteTask(File vid) {
//            this.mFileLen = vid.length();
//        }
        //  File Image;

//        public ImageUploadTask(File imgFile) {
//            this.Image = imgFile;
//
//        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainLocation.this);
           dialog.setMessage("Processing");
           // dialog.setMax(100);
            dialog.show();
            dialog.setCancelable(false);
           // dialog.setProgress(0);
//            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            int percent = (int) ((100.0 * (double) values[0] / mFileLen) + 0.5);
            dialog.setProgress(percent);
         Log.d("progerr" , ""+percent );
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("gcdfg",""+selectedVideoPath);
                org.apache.http.entity.mime.MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                String id= AppPreference.getUserid(MainLocation.this);
                if(selectedVideoPath !=null) {
                    entity.addPart("video", new FileBody(new File(selectedVideoPath)));
                }if(destination !=null)
                {
                    entity.addPart("file", new FileBody(destination));
                }
                if(selectedVideoPath !=null && destination !=null)
                {
                    entity.addPart("video", new FileBody(new File(selectedVideoPath)));
                    entity.addPart("file", new FileBody(destination));
                }


                    entity.addPart("city", new StringBody(City));
                    entity.addPart("category",new StringBody(Category));
                    entity.addPart("description",new StringBody(Description));
                    entity.addPart("poc",new StringBody(Poc));
                    entity.addPart("mobile",new StringBody(Mobile));
                    entity.addPart("place_name",new StringBody(Placename));
                    entity.addPart("lat",new StringBody(Lat));
                    entity.addPart("long",new StringBody(Long));
                    entity.addPart("full_address",new StringBody(Address));
                    entity.addPart("emp_id",new StringBody(id));

//                }else {

//                    if (destination !=null) {
//                        entity.addPart("file", new FileBody(destination));
//                    }
//                    entity.addPart("city", new StringBody(City));
//                    entity.addPart("category",new StringBody(Category));
//                    entity.addPart("description",new StringBody(Description));
//                    entity.addPart("poc",new StringBody(Poc));
//                    entity.addPart("mobile",new StringBody(Mobile));
//                    entity.addPart("place_name",new StringBody(Placename));
//                    entity.addPart("lat",new StringBody(Lat));
//                    entity.addPart("long",new StringBody(Long));
//                    entity.addPart("full_address",new StringBody(Address));
//                    entity.addPart("emp_id",new StringBody(id));
//                    entity.addPart("video", new FileBody(new File(selectedVideoPath)));
               // }
                //                for(int i =0;i<mFileLen;i++)
//                {
                   // dialog.setProgress(i);
                    result = Utilities.postEntityAndFindJson("http://ihisaab.in/vets/Api/registration_vets", entity);

                    return result;
               // }


            } catch (Exception e) {
                // something went wrong. connection with the server error
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(MainLocation.this, "res is "+result, Toast.LENGTH_LONG).show();

            //String result1 = result;
            if (result != null) {


                Log.e("result_Image", result);
                try {
                    JSONObject object = new JSONObject(result);
                    String responce = object.getString("responce");
                   // String img = object.getString("img");
                    if (responce.equals("true")) {

                        Toast.makeText(MainLocation.this, "Success", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(MainLocation.this,Activity_Success.class);
                        startActivity(intent);

                        finish();


                    } else {
                        Toast.makeText(MainLocation.this, "Some Problem", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    dialog.dismiss();
                    Toast.makeText(MainLocation.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            } else {
               dialog.dismiss();
                //  Toast.makeText(Registration.this, "No Response From Server", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        if(dialog.isShowing() && dialog !=null)
        {
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
