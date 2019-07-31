package nigeriandailies.com.ng.ogogwo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText registerUserName;

    private EditText registerFirstname,registerLastname,
            registerPhonenumber, registerEmail,registerPassword;
    private Button createAccount;
    private ProgressDialog loadingBar;

    private  static final String EMAIL_NAME ="@.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerUserName = findViewById(R.id.register_username_input);
        registerFirstname = findViewById(R.id.register_userfirstname_input);
        registerLastname = findViewById(R.id.register_userlastname_input);
        registerPhonenumber = findViewById(R.id.register_new_phonenumber);
        registerEmail= findViewById(R.id.register_useremail_input);
        registerPassword = findViewById(R.id.register_password);
        createAccount = findViewById(R.id.create_account_btn);
        loadingBar = new ProgressDialog(this);



        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }
    private void CreateAccount(){
        String username = registerUserName.getText().toString();
        String fname = registerFirstname.getText().toString();
        String lname = registerLastname.getText().toString();
        String phonenumber = registerPhonenumber.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "please enter your Username", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(fname)){
            Toast.makeText(this, "please enter your Firstname", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(lname)){
            Toast.makeText(this, "please enter your Lastname", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this, "please enter your Phonenumber", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "please enter your Email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter your Password", Toast.LENGTH_LONG).show();
        }else{
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhonenumber(username,fname,lname,phonenumber,email,password);
        }
    }

    private void ValidatePhonenumber(final String username, final String fname, final String lname,
                                     final String phonenumber, final String email, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phonenumber).exists()))
                {
                    HashMap<String, Object>userdataMap = new HashMap<>();
                    userdataMap.put("phonenumber", phonenumber);
                    userdataMap.put("username", username);
                    userdataMap.put("fname", fname);
                    userdataMap.put("lname", lname);
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);

                    RootRef.child("Users").child(phonenumber).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Congratulation your account has been created",
                                                Toast.LENGTH_SHORT ).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Network error, please try again later... ",
                                                Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(RegisterActivity.this, "this" + "" +  phonenumber + "" + "Already exist.",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
