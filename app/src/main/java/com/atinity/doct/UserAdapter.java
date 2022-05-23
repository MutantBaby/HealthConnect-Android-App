package com.atinity.doct;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context homeActivity_5;
    ArrayList<Users> usersArrayList;

    public UserAdapter(HomeActivity_5 homeActivity_5, ArrayList<Users> usersArrayList) {
        this.homeActivity_5 = homeActivity_5;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(homeActivity_5).inflate(R.layout.item_user_row, parent, false);
        return new ViewHolder(view);
    }

    // setting data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);

        holder.user_name.setText(users.name);
        holder.user_status.setText(users.status);
        Picasso.get().load(users.imageUrl).into(holder.user_profile);

        // to hide the current user from list
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUid())) {
            holder.itemView.setVisibility(View.GONE);
        }

        // item from list
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(homeActivity_5, ChatActivity.class);

            // user data of which we gonna chat
            intent.putExtra("name", users.getName());
            intent.putExtra("receiverImage", users.getImageUrl());
            intent.putExtra("uid", users.getUid());

            // we use startActivity like this cz we are in adapter
            homeActivity_5.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_profile;
        TextView user_name;
        TextView user_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_profile = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            user_status = itemView.findViewById(R.id.user_status);
        }
    }
}
