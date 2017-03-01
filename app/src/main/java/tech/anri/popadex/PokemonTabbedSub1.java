package tech.anri.popadex;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PokemonTabbedSub1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PokemonTabbedSub1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PokemonTabbedSub1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String clickedName;
    private int HP;
    private int Attack;
    private int Defense;
    private int SpAtk;
    private int SpDef;
    private int Speed;
    private int Dex;
    private int pokemonID;
    private OnFragmentInteractionListener mListener;

    public PokemonTabbedSub1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PokemonTabbedSub1.
     */
    // TODO: Rename and change types and number of parameters
    public static PokemonTabbedSub1 newInstance(String param1) {
        PokemonTabbedSub1 fragment = new PokemonTabbedSub1();
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
        return inflater.inflate(R.layout.fragment_pokemon_tabbed_sub1, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        // Setup the database for querying
        Pokemon p = new Pokemon(this.getActivity());
        SQLiteDatabase pRead = p.getReadableDatabase();

        // Query the database into a cursor c
        String[] columns = new String[]{"Name", "HP", "Attack", "Defense", "SpAtk", "SpDef", "Speed", "Type1", "Type2", "Dex", "ID"};
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

        // Store it into variables so we can close the cursor immediately
        String Name = c.getString(0);
        String Type1 = c.getString(7);
        String Type2 = c.getString(8);
        HP = c.getInt(1);
        Attack = c.getInt(2);
        Defense = c.getInt(3);
        SpAtk = c.getInt(4);
        SpDef = c.getInt(5);
        Speed = c.getInt(6);
        Dex = c.getInt(9);
        pokemonID = c.getInt(10);

        // Close the cursor
        c.close();

        // Extract the ability data from PokemonAbilities
        columns = new String[]{"AbilityID", "Hidden"};
        c = pRead.query("PokemonAbilities",columns,"PokemonID=" + String.valueOf(pokemonID),null,null,null,null);

        // Store it into variables to close the cursor immediately
        ArrayList<Integer> abilityIDs = new ArrayList<>();
        ArrayList<String> abilityHidden = new ArrayList<>();
        while(c.moveToNext()) {
            abilityIDs.add(c.getInt(0));
            abilityHidden.add(c.getString(1));
        }
        // Close the cursor
        c.close();

        // Last step for abilities: grab info from the Abilities table
        ArrayList<String> abilityNames = new ArrayList<>();
        ArrayList<String> abilityDescriptions = new ArrayList<>();
        columns = new String[]{"Name","Description"};
        for (int i = 0; i < abilityIDs.size(); ++i) {
            c = pRead.query("Abilities", columns, "ID=" + String.valueOf(abilityIDs.get(i)), null, null, null, null);
            while(c.moveToNext())
            {
                abilityNames.add(c.getString(0));
                abilityDescriptions.add(c.getString(1));
            }
            c.close();
        }
        pRead.close();

        // Populate the abilities text views
        String buffer;
        TextView abilityTextView1 = (TextView) getView().findViewById(R.id.textViewAbility1);
        TextView abilityDescTextView1 = (TextView) getView().findViewById(R.id.textViewAbilityDesc1);
        TextView abilityTextView2 = (TextView) getView().findViewById(R.id.textViewAbility2);
        TextView abilityDescTextView2 = (TextView) getView().findViewById(R.id.textViewAbilityDesc2);
        TextView abilityTextView3 = (TextView) getView().findViewById(R.id.textViewAbility3);
        TextView abilityDescTextView3 = (TextView) getView().findViewById(R.id.textViewAbilityDesc3);
        if (abilityNames.size() > 0) {
            buffer = abilityNames.get(0);
            if (abilityHidden.get(0).equals("true"))
                buffer = buffer + "(Hidden)";
            abilityTextView1.setText(buffer);
            abilityDescTextView1.setText(abilityDescriptions.get(0));
        } else {
            abilityTextView1.setVisibility(View.INVISIBLE);
            abilityDescTextView1.setVisibility(View.INVISIBLE);
        }
        if (abilityNames.size() > 1) {
            buffer = abilityNames.get(1);
            if (abilityHidden.get(1).equals("true"))
                buffer = buffer + "(Hidden)";
            abilityTextView2.setText(buffer);
            abilityDescTextView2.setText(abilityDescriptions.get(1));
        } else {
            abilityTextView2.setVisibility(View.GONE);
            abilityDescTextView2.setVisibility(View.GONE);
        }
        if (abilityNames.size() > 2) {
            buffer = abilityNames.get(2);
            if (abilityHidden.get(2).equals("true"))
                buffer = buffer + "(Hidden)";
            abilityTextView3.setText(buffer);
            abilityDescTextView3.setText(abilityDescriptions.get(2));
        } else {
            abilityTextView3.setVisibility(View.GONE);
            abilityDescTextView3.setVisibility(View.GONE);
        }

// Set the image of the Pokemon pulled
        GifImageView pokemonImageView = (GifImageView) getView().findViewById(R.id.imageView2);
        String pokemonName = clickedName.toLowerCase().replace(' ', '_').replace("'","").replace("-","_").replace("%","");
        if (pokemonName.contains("mega")) {
            pokemonName = pokemonName.replace("mega_", "");
            pokemonName = pokemonName.concat("_mega");
        }
        else if (pokemonName.contains("alolan")) {
            pokemonName = pokemonName.replace("alolan_","");
            pokemonName = pokemonName.concat("_alolan");
        }
        else if (pokemonName.contains("primal")) {
            pokemonName = pokemonName.replace("primal_","");
            pokemonName = pokemonName.concat("_primal");
        } else if (pokemonName.contains("pumpkaboo") || pokemonName.contains("gourgeist")) {
            pokemonName = pokemonName.replace("_super_size","").replace("_large_size","").replace("_average_size","").replace("_small_size","");
        }
        int imageResID = getResources().getIdentifier(pokemonName,"drawable",getActivity().getPackageName());
        pokemonImageView.setImageResource(imageResID);

// Set the textviews of the Pokemon's name and dex number
        TextView nameTextView = (TextView) getView().findViewById(R.id.textViewName);
        TextView dexTextView = (TextView) getView().findViewById(R.id.textViewDex);
        String pokemonDex = String.format("Dex Number: %03d", Dex);
        if (Name.length() >= 15) {
            nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
        }
        nameTextView.setText(Name);
        dexTextView.setText(pokemonDex);

// Now for the type images
        ImageView type1ImageView = (ImageView) getView().findViewById(R.id.imageViewType1);
        int Type1ResID = getResources().getIdentifier(Type1.toLowerCase(),"drawable",getActivity().getPackageName());
        type1ImageView.setImageResource(Type1ResID);

// For the second, this can be null so have to be a little careful
        ImageView type2ImageView = (ImageView) getView().findViewById(R.id.imageViewType2);
        if (!Type2.isEmpty())
        {
            int Type2ResID = getResources().getIdentifier(Type2.toLowerCase(),"drawable",getActivity().getPackageName());
            type2ImageView.setImageResource(Type2ResID);
        }
        else {
            type2ImageView.setVisibility(View.INVISIBLE);
        }
        CreateBarChart();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void CreateBarChart() {
        // *Deep breath* the stat bar...
        BarChart barChart = (BarChart) getView().findViewById(R.id.barChartStats);
        ArrayList<BarEntry> statsEntries = new ArrayList<>();
        statsEntries.add(new BarEntry(0f,HP));
        statsEntries.add(new BarEntry(1f,Attack));
        statsEntries.add(new BarEntry(2f,Defense));
        statsEntries.add(new BarEntry(3f,SpAtk));
        statsEntries.add(new BarEntry(4f,SpDef));
        statsEntries.add(new BarEntry(5f,Speed));
        BarDataSet barDataSet = new BarDataSet(statsEntries,"");
        barDataSet.setValueTextSize(11f);
        barDataSet.setValueFormatter(new LargeValueFormatter());
        int[] statsColors = new int[6];
        ArrayList<Integer> stats = new ArrayList<>();
        stats.add(HP);
        stats.add(Attack);
        stats.add(Defense);
        stats.add(SpAtk);
        stats.add(SpDef);
        stats.add(Speed);

        int biggestStat = 0;

        // Set the colors according to the stat
        // These are scaled according to either the largest stat or 190
        for (int i = 0; i < stats.size(); ++i) {
            if (stats.get(i) < 15)
                statsColors[i] = getResources().getColor(R.color.holo_red_dark);
            else if (stats.get(i) < 45)
                statsColors[i] = getResources().getColor(R.color.holo_red_light);
            else if (stats.get(i) < 60)
                statsColors[i] = getResources().getColor(R.color.holo_orange_dark);
            else if (stats.get(i) < 75)
                statsColors[i] = getResources().getColor(R.color.holo_orange_light);
            else if (stats.get(i) < 100)
                statsColors[i] = getResources().getColor(R.color.holo_green_light);
            else if (stats.get(i) < 130)
                statsColors[i] = getResources().getColor(R.color.holo_green_dark);
            else if (stats.get(i) < 160)
                statsColors[i] = getResources().getColor(R.color.holo_blue_dark);
            else if (stats.get(i) <= 190)
                statsColors[i] = getResources().getColor(R.color.holo_blue_light);
            else
                statsColors[i] = getResources().getColor(R.color.holo_blue_bright);
            if (biggestStat < stats.get(i))
                biggestStat = stats.get(i);
        }
        barDataSet.setColors(statsColors);
        barDataSet.setDrawValues(true);

        // Create the X axis values under the barchart using the IAxisValueFormatter and getFormattedValue
        final String[] statsNames = new String[] {"HP", "Attack", "Defense", "SpAtk", "SpDef", "Speed"};
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return statsNames[(int) value];
            }
        };

        // This goes into details for setting the xAxis and yAxis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(11f);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        if (biggestStat > 150)
            yAxis.setAxisMaximum(biggestStat);
        else
            yAxis.setAxisMaximum(150f);
        BarData chartData = new BarData(barDataSet);
        chartData.setBarWidth(0.9f);

        // Sets the details for the barChart itself
        barChart.setData(chartData);
        barChart.setFitBars(true);
        barChart.setDescription(null);
        barChart.setDrawBorders(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.setTouchEnabled(false);
        xAxis.setDrawLabels(true);
        barChart.invalidate();
    }
}
