package examples.pltw.org.collegeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wdumas on 4/8/16.
 */
public class FamilyListFragment extends ListFragment {
    private static final String TAG = FamilyListFragment.class.getName();

    private Family mFamily;

    public FamilyListFragment() {
        mFamily = Family.get();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.family_members_title);

        FamilyMemberAdapter adapter = new FamilyMemberAdapter(mFamily.getFamily());
        setListAdapter(adapter);

        setListAdapter(adapter);
        setHasOptionsMenu(true);

    }

    private class FamilyMemberAdapter extends ArrayAdapter<FamilyMember> {
        public FamilyMemberAdapter(ArrayList<FamilyMember> family) {
            super(getActivity(), 0, family);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_family_member, null);
            }

            FamilyMember f = getItem(position);

            TextView nameTextView = (TextView)convertView.findViewById(R.id.family_member_list_item_nameTextView);
            nameTextView.setText(f.toString());
            Log.d(TAG, "The type of FamilyMember at position " + position + " is " + f.getClass().getName());

            return convertView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        ListView listView = (ListView)v.findViewById(android.R.id.list);
        registerForContextMenu(listView);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_family_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FamilyMemberAdapter adapter = (FamilyMemberAdapter)getListAdapter();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(ApplicantActivity.EMAIL_PREF, null);
        boolean duplicate;

        switch (item.getItemId()) {
            case R.id.menu_item_new_guardian:
                Log.d(TAG, "Selected add new guardian.");
                Guardian guardian = new Guardian();
                guardian.setEmail(email);
                duplicate = false;
                for (FamilyMember f: Family.get().getFamily()) {
                    if (f.equals(guardian)) {
                        Log.i(TAG, "Duplicate" + guardian + " and " + f);
                        duplicate = true;
                    }
                }
                if(!duplicate) {
                    Family.get().addFamilyMember(guardian);
                    adapter.notifyDataSetChanged();
                }
                return true;
            case R.id.menu_item_new_sibling:
                Log.d(TAG, "Selected add new sibling.");
                Sibling sibling = new Sibling();
                sibling.setEmail(email);
                duplicate = false;
                for (FamilyMember f: Family.get().getFamily()) {
                    if (f.equals(sibling)) {
                        Log.i(TAG, "Duplicate" + sibling + " and " + f);
                        duplicate = true;
                    }
                }
                if(!duplicate) {
                    Family.get().addFamilyMember(sibling);
                    adapter.notifyDataSetChanged();
                };
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "Creating Context Menu.");
        getActivity().getMenuInflater().inflate(R.menu.family_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, "Context item selected.");

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final FamilyMemberAdapter adapter = (FamilyMemberAdapter) getListAdapter();
        final FamilyMember familyMember = adapter.getItem(info.position);

        switch (item.getItemId()) {

            case R.id.menu_item_delete_family_member:
                if (familyMember instanceof Guardian) {
                    Guardian g = (Guardian) familyMember;
                    Log.i(TAG, "Attempt to delete Guardian:" + g.toString());

                    Backendless.Data.of(Guardian.class).remove(g, new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long response) {
                            Log.i(TAG, " deleted.");
                            Family.get().deleteFamilyMember(familyMember);
                            adapter.notifyDataSetChanged();

                        }
                        @Override
                        public void handleFault(BackendlessFault deleteFault) {
                            Log.e(TAG, deleteFault.getMessage());
                        }
                    });
                } else if (familyMember instanceof Sibling) {
                    Sibling s = (Sibling) familyMember;
                    Log.i(TAG, "Attempt to delete Sibling:" + s.toString());

                    Backendless.Data.of(Sibling.class).remove(s, new AsyncCallback<Long>() {
                        @Override
                        public void handleResponse(Long response) {
                            Log.i(TAG, " deleted.");
                            Family.get().deleteFamilyMember(familyMember);
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void handleFault(BackendlessFault deleteFault) {
                            Log.e(TAG, deleteFault.getMessage());
                        }
                    });
                }
                return true;
            case R.id.menu_item_save_family_member:
                if (familyMember instanceof Guardian) {
                    Backendless.Data.of(Guardian.class).save(((Guardian) familyMember), new AsyncCallback<Guardian>() {
                        @Override
                        public void handleResponse(Guardian response) {
                            Log.i(TAG, "Saved guardian to Backendless");
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, fault.getMessage());
                        }
                    });
                } else {
                    Backendless.Data.of(Sibling.class).save(((Sibling) familyMember), new AsyncCallback<Sibling>() {
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
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        FamilyMemberAdapter adapter = (FamilyMemberAdapter) getListAdapter();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FamilyMember f = ((FamilyMemberAdapter)getListAdapter()).getItem(position);
        Log.d(TAG, f.toString() + " was clicked." + FamilyMemberActivity.class);
        Intent i = new Intent(getActivity(), FamilyMemberActivity.class);
        i.putExtra(FamilyMember.EXTRA_RELATION, f.getClass().getName());
        i.putExtra(FamilyMember.EXTRA_INDEX, position);

        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();

        Family.get().getFamily().clear();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(ApplicantActivity.EMAIL_PREF, null);
        String whereClause = "email = '" + email + "'";
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setWhereClause(whereClause);

        Backendless.Data.of(Sibling.class).find(query, new AsyncCallback<List<Sibling>>() {

            @Override
            public void handleResponse(List<Sibling> sibling) {
                if (!sibling.isEmpty()) {
                    for (Sibling g : sibling) {
                        Family.get().getFamily().add(g);
                    }
                } else {
                    Log.i(TAG, "Query returned no siblings");
                }

                FamilyMemberAdapter adapter = new FamilyMemberAdapter(mFamily.getFamily());
                setListAdapter(adapter);
                setHasOptionsMenu(true);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, fault.getMessage());
            }
        });

        Backendless.Data.of(Guardian.class).find(query, new AsyncCallback<List<Guardian>>() {

            @Override
            public void handleResponse(List<Guardian> guardian) {
                if (!guardian.isEmpty()) {
                    for (Guardian g : guardian) {
                        Family.get().getFamily().add(g);
                    }
                } else {
                    Log.i(TAG, "Query returned no guardians");
                }

                FamilyMemberAdapter adapter = new FamilyMemberAdapter(mFamily.getFamily());
                setListAdapter(adapter);
                setHasOptionsMenu(true);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, fault.getMessage());
            }
        });

    }
}
