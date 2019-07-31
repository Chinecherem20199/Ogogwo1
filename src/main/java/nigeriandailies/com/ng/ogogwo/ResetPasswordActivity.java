package nigeriandailies.com.ng.ogogwo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import model.Users;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle, titleQestions;
    private EditText phoneNumber, question1, question2;
    private Button verifyQuestionBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.reset_password_text_view);
        titleQestions = findViewById(R.id.question_title);
        phoneNumber = findViewById(R.id.security_question_phonenumber);
        question1 = findViewById(R.id.security_question_1);
        question2 = findViewById(R.id.security_question_2);
        verifyQuestionBtn = findViewById(R.id.verify_question);
    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);



        if (check.equals("settings"))
        {

            pageTitle.setText("Set Questions");
            titleQestions.setText("Please set Answer for the following security questions");
            verifyQuestionBtn.setText("Set");

            verifyQuestionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer1 = question1.getText().toString();
                    String answer2 = question2.getText().toString();

                    if (question1.equals("") && question2.equals("")){
                        Toast.makeText(ResetPasswordActivity.this, "Please answer both questions.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference().child("Users")
                                .child(Prevalent.currentOnlineUser.getPhonenumber());

                        HashMap<String, Object>userdataMap = new HashMap<>();
                        userdataMap.put("answer1", answer1);
                        userdataMap.put("answer2", answer2);

                        reference.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(ResetPasswordActivity.this, "You have answered security question successfully.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                    }
                }
            });
        }
        else if (check.equals("login"))
        {
            phoneNumber.setVisibility(View.VISIBLE);

        }
    }
}
