package com.example.joellehanna.libraryuser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button btnLogIn;
    private Button btnLogOut;
    private Button btnPermanent;
    private TextView contentTxt;
    private Button libraryButton;
    private Button bookScan;
    private TextView message;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener( authListener );
    }


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogIn = (Button) findViewById( R.id.btn_login );
        btnLogOut = (Button) findViewById( R.id.btn_sign_out );
        btnPermanent = (Button) findViewById( R.id.btn_permanent );


        mAuth = FirebaseAuth.getInstance();


        btnLogIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<AuthResult> resultTask = mAuth.signInAnonymously();
                resultTask.addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                    }
                } );
            }
        } );

        btnLogOut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        } );

        btnPermanent.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = "password";
                String email = "malo@malo.com";

                AuthCredential credential = EmailAuthProvider.getCredential( email, password );
                mAuth.getCurrentUser().linkWithCredential( credential ).addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        btnPermanent.setVisibility( View.GONE );
                        Toast.makeText( MainActivity.this, "done!", Toast.LENGTH_SHORT ).show();
                    }
                } );
            }
        } );

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(MainActivity.this, "SignedIn", Toast.LENGTH_SHORT).show();
                    btnLogIn.setVisibility( View.GONE );
                    btnLogOut.setVisibility( View.VISIBLE );
                    btnPermanent.setVisibility( View.VISIBLE );

                } else {
                    Toast.makeText(MainActivity.this, "SignedOut", Toast.LENGTH_SHORT).show();
                    btnLogOut.setVisibility( View.GONE );
                    btnLogIn.setVisibility( View.VISIBLE );
                    btnPermanent.setVisibility( View.GONE );
                }
            }
        };


        message = findViewById(R.id.welcomeMsg);
        message.setText(R.string.welcome);

        libraryButton = findViewById(R.id.searchBooks);
        libraryButton.setText(R.string.search_books);

        bookScan = findViewById(R.id.scanBook);
        bookScan.setText(R.string.scan_book);

        button = (Button) this.findViewById(R.id.scanBook);
        contentTxt = (TextView)findViewById(R.id.scanText);
        final Activity activity = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                contentTxt.setText("CONTENT: " + result.getContents());
                Intent intent = new Intent(this, RegisterNewBook.class);
                intent.putExtra("key",result.getContents());
                startActivity(intent);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void addBooksToFirebase() {

    }

    public void clickedSearchBook(View view) {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);

    }

    public void clickedUsers(View view) {
        Intent usersintent = new Intent(this, UserManagerActivity.class);
        startActivity(usersintent);
    }



    // uncomment the code below for not scanning in the main menu (i.e. in another activity) + add the corresponding onClick field in layout
//        public void clickedScanBook(View view) {
//
//            Intent intent = new Intent(this, searchActivity.class);
//            startActivity(intent);
//
//        }

}