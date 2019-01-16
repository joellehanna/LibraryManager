package com.example.joellehanna.libraryuser;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by aminmekacher on 01.01.19.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHoder> {

    List<BookClass> bookList = new ArrayList<>();
    static List<BookClass> bookListCopy = new ArrayList<>();

    Context context;

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private static String mUsername;

    public RecyclerAdapter(List<BookClass> bookList, Context context) {
        this.bookList = bookList;
        this.bookListCopy = new ArrayList<>(bookList);
        this.context = context;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
        final MyHoder myHoder = new MyHoder(view);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mUsername = firebaseUser.getDisplayName();

        return myHoder;
    }

    @Override
    public void onBindViewHolder(final MyHoder holder, int position) {
        final BookClass myList = bookList.get(position);
        holder.author.setText(myList.getAuthor());
        holder.title.setText(myList.getTitle());
        Picasso.get().load(myList.getCoverUri())
                .resize(220, 350)
                .into(holder.coverUri);

        if(!myList.getBorrowed().toString().equals("False")) {
            Log.e("TAG", "Borrowed :" + myList.getBorrowed());
            holder.borrowButton.setText("Not Available");
            holder.borrowButton.setTextColor(holder.itemView.getResources().getColor(R.color.redWarning));
        }
        else {
            holder.borrowButton.setText("Available");
            holder.borrowButton.setTextColor(holder.itemView.getResources().getColor(R.color.greenText));}

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "Clicked on: " + myList.getTitle());

                if(holder.borrowedState.getVisibility() == View.INVISIBLE) { // The click shall show the TextViews to the user
                    holder.borrowedState.setVisibility(View.VISIBLE);
                    holder.borrowedState.setText("Borrowed by: " + myList.getBorrowed());

                    holder.dueDate.setVisibility(View.VISIBLE);
                    holder.dueDate.setText("Due date: " + myList.getDueDate());
                } else {
                    holder.borrowedState.setVisibility(View.INVISIBLE);
                    holder.dueDate.setVisibility(View.INVISIBLE);
                }
            }
        });

        holder.borrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myList.getBorrowed().toString().equals("False")) {

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 30);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                    final String dueDate = sdf1.format(c.getTime());

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());

                    builder.setTitle("Borrow book");
                    builder.setMessage("Are you sure you want to borrow " + myList.getTitle() + "?"
                            + "\nThe due date would be on the " + dueDate);
                    builder.setIcon(holder.itemView.getResources().getDrawable(R.drawable.agenda));

                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Query query = mDatabase.orderByChild("author").equalTo(holder.author.getText().toString());
                            query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    dataSnapshot.child("borrowed").getRef().setValue(mUsername);
                                    dataSnapshot.child("dueDate").getRef().setValue(dueDate);
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
                    });

                    builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                } else { // The book is unavailable: do you want to join the waiting list?

                    if (!myList.getBorrowed().equals(mUsername)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Join Waiting List");
                        builder.setMessage("This book is not available yet. Do you want to be added to the waiting list?");
                        builder.setIcon(holder.itemView.getResources().getDrawable(R.drawable.agenda));

                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Query query = mDatabase.orderByChild("author").equalTo(holder.author.getText().toString());
                                query.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        long waitingIndex = dataSnapshot.child("waitingList").getChildrenCount();
                                        dataSnapshot.child("waitingList").child(Long.toString(waitingIndex)).getRef().setValue(mUsername);
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
                        });

                        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else { // The user is the one currently owning the book

                        Toast.makeText(holder.itemView.getContext(), "You already own the book!", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if (bookList.size() == 0) {
                arr = 0;
            } else {
                arr = bookList.size();
            }
        } catch (Exception e) {

        }

        return arr;
    }

    public void filter (String queryText, boolean barcodeFilter) {

        int i = bookListCopy.size();

        bookList.clear();

        if (queryText.isEmpty()) {
            bookList.addAll(bookListCopy);
        } else {

            if (!barcodeFilter) {
                Log.d("t", "Not barcode here");
                queryText = queryText.toLowerCase();
                for (BookClass bookClass : bookListCopy) {
                    if (bookClass.getTitle().toLowerCase().contains(queryText) || bookClass.getAuthor().toLowerCase().contains(queryText)) {
                        bookList.add(bookClass);
                    }
                }
            } else {
                Log.d("Tag,", "Barcode here");
                for (BookClass bookClass : bookListCopy) {
                    if (Long.toString(bookClass.getBarcode()).contains(queryText)) {
                        bookList.add(bookClass);
                    }
                }

            }
        }

        notifyDataSetChanged();

        int ii = bookListCopy.size();

    }

    class MyHoder extends RecyclerView.ViewHolder {

        TextView author, title, borrowed;
        ImageView coverUri;
        TextView borrowButton, detailsButton;
        TextView borrowedState, dueDate;

        public MyHoder(View itemView) {

            super(itemView);
            author = itemView.findViewById(R.id.book_author);
            title = itemView.findViewById(R.id.book_title);
            coverUri = itemView.findViewById(R.id.book_cover);

            borrowed = itemView.findViewById(R.id.borrowedState);

            borrowButton = itemView.findViewById(R.id.borrowButton);
            detailsButton = itemView.findViewById(R.id.detailsButton);

            borrowedState = itemView.findViewById(R.id.borrowedState);
            dueDate = itemView.findViewById(R.id.dueDate);

        }
    }
}