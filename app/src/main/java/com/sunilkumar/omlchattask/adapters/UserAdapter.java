package com.sunilkumar.omlchattask.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sunilkumar.omlchattask.R;
import com.sunilkumar.omlchattask.activities.MessageActivity;
import com.sunilkumar.omlchattask.models.Users;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    List<Users> usersList;
    private boolean isChat;

    public UserAdapter(Context context, List<Users> usersList, boolean isChat) {
        this.context = context;
        this.usersList = usersList;
        this.isChat = isChat;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, userStatus;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.textViewUsersOneItem);
            userStatus = itemView.findViewById(R.id.textViewStatus);
            imageView = itemView.findViewById(R.id.imageViewUsersOneItem);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Users user = usersList.get(position);
        holder.username.setText(user.getUsername());

//        Log.d("USERS", user.getImageURL());
        if(user.getImageURL().equals("default"))
        {

            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        } else
        {
            Glide.with(context)
                    .load(user.getImageURL())
                    .into(holder.imageView);
        }

        if(isChat)
        {
            if(user.getStatus().equals("Online"))
            {
                holder.userStatus.setText("Online");
            }
        }

       // Toast.makeText(context, "user: "+user.getStatus(), Toast.LENGTH_SHORT).show();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
