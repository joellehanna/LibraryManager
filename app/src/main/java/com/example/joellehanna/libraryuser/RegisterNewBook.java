package com.example.joellehanna.libraryuser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterNewBook extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private Button btnLogIn;
    private Button btnLogOut;
    private Button btnPermanent;
    private TextView contentTxt;
    private EditText book_title;
    private EditText book_author;
    private EditText book_genre;
    private EditText book_cover;
    private Button done;
    private Button goBack;

    private Boolean registerFormStatus;
    String authorHolder, titleHolder, genreHolder, pictureHolder;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener( authListener );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String keyID = getIntent().getExtras().getString( "key" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_new_book );
        btnLogIn = (Button) findViewById( R.id.btn_login );
        btnLogOut = (Button) findViewById( R.id.btn_sign_out );
        btnPermanent = (Button) findViewById( R.id.btn_permanent );
        contentTxt = (TextView) findViewById( R.id.scanText );
        book_author = (EditText) findViewById( R.id.book_author );
        book_cover = (EditText) findViewById( R.id.book_cover );
        book_title = (EditText) findViewById( R.id.book_title );
        book_genre = (EditText) findViewById( R.id.book_genre );
        done = (Button) findViewById( R.id.button4 );
        goBack = (Button) findViewById( R.id.button3 );


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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewBook(keyID);

                if (registerFormStatus) {
                    addBookToFirebase(keyID);
                }
            }
        });

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
                        Toast.makeText( RegisterNewBook.this, "done!", Toast.LENGTH_SHORT ).show();
                    }
                } );
            }
        } );

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    // Toast.makeText(ActivityAnonymous.this, "SignedIn", Toast.LENGTH_SHORT).show();
                    btnLogIn.setVisibility( View.GONE );
                    btnLogOut.setVisibility( View.VISIBLE );
                    btnPermanent.setVisibility( View.VISIBLE );
                    book_author.setVisibility( View.VISIBLE );
                    book_title.setVisibility( View.VISIBLE );
                    book_cover.setVisibility( View.VISIBLE );
                    book_genre.setVisibility( View.VISIBLE );
                    done.setVisibility( View.VISIBLE );
                    goBack.setVisibility( View.VISIBLE );
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    myRef = firebaseDatabase.getReference().child("Books");
                    //AddNewBook( keyID );
                    //if (registerFormStatus) {
                     //   addBookToFirebase(keyID);
                    //}
                    //Toast.makeText(ActivityAnonymous.this, mAuth.getCurrentUser().getProviderId(), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(ActivityAnonymous.this, "SignedOut", Toast.LENGTH_SHORT).show();
                    btnLogOut.setVisibility( View.GONE );
                    btnLogIn.setVisibility( View.VISIBLE );
                    btnPermanent.setVisibility( View.GONE );
                    book_author.setVisibility( View.GONE );
                    book_title.setVisibility( View.GONE );
                    book_cover.setVisibility( View.GONE );
                    book_genre.setVisibility( View.GONE );
                    done.setVisibility( View.GONE );
                    goBack.setVisibility( View.GONE );
                }
            }
        };
    }
    private void addBookToFirebase(String keyID) {

        long L;
        L = Long.parseLong( keyID );
        myRef.child(keyID).child("author").setValue(authorHolder);
        myRef.child(keyID).child("cover").setValue(pictureHolder);
        myRef.child(keyID).child("title").setValue(titleHolder);
        myRef.child(keyID).child("genre").setValue(genreHolder);
        myRef.child(keyID).child("barcode").setValue(L);
        myRef.child(keyID).child("borrowed").setValue("False");
        myRef.child(keyID ).child("dueDate").setValue("empty");
        Toast.makeText(RegisterNewBook.this, "Book Succesfully Added!", Toast.LENGTH_LONG).show();

    }

    private void AddNewBook(String keyID) {
            contentTxt.setText( "ID: " + keyID );
            authorHolder = book_author.getText().toString().trim();
            pictureHolder = book_cover.getText().toString().trim();
            titleHolder = book_title.getText().toString().trim();
            genreHolder = book_genre.getText().toString().trim();
            if (TextUtils.isEmpty( authorHolder ) || TextUtils.isEmpty( titleHolder ) ||
                    TextUtils.isEmpty( genreHolder ) || TextUtils.isEmpty( pictureHolder )) {
                //Toast.makeText(RegisterNewBook.this, "Didnt work", Toast.LENGTH_LONG);
                Toast.makeText( RegisterNewBook.this, "No Book Added", Toast.LENGTH_LONG ).show();
                registerFormStatus = false;
            } else {
                //Toast.makeText( RegisterNewBook.this, "Wo", Toast.LENGTH_LONG ).show();
                registerFormStatus = true;
            }
    }


    public void clickedGoBack(View view) {
        Intent intent = new Intent( RegisterNewBook.this, MainActivity.class );
        startActivity( intent );
    }
}