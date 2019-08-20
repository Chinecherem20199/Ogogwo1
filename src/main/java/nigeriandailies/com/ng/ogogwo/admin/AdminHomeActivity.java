package nigeriandailies.com.ng.ogogwo.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nigeriandailies.com.ng.ogogwo.Buyer.HomeActivity;
import nigeriandailies.com.ng.ogogwo.Buyer.MainActivity;
import nigeriandailies.com.ng.ogogwo.R;

public class AdminHomeActivity extends AppCompatActivity {
        private Button createNewOrderBtn, addminLogoutBtn, maintainProductsBtn , checkApproveProductBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

                createNewOrderBtn = findViewById(R.id.check_order_btn);
        addminLogoutBtn = findViewById(R.id.admin_logout_btn);
        maintainProductsBtn = findViewById(R.id.maintain_btn);
        checkApproveProductBtn = findViewById(R.id.check_approve_product_btn);


        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admins", "Admins");
                startActivity(intent);

            }
        });

        addminLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        createNewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminNewOrderActivity.class);
                startActivity(intent);

            }
        });
        checkApproveProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminCheckNewProductsActivity.class);
                startActivity(intent);
            }
        });

    }
}
