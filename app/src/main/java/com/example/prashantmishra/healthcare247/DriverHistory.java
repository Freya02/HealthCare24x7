package com.example.prashantmishra.healthcare247;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverHistory extends AppCompatActivity {
    RecyclerView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list= findViewById(R.id.blog_list);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.keepSynced(true);
        FirebaseRecyclerAdapter<SingleHistory,SingleHistoryHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<SingleHistory, SingleHistoryHolder>(SingleHistory.class,R.layout.history_row,SingleHistoryHolder.class,ref) {
            @Override
            protected void populateViewHolder(SingleHistoryHolder viewHolder, SingleHistory model, int position) {
                viewHolder.setDestination(model.getDest_address());
                viewHolder.setDate(model.getDate());
                viewHolder.SetTime(model.getTime());
                viewHolder.setContact(model.getContact());
            }
        };
        list.setAdapter(firebaseRecyclerAdapter);
    }
    public static class SingleHistoryHolder extends RecyclerView.ViewHolder{

        View view;

        public SingleHistoryHolder(View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setDestination(String dest){
            TextView t=view.findViewById(R.id.dest_add);
            t.setText(dest);
        }
        public void setDate(String date){
            TextView t=view.findViewById(R.id.date);
            t.setText(date);
        }
        public void SetTime(String time){
            TextView t=view.findViewById(R.id.time);
            t.setText(time);
        }
        public void setContact(String contact){
            TextView t=view.findViewById(R.id.contact);
            t.setText(contact);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            startActivity(new Intent(DriverHistory.this,MainActivity2.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
