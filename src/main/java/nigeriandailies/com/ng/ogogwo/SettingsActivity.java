package nigeriandailies.com.ng.ogogwo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Users;

public class SettingsActivity extends AppCompatActivity  {

   private CircleImageView profileImageView;
   private EditText userPhoneNumberEditext, fullNameEditext, addressEditext;
   private TextView profileChangeTextViewBtn, closeTextBtn, updateTextBtn;
   private Button securityQuestionBtn;

   private Uri imageUri;
   private String myUrl = "";
   private StorageTask uploadTask;
   private StorageReference storageProfilePictureRef;
   private String checker = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView = findViewById(R.id.profile_image_settings);
        userPhoneNumberEditext = findViewById(R.id.settings_phone_number);
        fullNameEditext = findViewById(R.id.settings_full_name);
        addressEditext = findViewById(R.id.settings_adress);
        profileChangeTextViewBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings_btn);
        securityQuestionBtn =findViewById(R.id.security_questions_btn);
        updateTextBtn = findViewById(R.id.update_account_settings_btn);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        userInfoDisplay(profileImageView, userPhoneNumberEditext, fullNameEditext, addressEditext);

        closeTextBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "settings");
                startActivity(intent);
            }
        });

        updateTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                   userInfoSaved();
                }else{

                    updateOnlyUserInfo();
                }

            }
        });
        profileChangeTextViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this,"Error, Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent( SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }
//    This method is checking validation before saving

    private void userInfoSaved() {

//        Checking if the user is updating an empty column,
        if (TextUtils.isEmpty(fullNameEditext.getText().toString())) {
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        }
         else if (TextUtils.isEmpty(addressEditext.getText().toString())) {
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneNumberEditext.getText().toString())) {
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked")){
            uploadImage();
        }
    }
//    This method is called when you want to upload your image

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update profile");
        progressDialog.setMessage("Please wait, while we are updating your profile Info");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri !=null){
            final StorageReference fileRef = storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhonenumber() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUtl = task.getResult();
                        myUrl = downloadUtl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("username", fullNameEditext.getText().toString());
                        userMap.put("phonenumber", userPhoneNumberEditext.getText().toString());
                        userMap.put("address", addressEditext.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhonenumber()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this,"profile Info updated successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }else {
                        Toast.makeText(SettingsActivity.this,"Error, Try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }else {
            Toast.makeText(SettingsActivity.this,"Image is not selected", Toast.LENGTH_SHORT).show();

        }
    }

//this method is called when you want to update only username, phonenumber and adress
    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", fullNameEditext.getText().toString());
        userMap.put("phonenumber", userPhoneNumberEditext.getText().toString());
        userMap.put("address", addressEditext.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhonenumber()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this,"profile Info updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
// This method is called when you want to display user info
    private void userInfoDisplay(final CircleImageView profileImageView, final EditText userPhoneNumberEditext,
                                 final EditText fullNameEditext, final EditText addressEditext)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhonenumber());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String phonenumber = dataSnapshot.child("phonenumber").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();


                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditext.setText(username);
                        userPhoneNumberEditext.setText(phonenumber);
                        addressEditext.setText(address);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
