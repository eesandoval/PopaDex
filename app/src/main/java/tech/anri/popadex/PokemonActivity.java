package tech.anri.popadex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class PokemonActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Auto generated actions
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Grab the intent from the MainActivity
        String clickedName = this.getIntent().getStringExtra("Name");

        // Setup the database for querying
        Pokemon p = new Pokemon(this);
        SQLiteDatabase pRead = p.getReadableDatabase();

        // Query the database into a cursor c
        String[] columns = new String[]{"Name", "HP", "Attack", "Defense", "SpAtk", "SpDef", "Speed", "Type1", "Type2", "Dex", "ID"};
        Cursor c = pRead.query("Pokemon",columns,"Name='" + clickedName + "'",null,null,null,null);

        // Grab what we pulled from the DB by advancing the cursor once
        c.moveToNext();

        // Store it into variables so we can close the cursor immediately
        String Name = c.getString(0);
        String Type1 = c.getString(7);
        String Type2 = c.getString(8);
        int HP = c.getInt(1);
        int Attack = c.getInt(2);
        int Defense = c.getInt(3);
        int SpAtk = c.getInt(4);
        int SpDef = c.getInt(5);
        int Speed = c.getInt(6);
        int Dex = c.getInt(9);
        int pokemonID = c.getInt(10);

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
        TextView abilityTextView1 = (TextView) findViewById(R.id.textViewAbility1);
        TextView abilityDescTextView1 = (TextView) findViewById(R.id.textViewAbilityDesc1);
        TextView abilityTextView2 = (TextView) findViewById(R.id.textViewAbility2);
        TextView abilityDescTextView2 = (TextView) findViewById(R.id.textViewAbilityDesc2);
        TextView abilityTextView3 = (TextView) findViewById(R.id.textViewAbility3);
        TextView abilityDescTextView3 = (TextView) findViewById(R.id.textViewAbilityDesc3);
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
            abilityTextView2.setVisibility(View.INVISIBLE);
            abilityDescTextView2.setVisibility(View.INVISIBLE);
        }
        if (abilityNames.size() > 2) {
            buffer = abilityNames.get(2);
            if (abilityHidden.get(2).equals("true"))
                buffer = buffer + "(Hidden)";
            abilityTextView3.setText(buffer);
            abilityDescTextView3.setText(abilityDescriptions.get(2));
        } else {
            abilityTextView3.setVisibility(View.INVISIBLE);
            abilityDescTextView3.setVisibility(View.INVISIBLE);
        }

        // Set the image of the Pokemon pulled
        ImageView pokemonImageView = (ImageView) findViewById(R.id.imageView2);
        String imageName = String.format("pokemon%03d", Dex);
        int imageResID = getResources().getIdentifier(imageName,"drawable",getPackageName());
        pokemonImageView.setImageResource(imageResID);

        // Set the textviews of the Pokemon's name and dex number
        TextView nameTextView = (TextView) findViewById(R.id.textViewName);
        TextView dexTextView = (TextView) findViewById(R.id.textViewDex);
        String pokemonDex = String.format("Dex Number: %03d", Dex);
        nameTextView.setText(Name);
        dexTextView.setText(pokemonDex);

        // *Deep breath* the stat bar...
        BarChart barChart = (BarChart) findViewById(R.id.barChartStats);
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

        for (int i = 0; i < stats.size(); ++i) {
            if (stats.get(i) < 50)
                statsColors[i] = getResources().getColor(R.color.holo_red_light);
            else if (stats.get(i) < 75)
                statsColors[i] = getResources().getColor(R.color.holo_orange_dark);
            else if (stats.get(i) < 125)
                statsColors[i] = getResources().getColor(R.color.holo_green_dark);
            else
                statsColors[i] = getResources().getColor(R.color.holo_blue_bright);
            if (biggestStat < stats.get(i))
                biggestStat = stats.get(i);
        }
        barDataSet.setColors(statsColors);
        barDataSet.setDrawValues(true);

        final String[] statsNames = new String[] {"HP", "Attack", "Defense", "SpAtk", "SpDef", "Speed"};
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return statsNames[(int) value];
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(11f);
        xAxis.setDrawLabels(true);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        if (biggestStat > 150)
            yAxis.setAxisMaximum(biggestStat);
        else
            yAxis.setAxisMaximum(150f);
        BarData chartData = new BarData(barDataSet);
        chartData.setBarWidth(0.9f);

        barChart.setData(chartData);
        barChart.setFitBars(true);
        barChart.setDescription(null);
        barChart.setDrawBorders(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.invalidate();

        // Now for the type images
        ImageView type1ImageView = (ImageView) findViewById(R.id.imageViewType1);
        int Type1ResID = getResources().getIdentifier(Type1.toLowerCase(),"drawable",getPackageName());
        type1ImageView.setImageResource(Type1ResID);

        // For the second, this can be null so have to be a little careful
        ImageView type2ImageView = (ImageView) findViewById(R.id.imageViewType2);
        if (!Type2.isEmpty())
        {
            int Type2ResID = getResources().getIdentifier(Type2.toLowerCase(),"drawable",getPackageName());
            type2ImageView.setImageResource(Type2ResID);
        }
        else
        {
            type2ImageView.setVisibility(View.INVISIBLE);
        }

        // Perform the rest of the operations as defined
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pokemon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
