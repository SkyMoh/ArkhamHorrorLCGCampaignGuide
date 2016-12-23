package com.whitdan.arkhamhorrorlcgcampaignguide.finishscenario;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.whitdan.arkhamhorrorlcgcampaignguide.ContinueOnClickListener;
import com.whitdan.arkhamhorrorlcgcampaignguide.GlobalVariables;
import com.whitdan.arkhamhorrorlcgcampaignguide.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * Created by danie on 16/12/2016.
 */

public class FinishResolutionFragment extends Fragment {

    GlobalVariables globalVariables;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_finish_resolution, container, false);
        globalVariables  = (GlobalVariables) getActivity().getApplication();
        ArrayAdapter<CharSequence> adapter;

        // Setup resolution choice spinner
        if ((globalVariables.getCurrentScenario() == 1) || (globalVariables.getCurrentScenario() == 3)) {
            // Create an ArrayAdapter using the investigators string array and a default spinner layout
            adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                    R.array.resolutions_three, android.R.layout.simple_spinner_item);
        } else {
            adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                    R.array.resolutions_two, android.R.layout.simple_spinner_item);
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Setup resolution spinner and apply the adapter
        Spinner resolutionSpinner = (Spinner) v.findViewById(R.id.resolution_selection);
        resolutionSpinner.setAdapter(adapter);
        resolutionSpinner.setOnItemSelectedListener(new ResolutionSpinnerListener());

        // Setup victory display spinner and apply an adapter
        Spinner victorySpinner = (Spinner) v.findViewById(R.id.victory_display);
        List<Integer> victoryArray = new ArrayList<Integer>();
        for (int i = 0; 10 > i; i++) {
            victoryArray.add(i);
        }
        ArrayAdapter<Integer> victoryAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, victoryArray);
        victoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        victorySpinner.setAdapter(victoryAdapter);
        victorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                globalVariables.setVictoryDisplay(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                globalVariables.setVictoryDisplay(0);
            }
        });

        // Setup additional options for second resolution
        if(globalVariables.getCurrentCampaign()==1 && globalVariables.getCurrentScenario()==2){
            scenarioTwo(v);
        }

        // Set continue button click listener
        TextView button = (TextView) v.findViewById(R.id.continue_button);
        button.setOnClickListener(new ContinueOnClickListener(globalVariables, this.getActivity()));

        return v;
    }

    // OnItemSelectedListener for the Resolution Spinner
    private class ResolutionSpinnerListener extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            TextView resolutionText = (TextView) view.getRootView().findViewById(R.id.resolution_text);
            if(globalVariables.getCurrentScenario()==1){
                switch (pos) {
                    // No resolution
                    case 0:
                        resolutionText.setText(R.string.gathering_no_resolution);
                        globalVariables.setResolution(0);
                        break;
                    // Resolution one
                    case 1:
                        resolutionText.setText(R.string.gathering_resolution_one);
                        globalVariables.setResolution(1);
                        break;
                    // Resolution two
                    case 2:
                        resolutionText.setText(R.string.gathering_resolution_two);
                        globalVariables.setResolution(2);
                        break;
                    // Resolution three
                    case 3:
                        resolutionText.setText(R.string.gathering_resolution_three);
                        globalVariables.setResolution(3);
                        break;
                }
            }
            else if(globalVariables.getCurrentScenario()==2){
                switch(pos){
                    // No resolution
                    case 0:
                        resolutionText.setText(R.string.midnight_resolution_one);
                        globalVariables.setResolution(0);
                        break;
                    case 1:
                        resolutionText.setText(R.string.midnight_resolution_one);
                        globalVariables.setResolution(1);
                        break;
                    case 2:
                        resolutionText.setText(R.string.midnight_resolution_two);
                        globalVariables.setResolution(2);
                        break;
                }
            }
            else if(globalVariables.getCurrentScenario()==3){
                switch(pos){
                    // No resolution
                    case 0:
                        resolutionText.setText(R.string.devourer_no_resolution);
                        globalVariables.setResolution(0);
                        break;
                    // Resolution one
                    case 1:
                        resolutionText.setText(R.string.devourer_resolution_one);
                        globalVariables.setResolution(1);
                        break;
                    // Resolution two
                    case 2:
                        resolutionText.setText(R.string.devourer_resolution_two);
                        globalVariables.setResolution(2);
                        break;
                    // Resolution three
                    case 3:
                        resolutionText.setText(R.string.devourer_resolution_three);
                        globalVariables.setResolution(3);
                        break;
                }
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void scenarioTwo(View v){
        LinearLayout cultists = (LinearLayout) v.findViewById(R.id.cultists_interrogated);
        cultists.setVisibility(VISIBLE);
        if(globalVariables.getGhoulPriestAlive() == 1){
            CheckBox ghoulPriest = (CheckBox) v.findViewById(R.id.ghoul_priest_killed);
            ghoulPriest.setVisibility(VISIBLE);
        }
    }
}