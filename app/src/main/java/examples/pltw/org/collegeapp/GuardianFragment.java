package examples.pltw.org.collegeapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;

/**
 * Created by wdumas on 4/8/16.
 */
public class GuardianFragment extends Fragment {
    private static final String TAG = "GuardianFragment";

    private TextView mFirstName;
    private EditText mEditFirstName;
    private TextView mLastName;
    private EditText mEditLastName;
    private TextView mOccupation;
    private EditText mEditOccupation;
    private Button mSubmitButton;

    private Guardian mGuardian;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_guardian, container, false);

        mGuardian = new Guardian();

        mFirstName = (TextView) rootView.findViewById(R.id.guardianFirstNameTextView);
        mFirstName.setText(mGuardian.getFirstName());
        mLastName = (TextView) rootView.findViewById(R.id.guardianLastNameTextView);
        mLastName.setText(mGuardian.getLastName());

        mEditFirstName = (EditText) rootView.findViewById(R.id.guardianFirstNameEditText);
        mEditLastName = (EditText) rootView.findViewById(R.id.guardianLastNameEditText);

        mOccupation = (TextView) rootView.findViewById(R.id.guardianOccupationTextView);
        mOccupation.setText(mGuardian.getFirstName());
        mEditOccupation = (EditText) rootView.findViewById(R.id.guardianOccupationEditText);

        mSubmitButton = (Button) rootView.findViewById(R.id.guardianSubmitButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuardian.setFirstName(mEditFirstName.getText().toString());
                mFirstName.setText(mGuardian.getFirstName());
                mGuardian.setLastName(mEditLastName.getText().toString());
                mLastName.setText(mGuardian.getLastName());
                mGuardian.setOccupation(mEditOccupation.getText().toString());
                mOccupation.setText(mGuardian.getLastName());

                saveToBackendless();
            }
        });

        return rootView;
    }

    public void saveToBackendless() {


        Backendless.Data.of(Guardian.class).save(mGuardian, new AsyncCallback<Guardian>() {
            @Override
            public void handleResponse(Guardian response) {
                Log.i(TAG, "Saved guardian to Backendless");
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, fault.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        int index = getActivity().getIntent().getIntExtra(FamilyMember.EXTRA_INDEX, -1);
        ArrayList<FamilyMember> familyList = Family.get().getFamily();
        if (index != -1) {
            mGuardian = (Guardian) familyList.get(index);
            Log.d(TAG, mGuardian.toString());
            mFirstName.setText(mGuardian.getFirstName());
            mLastName.setText(mGuardian.getLastName());
            mOccupation.setText(mGuardian.getOccupation());
        }
    }
}
