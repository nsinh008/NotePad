package com.org.book.note;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ShareActionProvider;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.net.Uri;


/*
* Reference for storing and retrieving image from database:
* https://stackoverflow.com/questions/7331310/how-to-store-image-as-blob-in-sqlite-how-to-retrieve-it
*
* Reference for listing menu options:
* https://developer.android.com/guide/topics/ui/menus
*
* Reference for share through e-mail
* https://developer.android.com/training/sharing/shareaction
 */
public class EditNote extends AppCompatActivity {

    private NoteDB myDB;
    EditText name;
    EditText text;
    private CoordinatorLayout coordinatorLayout;
    String dateString;
    int value;
    Snackbar sb;
    int id_To_Update = 0;
    static final String STATE_USER = "user";
    private String mUser;
    private ShareActionProvider mShareActionProvider;
    private static final int PICK_IMAGE = 1;
    public static SharedPreferences pref;
    private ImageView image;
    private Bitmap bitmap;
    private byte[] image_array;
    private byte[] img;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getString(STATE_USER);
        } else {
            mUser = "NewUser";
        }

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        name = (EditText) findViewById(R.id.notename);
        text = (EditText) findViewById(R.id.notetext);
        image = (ImageView) findViewById(R.id.add_image);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout2);
        myDB = new NoteDB(this);

        String title = name.getText().toString();
        String text1 = text.getText().toString();

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            value = extras.getInt("id");
        if (value > 0) {
            //sb = Snackbar
              //      .make(coordinatorLayout, "Note Id : " + String.valueOf(value), Snackbar.LENGTH_LONG);
            //sb.show();
            Cursor rs = myDB.getNote(value);
            id_To_Update = value;
            rs.moveToFirst();
            String nam = rs.getString(rs.getColumnIndex(myDB.name));
            String contents = rs.getString(rs.getColumnIndex(myDB.remark));

            if (rs.getBlob(rs.getColumnIndex(myDB.image_array)) != null) {
                System.out.println("checking blob if blob is not null");
                image_array = rs.getBlob(rs.getColumnIndex(myDB.image_array));
                image.setImageBitmap(BitmapFactory.decodeByteArray(image_array, 0, image_array.length));
            }

            if (!rs.isClosed()) {
                rs.close();
            }

            name.setText((CharSequence) nam);
            text.setText((CharSequence) contents);


        }

    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.edit_menu, menu);
           // MenuItem item = menu.findItem(R.id.menu_item_share);
          //  mShareActionProvider = (ShareActionProvider) item.getActionProvider();
            return true;
        }


        public boolean onOptionsItemSelected (MenuItem item){
            super.onOptionsItemSelected(item);
            switch (item.getItemId()) {
                case R.id.Delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.DeleteNote)
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            myDB.deleteNote(id_To_Update);
                                            Toast.makeText(EditNote.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(
                                                    getApplicationContext(),
                                                    listNote.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                        }
                                    });
                    AlertDialog d = builder.create();
                    d.setTitle("Are you sure");
                    d.show();
                    return true;
                case R.id.Save:
                    Bundle extras = getIntent().getExtras();
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c.getTime());
                     dateString = formattedDate;
                    if (extras != null) {
                        int Value = extras.getInt("id");
                        if (Value > 0) {
                            if (text.getText().toString().trim().equals("")
                                    || name.getText().toString().trim().equals("")) {
                                Toast.makeText(EditNote.this, "Please fill Name of the Note!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (myDB.updateNote(id_To_Update, name.getText().toString(), dateString, text.getText().toString(), image_array)) {
                                    Toast.makeText(EditNote.this, "Your note updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditNote.this, "There's an error in update!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (text.getText().toString().trim().equals("")
                                    || name.getText().toString().trim().equals("")) {
                                Toast.makeText(EditNote.this, "Please fill Name of the Note!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (myDB.addNotes(name.getText().toString(), dateString,
                                        text.getText().toString(), image_array)) {
                                    Toast.makeText(EditNote.this, "Successfully added the Note!", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(EditNote.this, "There's an error adding the Note!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    return true;
                case R.id.Share: {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text.getText());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    return true;
                }

                case R.id.Image: {
                    // Reference about permissions: https://www.youtube.com/watch?v=SMrB97JuIoM
                    if (ActivityCompat.checkSelfPermission(EditNote.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditNote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE);
                    }
                    else{
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_IMAGE);
                    return true;
                    }
                }


                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        @Override
        public void onSaveInstanceState (Bundle savedInstanceState){
            savedInstanceState.putString(STATE_USER, mUser);
            super.onSaveInstanceState(savedInstanceState);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            Log.i("columnIndex", "columnIndex: " + columnIndex);

            String picturePath = cursor.getString(columnIndex);
            Log.i("picturePath", "picturePath: " + picturePath);
            cursor.close();

            decodeFile(picturePath);


        }
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        ByteArrayOutputStream stream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        image_array= stream.toByteArray();

        image.setImageBitmap(bitmap);
    }




    @Override
        public void onBackPressed () {
            Intent intent = new Intent(
                    getApplicationContext(),
                    listNote.class);


            startActivity(intent);
            finish();
            return;
        }
    }
