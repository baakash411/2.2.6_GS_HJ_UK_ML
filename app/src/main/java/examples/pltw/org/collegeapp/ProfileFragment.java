package examples.pltw.org.collegeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by wdumas on 4/8/16.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private static final int REQUEST_DATE_OF_BIRTH = 0;
    private static final int WITHIN_8_YEARS = 2008;
    private Button mDateOfBirthButton;

    private TextView mFirstName;
    private EditText mEditFirstName;
    private TextView mLastName;
    private EditText mEditLastName;
    private Button mSubmitButton;
    //step 3
    private ImageButton mSelfieButton;
    private ImageView mSelfieView;
    private File mSelfieFile;
    private Profile mProfile;
    //step 7
    private final int REQUEST_SELFIE = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfile = new Profile();

        mFirstName = (TextView) rootView.findViewById(R.id.profileFirstNameTextView);
        mFirstName.setText(mProfile.getFirstName());
        mLastName = (TextView) rootView.findViewById(R.id.profileLastNameTextView);
        mLastName.setText(mProfile.getLastName());

        mEditFirstName = (EditText) rootView.findViewById(R.id.profileFirstNameEditText);
        mEditLastName = (EditText) rootView.findViewById(R.id.profileLastNameEditText);

        mSubmitButton = (Button) rootView.findViewById(R.id.profileSubmitButton);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfile.setFirstName(mEditFirstName.getText().toString());
                mFirstName.setText(mProfile.getFirstName());
                mProfile.setLastName(mEditLastName.getText().toString());
                mLastName.setText(mProfile.getLastName());

                saveToBackendless();
            }
        });

        mDateOfBirthButton = (Button) rootView.findViewById(R.id.profileDateOfBirthButton);

        mDateOfBirthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mProfile.getDateOfBirth());
                dialog.setTargetFragment(ProfileFragment.this, REQUEST_DATE_OF_BIRTH);
                dialog.show(fm, "DialogDateOfBirth");

            }
        });
        //step 6
        mSelfieFile = getPhotoFile();
        mSelfieView =(ImageView) rootView.findViewById(R.id.profile_pic);
        mSelfieButton = (ImageButton) rootView.findViewById(R.id.profile_camera);
        //step 8b
        final Intent captureSelfie = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakeSelfie = mSelfieFile != null &&
                captureSelfie.resolveActivity(getActivity().getPackageManager()) != null;
        mSelfieButton.setEnabled(canTakeSelfie);
        if (canTakeSelfie) {
            Uri uri = Uri.fromFile(mSelfieFile);
            captureSelfie.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mSelfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureSelfie, REQUEST_SELFIE);
            }
        });
        //step 11
        updateSelfieView();
        return rootView;
    }
    //step 5
    public File getPhotoFile() {
        File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, mProfile.getPhotoFilename());
    }
    //step 10
    public void updateSelfieView(){
        if(mSelfieFile != null && mSelfieFile.exists()){
            BitmapFactory.decodeFile(mSelfieFile.getPath());
        }
    }
    public void saveToBackendless() {

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(ApplicantActivity.EMAIL_PREF, null);
        if (mProfile.getEmail() == null) {
            mProfile.setEmail(email);
        }

        String whereClause = "email = '" + email + "'";
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setWhereClause(whereClause);
        Backendless.Data.of(Profile.class).find(query, new AsyncCallback<List<Profile>>() {
            @Override
            public void handleResponse(List<Profile> profile) {
                if (!profile.isEmpty()) {
                    String profileId = profile.get(0).getObjectId();
                    Log.d(TAG, "Object ID: " + profileId);
                    mProfile.setObjectId(profileId);
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Failed to find profile: " + fault.getMessage());
            }
        });

        Backendless.Data.of(Profile.class).save(mProfile, new AsyncCallback<Profile>() {
            @Override
            public void handleResponse(Profile response) {
                Log.i(TAG, "Saved profile to Backendless");
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, fault.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE_OF_BIRTH) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE_OF_BIRTH);
            try {
                if (date.getYear() + 1900 <= WITHIN_8_YEARS) {
                    mProfile.setDateOfBirth(date);
                    mDateOfBirthButton.setText(mProfile.getDateOfBirth().toString());
                } else throw new AgeException("Who are you, Michael Kearney?");
            } catch (AgeException e) {
                Log.i(TAG, e.joinMessageAndYear(e.getMessage(), date.getYear() + 1900));
            }
        }
        //step 12
        else if (requestCode == REQUEST_SELFIE){
            updateSelfieView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        super.onStart();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(ApplicantActivity.EMAIL_PREF, null);

        String whereClause = "email = '" + email + "'";
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setWhereClause(whereClause);
        Backendless.Data.of(Profile.class).find(query, new AsyncCallback<List<Profile>>() {
            @Override
            public void handleResponse(List<Profile> profile) {
                if (!profile.isEmpty()) {
                    mProfile = profile.get(0);
                    Log.d(TAG, "Retrieved Profile: " + mProfile.getObjectId());
                    mFirstName.setText(mProfile.getFirstName());
                    mLastName.setText(mProfile.getLastName());
                    String dob = mProfile.getDateOfBirth().toString();
                    mDateOfBirthButton.setText(dob);
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Failed to find profile: " + fault.getMessage());
            }
        });
    }
}
