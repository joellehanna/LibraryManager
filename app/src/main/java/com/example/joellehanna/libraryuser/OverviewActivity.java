package com.example.joellehanna.libraryuser;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class OverviewActivity extends RecyclerView.Adapter<OverviewActivity.AllBooks> {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;

    static List<BookClass> bookClassList;
    static List<BookClass> bookClassListCopy;
    RecyclerView recycle;
    List<BookClass> bookList = new ArrayList<>();
    static List<BookClass> bookListCopy = new ArrayList<>();

    Context context;

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private static String mUsername;

    public OverviewActivity() {
        this.bookList = bookList;
        this.bookListCopy = new ArrayList<>(bookList);
        this.context = context;
    }

    @Override
    public AllBooks onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
        final AllBooks AllBooks = new AllBooks(view);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mUsername = firebaseUser.getDisplayName();

        return AllBooks;
    }

    @Override
    public void onBindViewHolder(final AllBooks holder, int position) {
        final BookClass myList = bookList.get(position);
        holder.author.setText(myList.getAuthor());
        holder.title.setText(myList.getTitle());
        Picasso.get().load(myList.getCoverUri())
                .resize(220, 350)
                .into(holder.coverUri);

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

    class AllBooks extends RecyclerView.ViewHolder {

        TextView author, title, borrowed;
        ImageView coverUri;
        TextView borrowButton, detailsButton;
        TextView borrowedState, dueDate;

        public AllBooks(View itemView) {

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