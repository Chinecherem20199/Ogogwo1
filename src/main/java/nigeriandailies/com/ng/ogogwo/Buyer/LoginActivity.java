package nigeriandailies.com.ng.ogogwo.Buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;
import model.Users;
import nigeriandailies.com.ng.ogogwo.Prevalent;
import nigeriandailies.com.ng.ogogwo.R;
import nigeriandailies.com.ng.ogogwo.Sellers.SellerProductCategoryActivity;
import nigeriandailies.com.ng.ogogwo.admin.AdminHomeActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText loginPhoneNumber, loginPassword;
    private CheckBox rememberCkb;
    private TextView forgetPasswordLink, adminPanelLink, nonAdminPanelLink;
    private Button loginBtn;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginPhoneNumber = findViewById(R.id.login_phone_number_input);
        loginPassword = findViewById(R.id.login_password_input);


        rememberCkb= findViewById(R.id.remember_me_ckb);
        Paper.init(this);

        forgetPasswordLink = findViewById(R.id.forget_password_link);
        adminPanelLink = findViewById(R.id.admin_panel_link);
        nonAdminPanelLink = findViewById(R.id.admin_not_panel_link);
        loginBtn = findViewById(R.id.login_btn);

        loadingBar = new ProgressDialog(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        adminPanelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login Admins");
                adminPanelLink.setVisibility(View.INVISIBLE);
                nonAdminPanelLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        nonAdminPanelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login");
                nonAdminPanelLink.setVisibility(View.INVISIBLE);
                adminPanelLink.setVisibility(View.VISIBLE);
                parentDbName = "Users";
            }
        });
        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

    }

    private void LoginUser() {
        String phonenumber = loginPhoneNumber.getText().toString();
        String password = loginPassword.getText().toString();

          if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this, "please enter your Phonenumber", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please enter your Password", Toast.LENGTH_LONG).show();
        }else {
              loadingBar.setTitle("Login Account");
              loadingBar.setMessage("Please wait, while we are checking the credentials...");
              loadingBar.setCanceledOnTouchOutside(false);
              loadingBar.show();
              
              AllowAccessToAccount(phonenumber, password);
          }

    }

    private void AllowAccessToAccount(final String phonenumber, final String password) {
//        Check if the user is checked in remember me check box, then this will remember the user its account
        if (rememberCkb.isChecked()){
            Paper.book().write(Prevalent.UserPhonenumberKey,phonenumber);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

//            Check if the user is having the same phone number and password, then login else dismiss
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phonenumber).exists()){
                    Users userData = dataSnapshot.child(parentDbName).child(phonenumber).getValue(Users.class);
                    if (userData.getPhonenumber().equals(phonenumber)){
                        if (userData.getPassword().equals(password)){
                            if (parentDbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                           startActivity(intent);

                            }else {
                                if (parentDbName.equals("Users")){
                                    Toast.makeText(LoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Prevalent.currentOnlineUser = userData;
                                    startActivity(intent);
                                }
                            }
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"The password is incorrect" , Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);

                        }

                    }

                }else {
                    Toast.makeText(LoginActivity.this, "Account with this " +phonenumber + " does not exist", Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
