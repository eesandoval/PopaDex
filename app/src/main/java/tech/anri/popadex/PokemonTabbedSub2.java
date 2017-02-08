package tech.anri.popadex;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PokemonTabbedSub2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PokemonTabbedSub2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PokemonTabbedSub2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public SimpleAdapter adapter;
    // TODO: Rename and change types of parameters
    private String clickedName;

    private OnFragmentInteractionListener mListener;

    public PokemonTabbedSub2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PokemonTabbedSub2.
     */
    // TODO: Rename and change types and number of parameters
    public static PokemonTabbedSub2 newInstance(String param1) {
        PokemonTabbedSub2 fragment = new PokemonTabbedSub2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokemon_tabbed_sub2, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle instanceSavedState) {
        super.onActivityCreated(instanceSavedState);

        // Setup the database for querying
        Pokemon p = new Pokemon(this.getActivity());
        SQLiteDatabase pRead = p.getReadableDatabase();

        // Query the database into a cursor c
        String[] columns = new String[]{"ID"};
        Cursor c;

        if (clickedName == null)
            clickedName = "Pikachu";
        if (clickedName.equals("Farfetch'd")) {
            c = pRead.query("Pokemon",columns,"ID=105",null,null,null,null);
        } else if (clickedName.equals("Oricorio Pa'u Style")) {
            c = pRead.query("Pokemon", columns, "ID=843", null, null, null, null);
        } else {
            c = pRead.query("Pokemon", columns, "Name='" + clickedName + "'", null, null, null, null);
        }

        // Grab what we pulled from the DB by advancing the cursor once
        c.moveToNext();

        int pokemonID = c.getInt(0);

        c.close();
        // Extract the ability data from PokemonMoves
        columns = new String[]{"MoveID", "Method", "Concept"};
        c = pRead.query("PokemonMoves",columns,"PokemonID=" + String.valueOf(pokemonID),null,null,null,null);

        // Store it into variables to close the cursor immediately
        ArrayList<Integer> moveIDs = new ArrayList<>();
        ArrayList<String> moveMethods = new ArrayList<>();
        ArrayList<Integer> moveConcepts = new ArrayList<>();
        while(c.moveToNext()) {
            moveIDs.add(c.getInt(0));
            moveMethods.add(c.getString(1));
            moveConcepts.add(c.getInt(2));
        }
        // Close the cursor
        c.close();

        // Last step for moves: grab info from the Moves table
        ArrayList<String> moveNames = new ArrayList<>();
        ArrayList<String> moveTypes = new ArrayList<>();
//        ArrayList<String> moveCategories = new ArrayList<>();
//        ArrayList<Integer> movePowers = new ArrayList<>();
//        ArrayList<Integer> moveAccuracies = new ArrayList<>();
//        ArrayList<Integer> movePPs = new ArrayList<>();
//        ArrayList<String> moveTMs = new ArrayList<>();
//        ArrayList<String> moveEffects = new ArrayList<>();
//        ArrayList<String> moveEffectChances = new ArrayList<>();
        columns = new String[]{"Name", "Type"};//, "Category", "Power", "Accuracy", "PP", "TM", "Effect", "EffectChance"};
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 0; i < moveIDs.size(); ++i) {
            c = pRead.query("Moves", columns, "ID=" + String.valueOf(moveIDs.get(i)), null, null, null, null);
            while(c.moveToNext())
            {
                HashMap<String, String> datum = new HashMap<>();
                moveNames.add(c.getString(0));
                moveTypes.add(c.getString(1));
//                moveCategories.add(c.getString(2));
//                movePowers.add(c.getInt(3));
//                moveAccuracies.add(c.getInt(4));
//                movePPs.add(c.getInt(5));
//                moveTMs.add(c.getString(6));
//                moveEffects.add(c.getString(7));
//                moveEffectChances.add(c.getString(8));
                datum.put("MoveName", c.getString(0));
                int moveConceptTemp = moveConcepts.get(i);
                if (moveConceptTemp == 0)
                    datum.put("MoveMethod", moveMethods.get(i).substring(0,1).toUpperCase() + moveMethods.get(i).substring(1));
                else
                    datum.put("MoveMethod", moveMethods.get(i).substring(0,1).toUpperCase() + moveMethods.get(i).substring(1) + " " + moveConcepts.get(i));
                data.add(datum);
            }
            c.close();
        }
        EditText enteredMoveName = (EditText)getView().findViewById(R.id.editTextPokemonMoves);

        pRead.close();
        final ListView levelListView = (ListView) getView().findViewById(R.id.levelListVIew);
        adapter = new SimpleAdapter(this.getActivity(), data, android.R.layout.simple_list_item_2, new String[] {"MoveName", "MoveMethod"}, new int[] {android.R.id.text1, android.R.id.text2});

        levelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String clickedMove = levelListView.getItemAtPosition(position).toString();
                Intent intent = new Intent(PokemonTabbedSub2.this.getContext(),MoveActivity.class);
                intent.putExtra("MoveName", clickedMove);
                startActivity(intent);
            }
        });
        levelListView.setAdapter(adapter);
        levelListView.setTextFilterEnabled(true);
        enteredMoveName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3)
            {
                // TODO Auto-generated method stub

            }



            @Override
            public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3)
            {
                // TODO Auto-generated method stub

            }



            @Override
            public void afterTextChanged( Editable arg0)
            {
                // TODO Auto-generated method stub
                PokemonTabbedSub2.this.adapter.getFilter().filter(arg0);

            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
