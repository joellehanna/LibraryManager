package com.example.joellehanna.libraryuser;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerView_Config {
    private UserAdapter mUserAdapter;
    private Context mContext;
    public void setConfig(RecyclerView recycleView, Context context, List<User> users, List<String> keys ){
        mContext = context;
        mUserAdapter = new UserAdapter(users, keys);
        recycleView.setLayoutManager( new LinearLayoutManager( context ));
        recycleView.setAdapter( mUserAdapter );
    }

    class UserItemView extends RecyclerView.ViewHolder {
        private ImageView mPicture;
        private TextView mUsername;

        private String key;
        public UserItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.user_list_item, parent, false) );

            mUsername = (TextView) itemView.findViewById( R.id.username );
            mPicture = (ImageView) itemView.findViewById( R.id.picture );

        }
        public void bind(User user, String key){
            mUsername.setText(user.getUsername());
            Picasso.get().load(user.getPicture())
                    .resize(50, 50)
                    .into(mPicture);
            this.key = key;
        }


    }
    class UserAdapter extends RecyclerView.Adapter<UserItemView>{
        private List<User> mUserList;
        private List<String> mKeys;

        public UserAdapter(List<User> mUserList, List<String> mKeys) {
            this.mUserList = mUserList;
            this.mKeys = mKeys;
        }


        @Override
        public UserItemView onCreateViewHolder(ViewGroup parent, int viewType ) {
            return new UserItemView(parent);
        }

        @Override
        public void onBindViewHolder(UserItemView holder, int position) {
            holder.bind(mUserList.get( position ),mKeys.get(position));

        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }
    }
}
