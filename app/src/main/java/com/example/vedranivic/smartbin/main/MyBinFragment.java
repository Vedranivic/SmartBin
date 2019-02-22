package com.example.vedranivic.smartbin.main;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.base.Constants;
import com.example.vedranivic.smartbin.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        databaseReference.child(userID).child("percentage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                percentage = (int)Math.round(dataSnapshot.getValue(Double.class));
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
                lbWarning.setPaintFlags(lbWarning.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));

            } else if (percentage >= 85) {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText(getString(R.string.WarningRed));
                lbWarning.setPaintFlags(lbWarning.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            } else {
                progressBar.getProgressDrawable().clearColorFilter();
                progressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                lbWarning.setText("");
            }
        }
    }

    @OnClick(R.id.lbWarning)
    public void fullBinClick() {
        if (percentage >= 85 && Constants.IsNetworkConnected(getActivity())) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Your bin is full!");
            alertDialog.setMessage("Do you want to schedule it for collection?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    (dialog, which) -> {
                        databaseReference.child(userID).child("scheduledForCollection").setValue(true);
                        dialog.dismiss();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    (dialog, which) -> {
                        databaseReference.child(userID).child("scheduledForCollection").setValue(false);
                        dialog.dismiss();
                    });
            alertDialog.show();
        }
    }
}
