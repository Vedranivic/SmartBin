package com.example.vedranivic.smartbin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vedranivic.smartbin.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvPercentage)
    TextView tvPercentage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getData();
    }

    private void getData() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("-LYJcWV50-PrLDQq9Vo8").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int percentage = (int)Math.round(dataSnapshot.getValue(User.class).getPercentage());
                if (percentage > 50 && percentage < 85){
                    tvPercentage.setBackgroundColor(getResources().getColor(R.color.yellow));
                }
                else if (percentage > 85){
                    tvPercentage.setBackgroundColor(getResources().getColor(R.color.red));
                }
                else {
                    tvPercentage.setBackgroundColor(getResources().getColor(R.color.green));
                }
                tvPercentage.setText(String.valueOf(percentage) + "% full");
                progressBar.setProgress(percentage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
