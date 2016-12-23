package com.whitdan.arkhamhorrorlcgcampaignguide;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.CheckBox;

import com.whitdan.arkhamhorrorlcgcampaignguide.data.ArkhamContract;
import com.whitdan.arkhamhorrorlcgcampaignguide.data.ArkhamDbHelper;
import com.whitdan.arkhamhorrorlcgcampaignguide.finishscenario.FinishScenarioActivity;
import com.whitdan.arkhamhorrorlcgcampaignguide.scenariosetup.ScenarioSetupActivity;
import com.whitdan.arkhamhorrorlcgcampaignguide.selectcampaign.SelectCampaignActivity;


/**
 * Created by danie on 19/12/2016.
 */

public class ContinueOnClickListener implements View.OnClickListener {

    GlobalVariables globalVariables;
    Context context;

    public ContinueOnClickListener(GlobalVariables mGlobalVariables, Context mContext) {
        globalVariables = mGlobalVariables;
        context = mContext;

    }

    public void onClick(View v) {
        // Continue the scenario and advance to scenario finish
        if (globalVariables.getScenarioStage() == 1) {
            // Set XP from tempXP (by subtracting) and reset tempXP
            for (int i = 0; i < globalVariables.investigators.size(); i++) {
                int XPSpent = globalVariables.investigators.get(i).getTempXP();
                globalVariables.investigators.get(i).changeXP(-XPSpent);
                globalVariables.investigators.get(i).setTempXP(0);
            }
            // Go to scenario finish
            globalVariables.setScenarioStage(2);
            Intent intent = new Intent(context, FinishScenarioActivity.class);
            context.startActivity(intent);
        }

        // Apply the scenario resolution then advance to the next scenario and scenario setup
        else if (globalVariables.getScenarioStage() == 2) {
            // Apply the scenario resolution
            if (globalVariables.getCurrentCampaign() == 1) {
                nightResolutions(globalVariables, v);
            }
            // Apply defeats
            for (int i = 0; i < globalVariables.investigators.size(); i++) {
                int status = globalVariables.investigators.get(i).getTempStatus();
                if (status == 4) {
                    globalVariables.investigators.get(i).changeDamage(1);
                } else if (status == 5) {
                    globalVariables.investigators.get(i).changeHorror(1);
                }
                globalVariables.investigators.get(i).setTempStatus(1);
            }
            // Increment current scenario
            int nextScenario = globalVariables.getCurrentScenario() + 1;
            globalVariables.setCurrentScenario(nextScenario);

            // Save the campaign
            saveCampaign();

            // Go to scenario finish or end and delete campaign
            if (globalVariables.getCurrentScenario() > 3) {
                // Delete campaign
                ArkhamDbHelper dbHelper = new ArkhamDbHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String campaignSelection = ArkhamContract.CampaignEntry._ID + " = ?";
                String nightSelection = ArkhamContract.NightEntry.PARENT_ID + " = ?";
                String investigatorSelection = ArkhamContract.InvestigatorEntry.PARENT_ID + " = ?";
                // Where id = current campaign id
                String[] selectionArgs = {Long.toString(globalVariables.getCampaignID())};
                // Execute deletions
                db.delete(ArkhamContract.CampaignEntry.TABLE_NAME, campaignSelection, selectionArgs);
                db.delete(ArkhamContract.NightEntry.TABLE_NAME, nightSelection, selectionArgs);
                db.delete(ArkhamContract.InvestigatorEntry.TABLE_NAME, investigatorSelection, selectionArgs);

                Intent intent = new Intent(context, SelectCampaignActivity.class);
                context.startActivity(intent);
            } else {
                globalVariables.setScenarioStage(1);
                Intent intent = new Intent(context, ScenarioSetupActivity.class);
                context.startActivity(intent);
            }
        }
    }

