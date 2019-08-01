package nigeriandailies.com.ng.ogogwo.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import nigeriandailies.com.ng.ogogwo.R;

public class SellersLoginActivity extends AppCompatActivity {

    private Button loginSellerBtn;
    private EditText emailIput, passwordInput;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_login);
        loadingBar = new ProgressDialog(this);

        loginSellerBtn = findViewById(R.id.seller_login_btn);
        emailIput = findViewById(R.id.seller_login_email);
        passwordInput = findViewById(R.id.seller_login_password);

        mAuth = FirebaseAuth.getInstance();

        loginSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });
    }

    private void loginSeller() {
        final String email = emailIput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            loadingBar.setTitle("Sellers Account Login");
            loadingBar.setMessage("Please wait while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                Intent i = new Intent(SellersLoginActivity.this, SellerHomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }

                        }
                    });
        }
        else
        {
            Toast.makeText(this, "email and password do not match", Toast.LENGTH_SHORT).show();
        }
    }
}
