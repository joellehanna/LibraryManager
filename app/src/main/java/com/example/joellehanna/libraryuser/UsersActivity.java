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

public class UsersActivity extends RecyclerView.Adapter<UsersActivity.AllUsers> {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;

    static List<BookClass> bookClassList;
    static List<BookClass> bookClassListCopy;
    RecyclerView recycle;
    List<User> userList = new ArrayList<>();
    static List<User> userListCopy = new ArrayList<>();

    Context context;

    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private static String mUsername;

    public UsersActivity() {
        this.userList = userList;
        this.userListCopy = new ArrayList<>(userList);
        this.context = context;
    }

    @Override
    public AllUsers onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
        final AllUsers AllUsers = new AllUsers(view);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mUsername = firebaseUser.getDisplayName();

        return AllUsers;
    }

    @Override
    public void onBindViewHolder(final AllUsers holder, int position) {
        final User myList = userList.get(position);
        holder.firstname.setText(myList.getFirstName());
        holder.lastname.setText(myList.getLastName());
        holder.emailaddress.setText(myList.getemailAdress());
        Picasso.get().load(myList.getProfilepic())
                .resize(220, 350)
                .into(holder.profilePic);

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if (userList.size() == 0) {
                arr = 0;
            } else {
                arr = userList.size();
            }
        } catch (Exception e) {

        }

        return arr;
    }

    class AllUsers extends RecyclerView.ViewHolder {

        TextView firstname, lastname, emailaddress;
        ImageView profilePic;

        public AllUsers(View itemView) {

            super(itemView);
            firstname = itemView.findViewById(R.id.first_name);
            lastname = itemView.findViewById(R.id.last_name);
            profilePic = itemView.findViewById(R.id.profilepic);
            emailaddress = itemView.findViewById(R.id.emailaddress);


        }
    }


}
