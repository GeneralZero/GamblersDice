package zero.general.gamblerdice;


import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Arrays;

import zero.general.gamblerdice.model.GamblerDie;

import static android.content.ContentValues.TAG;

/**
 * Created by generalzero on 8/15/2017.
 */

public class GamblerFragment extends Fragment {
    int dice_type_index = 0;
    int dice_count_index = 0;

    String[] stringArrayCount = new String[]{"1","2","3","4","5","6","7","8","9","10"};
    String[] stringArrayType = new String[]{"2","3","4","6","8","10","12","20","100"};

    GamblerDie[] diceArray;

    View rootView;

    public GamblerFragment() {
        // Required empty public constructor
        diceArray = new GamblerDie[stringArrayType.length-1];

        for (int i = 0; i < stringArrayType.length-1; i++) {
            int size = Integer.parseInt(stringArrayType[i]);
            diceArray[i] = new GamblerDie(size);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gambler, container, false);

        //Set Roll Button Callback
        final Button rollButton = (Button) rootView.findViewById(R.id.gam_roll);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Rolling " + stringArrayCount[dice_count_index] + "d" + stringArrayType[dice_type_index]);

                int end = Integer.parseInt(stringArrayCount[dice_count_index]);

                int[] rolled = new int[end];

                for (int i = 0; i < end; i++) {
                    rolled[i] = diceArray[dice_type_index].roll();
                }

                String returnString = Arrays.toString(rolled);

                Log.v("Results",returnString);
                returnDiceResults(returnString);

                double[] probs = diceArray[dice_type_index].weights();
                Log.v("Probabilities", Arrays.toString(probs));

                //Update Graph
                updateGraph(probs);
            }
        });

        updateDiceString();

        //Set NumberPicker typeDie Callback
        NumberPicker typeDie = (NumberPicker)rootView.findViewById(R.id.gam_dice_type);

        typeDie.setDisplayedValues(stringArrayType);
        typeDie.setMaxValue(0);
        typeDie.setMaxValue(stringArrayType.length -1);

        typeDie.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dice_type_index = newVal;

                updateDiceString();
            }
        });

        //Set NumberPicker typeDie Callback
        NumberPicker countDie = (NumberPicker)rootView.findViewById(R.id.gam_dice_count);

        countDie.setDisplayedValues(stringArrayCount);
        countDie.setMaxValue(0);
        countDie.setMaxValue(stringArrayCount.length -1);

        countDie.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dice_count_index = newVal;

                updateDiceString();
            }
        });

        //Set
        setupGraph();

        // Inflate the layout for this fragment
        return rootView;
    }

    public void updateDiceString(){
        TextView diceText = (TextView) rootView.findViewById(R.id.gam_result);

        diceText.setText(stringArrayCount[dice_count_index]+ "d" + stringArrayType[dice_type_index]);
    }

    public void returnDiceResults(String results){
        TextView diceText = (TextView) rootView.findViewById(R.id.gam_result);

        diceText.setText(results);
    }

    public void setupGraph(){
        HorizontalBarChart chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        chart.setMaxVisibleValueCount(20);

        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);


        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(1f);

        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        YAxis yr = chart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        updateGraph(diceArray[dice_type_index].weights());

    }

    public void updateGraph(double[] probs){
        HorizontalBarChart chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < probs.length; i++) {
            yVals1.add(new BarEntry(i+1, ((float)probs[i]*100)));
        }

        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)chart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Dice Probs");

            set1.setDrawIcons(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            chart.setData(data);
        }

        chart.setFitBars(true);
        chart.animateY(500);
    }
}