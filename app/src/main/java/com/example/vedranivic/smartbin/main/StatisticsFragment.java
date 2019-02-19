package com.example.vedranivic.smartbin.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.model.UnicomConstants;
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
Fragment on the main screen for displaying
current statistics of the user’s waste
management. Data is retrieved from Firebase
database and calculations are made
----------------------------------------*/
public class StatisticsFragment extends Fragment {

    public static final String TAG = StatisticsFragment.class.getSimpleName();

    @BindView(R.id.tvCollected)
    TextView tvCollected;
    @BindView(R.id.tvExpenses)
    TextView tvExpenses;
    @BindView(R.id.tvWaste)
    TextView tvWaste;
    @BindView(R.id.tvCollectedThis)
    TextView tvCollectedThis;
    @BindView(R.id.tvExpensesThis)
    TextView tvExpensesThis;
    @BindView(R.id.tvWasteThis)
    TextView tvWasteThis;


    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String userID;
    private UnicomConstants unicomConstants;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, view);

        if(getActivity()!=null) {
            userID = getActivity().getSharedPreferences("SMARTBIN", MODE_PRIVATE).getString("USER_ID", "");
        }

        getStatistics();
        return view;
    }

    // calculate and display statistics
    private void getStatistics() {

        // get User data
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.child("lastCollectionNumber").exists()) {
                    tvCollected.setText(String.valueOf(user.getLastCollectionNumber()));
                    tvWaste.setText(String.format("%.2f",user.getLastWasteAmount()).concat(getResources().getString(R.string.unit_m_cube)));
                }
                if(dataSnapshot.child("collectionNumber").exists()){
                    tvCollectedThis.setText(String.valueOf(user.getCollectionNumber()));
                    tvWasteThis.setText(String.format("%.2f",user.getWasteAmount()).concat(getResources().getString(R.string.unit_m_cube)));
                }

                // get constant parameters for calculations (parameters are stored online)
                databaseReference.child("parameters").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        unicomConstants = dataSnapshot.getValue(UnicomConstants.class);
                        Double expenses, expensesLast;
                        expenses = unicomConstants.getMJU()
                                + (unicomConstants.getJC()*(Integer.parseInt(user.getBinSize().split(" ")[0]))*user.getCollectionNumber()*1);
                        expenses += expenses*unicomConstants.getPDV()/100;
                        expenses += 0.01*(Integer.parseInt(user.getBinSize().split(" ")[0]))*user.getCollectionNumber()*1;
                        tvExpensesThis.setText(String.format("%.2f",expenses).concat(getResources().getString(R.string.unit_HRK)));

                        expensesLast = unicomConstants.getMJU()
                                + (unicomConstants.getJC()*(Integer.parseInt(user.getBinSize().split(" ")[0]))*user.getLastCollectionNumber()*1);
                        expensesLast += expensesLast*unicomConstants.getPDV()/100;
                        expensesLast += 0.01*(Integer.parseInt(user.getBinSize().split(" ")[0]))*user.getLastCollectionNumber()*1;
                        tvExpenses.setText(String.format("%.2f", expensesLast).concat(getResources().getString(R.string.unit_HRK)));

                        if(expenses < expensesLast){
                            tvExpensesThis.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_trending_down_24dp,0);
                        }
                        else {
                            tvExpensesThis.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_trending_up_24dp,0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG,databaseError.getMessage());
                    }
                });

                // setting the trend icon (up or down) for values
                if(user.getCollectionNumber() < user.getLastCollectionNumber()){
                    tvCollectedThis.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_trending_down_24dp,0);
                }
                else {
                    tvCollectedThis.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_trending_up_24dp,0);
                }

                if(user.getWasteAmount() < user.getLastWasteAmount()){
                    tvWasteThis.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_trending_down_24dp,0);
                }
                else {
                    tvWasteThis.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_trending_up_24dp,0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,databaseError.getMessage());
            }
        });
    }


}
