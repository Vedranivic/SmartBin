package com.example.vedranivic.smartbin;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vedranivic.smartbin.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class MyBinFragment extends Fragment {

    public static final String TAG = MyBinFragment.class.getSimpleName();

    @BindView(R.id.tvPercentage)
    TextView tvPercentage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.lbWarning)
    TextView lbWarning;

    private int percentage = 0;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybin, container, false);
        ButterKnife.bind(this, view);


        if(getActivity()!=null) {
            userID = getActivity().getSharedPreferences("SMARTBIN", MODE_PRIVATE).getString("USER_ID", "");
        }

        // set default bin indicator appeareance
        updateDisplay();

        // retrieve data (setting value event listener)
        if(userID!="") {
            getData();
        }

        return view;
    }

    private void getData() {
        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                percentage = (int)Math.round(dataSnapshot.getValue(User.class).getPercentage());
                updateDisplay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });
    }

    private void updateDisplay() {
        if(getContext()!=null) {
            tvPercentage.setText(String.valueOf(percentage) + "%");
            progressBar.setProgress(percentage);

            if (percentage >= 50 && percentage < 85) {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.yellow), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText("Your bin is getting full. Consider some recycling and sorting.");
            } else if (percentage >= 85) {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText("Your bin is full.");
            } else {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText("");
            }
        }
    }

}
