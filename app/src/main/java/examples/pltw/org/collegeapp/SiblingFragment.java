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
public class SiblingFragment extends Fragment {
    private static final String TAG = "SiblingFragment";

    private TextView mFirstName;
    private EditText mEditFirstName;
    private TextView mLastName;
    private EditText mEditLastName;
    private Button mSubmitButton;

    private Sibling mSibling;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sibling, container, false);

        mSibling = new Sibling();

        mFirstName = (TextView) rootView.findViewById(R.id.siblingFirstNameTextView);
        mFirstName.setText(mSibling.getFirstName());
        mLastName = (TextView) rootView.findViewById(R.id.siblingLastNameTextView);
        mLastName.setText(mSibling.getLastName());

        mEditFirstName = (EditText) rootView.findViewById(R.id.siblingFirstNameEditText);
        mEditLastName = (EditText) rootView.findViewById(R.id.siblingLastNameEditText);

        mSubmitButton = (Button) rootView.findViewById(R.id.siblingSubmitButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSibling.setFirstName(mEditFirstName.getText().toString());
                mFirstName.setText(mSibling.getFirstName());
                mSibling.setLastName(mEditLastName.getText().toString());
                mLastName.setText(mSibling.getLastName());

                saveToBackendless();
            }
        });

        return rootView;
    }

    public void saveToBackendless() {

        Backendless.Data.of(Sibling.class).save(mSibling, new AsyncCallback<Sibling>() {
            @Override
            public void handleResponse(Sibling response) {
                Log.i(TAG, "Saved sibling to Backendless");
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
            mSibling = (Sibling) familyList.get(index);
            Log.d(TAG, mSibling.toString());
            mFirstName.setText(mSibling.getFirstName());
            mLastName.setText(mSibling.getLastName());
        }
    }
}