    // Contains all the Night of the Zealot resolutions
    private void nightResolutions(GlobalVariables globalVariables, View v) {
        int leadInvestigator = globalVariables.getLeadInvestigator();

        /*
         The Gathering (scenario one) resolutions
          */
        if (globalVariables.getCurrentScenario() == 1) {
            switch (globalVariables.getResolution()) {
                // No resolution
                case 0:
                    globalVariables.setHouseStanding(1);
                    globalVariables.setGhoulPriestAlive(1);
                    globalVariables.setLitaStatus(1);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        globalVariables.investigators.get(i).changeXP(globalVariables.getVictoryDisplay() + 2);
                    }
                    break;
                // Resolution one
                case 1:
                    globalVariables.setHouseStanding(0);
                    globalVariables.setGhoulPriestAlive(0);
                    globalVariables.setLitaStatus(1);
                    globalVariables.investigators.get(leadInvestigator).changeHorror(1);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        globalVariables.investigators.get(i).changeXP(globalVariables.getVictoryDisplay() + 2);
                    }
                    break;
                // Resolution two
                case 2:
                    globalVariables.setHouseStanding(1);
                    globalVariables.setGhoulPriestAlive(0);
                    globalVariables.setLitaStatus(0);
                    globalVariables.investigators.get(leadInvestigator).changeXP(1);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        globalVariables.investigators.get(i).changeXP(globalVariables.getVictoryDisplay() + 2);
                    }
                    break;
                // Resolution three
                case 3:
                    globalVariables.setHouseStanding(1);
                    globalVariables.setGhoulPriestAlive(1);
                    globalVariables.setLitaStatus(2);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        if (globalVariables.investigators.get(i).getTempStatus() != 3) {
                            //TODO: Kill remaining investigators (globalVariables.investigators.get(i).setStatus(2);)
                        }
                    }
                    break;
            }
        }

        /*
         The Midnight Masks (scenario 2) resolutions
          */
        else if (globalVariables.getCurrentScenario() == 2) {

            View parent = v.getRootView();

            // Check if Ghoul Priest is still alive
            CheckBox ghoulPriest = (CheckBox) parent.findViewById(R.id.ghoul_priest_killed);
            if (ghoulPriest.isChecked()) {
                globalVariables.setGhoulPriestAlive(0);
            }

            // Check if each cultist has been interrogated
            CheckBox drew = (CheckBox) parent.findViewById(R.id.drew_interrogated);
            if (drew.isChecked()) {
                globalVariables.incrementCultistsInterrogated();
                globalVariables.setDrewInterrogated(1);
            } else {
                globalVariables.setDrewInterrogated(0);
            }
            CheckBox herman = (CheckBox) parent.findViewById(R.id.herman_interrogated);
            if (herman.isChecked()) {
                globalVariables.incrementCultistsInterrogated();
                globalVariables.setHermanInterrogated(1);
            } else {
                globalVariables.setHermanInterrogated(0);
            }
            CheckBox peter = (CheckBox) parent.findViewById(R.id.peter_interrogated);
            if (peter.isChecked()) {
                globalVariables.incrementCultistsInterrogated();
                globalVariables.setPeterInterrogated(1);
            } else {
                globalVariables.setPeterInterrogated(0);
            }
            CheckBox victoria = (CheckBox) parent.findViewById(R.id.victoria_interrogated);
            if (victoria.isChecked()) {
                globalVariables.incrementCultistsInterrogated();
                globalVariables.setVictoriaInterrogated(1);
            } else {
                globalVariables.setVictoriaInterrogated(0);
            }
            CheckBox ruth = (CheckBox) parent.findViewById(R.id.ruth_interrogated);
            if (ruth.isChecked()) {
                globalVariables.incrementCultistsInterrogated();
                globalVariables.setRuthInterrogated(1);
            } else {
                globalVariables.setRuthInterrogated(0);
            }
            CheckBox masked = (CheckBox) parent.findViewById(R.id.masked_hunter_interrogated);
            if (masked.isChecked()) {
                globalVariables.incrementCultistsInterrogated();
                globalVariables.setMaskedInterrogated(1);
            } else {
                globalVariables.setMaskedInterrogated(0);
            }

            switch (globalVariables.getResolution()) {
                case 0:
                    globalVariables.setMidnightStatus(0);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        globalVariables.investigators.get(i).changeXP(globalVariables.getVictoryDisplay());
                    }
                    break;
                case 1:
                    globalVariables.setMidnightStatus(0);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        globalVariables.investigators.get(i).changeXP(globalVariables.getVictoryDisplay());
                    }
                    break;
                case 2:
                    globalVariables.setMidnightStatus(1);
                    for (int i = 0; i < globalVariables.investigators.size(); i++) {
                        globalVariables.investigators.get(i).changeXP(globalVariables.getVictoryDisplay());
                    }
                    break;
            }
        }
    }

    // Saves the campaign, including all relevant variables
    private void saveCampaign() {
        ArkhamDbHelper mDbHelper = new ArkhamDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Update scenario number
        ContentValues campaignValues = new ContentValues();
        campaignValues.put(ArkhamContract.CampaignEntry.COLUMN_CURRENT_SCENARIO, globalVariables.getCurrentScenario());

        String campaignSelection = ArkhamContract.CampaignEntry._ID + " LIKE ?";
        String[] campaignSelectionArgs = {Long.toString(globalVariables.getCampaignID())};
        int campaignUpdate = db.update(
                ArkhamContract.CampaignEntry.TABLE_NAME,
                campaignValues,
                campaignSelection,
                campaignSelectionArgs);

        // Update all night variables
        if (globalVariables.getCurrentCampaign() == 1) {
            ContentValues nightValues = new ContentValues();
            nightValues.put(ArkhamContract.NightEntry.COLUMN_HOUSE_STANDING, globalVariables.getHouseStanding());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_GHOUL_PRIEST, globalVariables.getGhoulPriestAlive());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_LITA_STATUS, globalVariables.getLitaStatus());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_CULTISTS_INTERROGATED, globalVariables
                    .getCultistsInterrogated());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_MIDNIGHT_STATUS, globalVariables.getMidnightStatus());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_DREW_INTERROGATED, globalVariables.getDrewInterrogated());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_HERMAN_INTERROGATED, globalVariables
                    .getHermanInterrogated());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_PETER_INTERROGATED, globalVariables.getPeterInterrogated
                    ());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_RUTH_INTERROGATED, globalVariables.getRuthInterrogated());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_VICTORIA_INTERROGATED, globalVariables
                    .getVictoriaInterrogated());
            nightValues.put(ArkhamContract.NightEntry.COLUMN_MASKED_INTERROGATED, globalVariables
                    .getMaskedInterrogated());

            String nightSelection = ArkhamContract.NightEntry.PARENT_ID + " LIKE ?";
            String[] nightSelectionArgs = {Long.toString(globalVariables.getCampaignID())};
            int nightUpdate = db.update(
                    ArkhamContract.NightEntry.TABLE_NAME,
                    nightValues,
                    nightSelection,
                    nightSelectionArgs);
        }

        // Update investigator entries
        for (int i = 0; i < globalVariables.investigators.size(); i++) {
            ContentValues investigatorValues = new ContentValues();
            investigatorValues.put(ArkhamContract.InvestigatorEntry.COLUMN_INVESTIGATOR_NAME, globalVariables
                    .investigators.get(i).getName());
            investigatorValues.put(ArkhamContract.InvestigatorEntry.COLUMN_INVESTIGATOR_STATUS, globalVariables
                    .investigators.get(i).getStatus());
            investigatorValues.put(ArkhamContract.InvestigatorEntry.COLUMN_INVESTIGATOR_DAMAGE, globalVariables
                    .investigators.get(i).getDamage());
            investigatorValues.put(ArkhamContract.InvestigatorEntry.COLUMN_INVESTIGATOR_HORROR, globalVariables
                    .investigators.get(i).getHorror());
            investigatorValues.put(ArkhamContract.InvestigatorEntry.COLUMN_INVESTIGATOR_XP, globalVariables
                    .investigators.get(i).getAvailableXP());

            String investigatorSelection = ArkhamContract.InvestigatorEntry.PARENT_ID + " LIKE ?" + " AND " +
                    ArkhamContract.InvestigatorEntry.INVESTIGATOR_ID + " LIKE ?";
            String[] investigatorSelectionArgs = {Long.toString(globalVariables.getCampaignID()), Integer.toString(i)};
            int investigatorUpdate = db.update(
                    ArkhamContract.InvestigatorEntry.TABLE_NAME,
                    investigatorValues,
                    investigatorSelection,
                    investigatorSelectionArgs);
        }
    }
}