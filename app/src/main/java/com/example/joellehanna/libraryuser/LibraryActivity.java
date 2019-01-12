package com.example.joellehanna.libraryuser;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.joellehanna.libraryuser.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    static List<BookClass> bookClassList;
    static List<BookClass> bookClassListCopy;
    RecyclerView recycle;

    RecyclerAdapter recyclerAdapter;

    SearchView searchLibrary;
    ImageView scanBarcode;
    boolean barcodeFilter;

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private static String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        recycle = findViewById(R.id.recyclerLibrary);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Books");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            mUsername = firebaseUser.getDisplayName();
        }

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Value", "onDataChange called");
                bookClassList = new ArrayList<BookClass>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    BookClass value = dataSnapshot1.getValue(BookClass.class);
                    BookClass fire = new BookClass();

                    String author = value.getAuthor();
                    String title = value.getTitle();
                    String borrowedState = value.getBorrowed();
                    String dueDate = value.getDueDate();
                    long barcode = value.getBarcode();

                    String coverURL = dataSnapshot1.child("cover").getValue(String.class);

                    fire.setAuthor(author);
                    fire.setTitle(title);
                    fire.setCoverUri(Uri.parse(coverURL));
                    fire.setBorrowed(borrowedState);
                    fire.setDueDate(dueDate);
                    fire.setBarcode(barcode);

                    bookClassList.add(fire);
                }

                bookClassListCopy = new ArrayList<>(bookClassList);

                if (getIntent().getBooleanExtra("ScannerDone", false) != true) {
                    setRecyclerAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("Library", "Failed to read database" + databaseError.toException());

            }
        });

        searchLibrary = findViewById(R.id.librarySearch);
        searchLibrary.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                bookClassList = new ArrayList<>(bookClassListCopy);
                setRecyclerAdapter();
                barcodeFilter = false;
                recyclerAdapter.filter(query, barcodeFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });

        scanBarcode = findViewById(R.id.scanBarcode);
        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
                startActivity(intent);
            }
        });

        Intent barcodeIntent = getIntent();
        String barcodeSearch = barcodeIntent.getStringExtra("Barcode");

        if (barcodeSearch != null) {
            bookClassList = new ArrayList<>(bookClassListCopy);
            setRecyclerAdapter();
            barcodeFilter = true;
            //recyclerAdapter.filter(barcodeSearch, barcodeFilter);

            Query query = myRef.orderByChild("barcode").equalTo(Long.parseLong(barcodeSearch));
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                    String borrowedState = dataSnapshot.child("borrowed").getValue(String.class);

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 30);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    final String dueDate = sdf1.format(c.getTime());

                    if (borrowedState.equals(mUsername)) {
                        // The user is already borrowing the book: give it back?
                        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);

                        builder.setTitle("Book in your possession");
                        builder.setMessage("This book is currently in your possession. Do you want to " +
                                "return it to the library?)");
                        builder.setIcon(getResources().getDrawable(R.drawable.agenda));
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dataSnapshot.child("borrowed").getRef().setValue("False");
                                dataSnapshot.child("dueDate").getRef().setValue("empty");
                            }
                        });
                        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {

                        if (borrowedState.equals("False")) {
                            // The book is currently free: borrow it?

                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                            builder.setTitle("Book available!");
                            builder.setMessage("This book is currently available. Do you want to" +
                                    "borrow it?");
                            builder.setIcon(getResources().getDrawable(R.drawable.agenda));
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dataSnapshot.child("borrowed").getRef().setValue(mUsername);
                                    dataSnapshot.child("dueDate").getRef().setValue(dueDate);
                                }
                            });

                            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            // The book is currently borrowed by someone else: join the waiting list?

                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                            builder.setTitle("Book not available");
                            builder.setMessage("This book is currently not available. Do you want to" +
                                    "join the waiting list?");
                            builder.setIcon(getResources().getDrawable(R.drawable.agenda));

                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    long waitingIndex = dataSnapshot.child("waitingList").getChildrenCount();
                                    dataSnapshot.child("waitingList").child(Long.toString(waitingIndex)).getRef().setValue(mUsername);
                                }
                            });

                            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void setRecyclerAdapter() {

        recyclerAdapter = new RecyclerAdapter(bookClassList, LibraryActivity.this);
        RecyclerView.LayoutManager recyclerManager = new GridLayoutManager(LibraryActivity.this, 1);
        recycle.setLayoutManager(recyclerManager);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);
    }
}
