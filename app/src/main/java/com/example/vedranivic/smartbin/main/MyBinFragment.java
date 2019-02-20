package com.example.vedranivic.smartbin.main;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vedranivic.smartbin.R;
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

/*----------------------------------------
Main Fragment of the main screen for displaying
current bin state (percentage of fullness).
The data is retrieved from Firebase Database.
----------------------------------------*/
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

        // retrieving userID that was stored in Shared Prefferences during successful setup process
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

    // retrieving percentage data from Firebase Database
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

    // updating display (color and level of the bin) based on percentage
    private void updateDisplay() {
        if(getContext()!=null) {
            tvPercentage.setText(String.valueOf(percentage).concat("%"));
            progressBar.setProgress(percentage);

            if (percentage >= 50 && percentage < 85) {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.yellow), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText(getString(R.string.WarningYellow));
            } else if (percentage >= 85) {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText(getString(R.string.WarningRed));
            } else {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText("");
            }
        }
    }

}
