package com.example.liew.ideliveryserver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liew.ideliveryserver.Common.Common;
import com.example.liew.ideliveryserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInAsAdmin extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignInAsAdmin;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_sign_in_admin);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());

        btnSignInAsAdmin = (FButton) findViewById(R.id.btnSignInAsAdmin);

        //Init firebase

        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSignInAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void signInUser(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignInAsAdmin.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(localPhone).exists()) {

                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsadmin())) {

                        //If isAdmin = true
                        if (user.getPassword().equals(localPassword)) {

                            Intent login = new Intent(SignInAsAdmin.this, MainAdminActivity.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();
                        } else
                            Toast.makeText(SignInAsAdmin.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignInAsAdmin.this, "Please login with Staff account", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignInAsAdmin.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}