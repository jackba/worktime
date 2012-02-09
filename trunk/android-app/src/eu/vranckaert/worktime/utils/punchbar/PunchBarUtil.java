/*
 *  Copyright 2012 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.utils.punchbar;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.activities.HomeActivity;
import eu.vranckaert.worktime.activities.widget.StartTimeRegistrationActivity;
import eu.vranckaert.worktime.activities.widget.StopTimeRegistrationActivity;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.utils.preferences.Preferences;

/**
 * User: DIRK VRANCKAERT
 * Date: 7/02/12
 * Time: 11:44
 */
public class PunchBarUtil {
    /**
     * Configure the punch bar to be shown correctly.
     * @param ctx The activity from which the bar should be created.
     * @param timeRegistrationService A reference to the {@link TimeRegistrationService}.
     * @param taskService A reference to the {@link TaskService}.
     * @param projectService A reference to the {@link ProjectService}.
     */
    public static void configurePunchBar(Activity ctx,
            TimeRegistrationService timeRegistrationService, TaskService taskService, ProjectService projectService) {
        View bar = ctx.findViewById(R.id.punch_bar_container);
        if (Preferences.getTimeRegistrationPunchBarEnabledFromHomeScreen(ctx) &&
                (ctx.getClass().equals(HomeActivity.class) || Preferences.getTimeRegistrationPunchBarEnabledOnAllScreens(ctx))) {
            bar.setVisibility(View.VISIBLE);
        } else {
            bar.setVisibility(View.GONE);
            return;
        }

        TimeRegistration lastTimeRegistration = timeRegistrationService.getLatestTimeRegistration();
        taskService.refresh(lastTimeRegistration.getTask());
        projectService.refresh(lastTimeRegistration.getTask().getProject());
        TextView footerText = (TextView) ctx.findViewById(R.id.punch_bar_text);
        ImageButton actionButton = (ImageButton) ctx.findViewById(R.id.punchBarActionId);

        if (lastTimeRegistration != null && lastTimeRegistration.isOngoingTimeRegistration()) {
            footerText.setText(
                    lastTimeRegistration.getTask().getProject().getName() +
                            " " + ctx.getString(R.string.dash) + " " +
                            lastTimeRegistration.getTask().getName()
            );
            actionButton.setImageResource(R.drawable.ic_stop);
        } else {
            footerText.setText(R.string.home_comp_start_stop_time_registration_no_ongoing);
            actionButton.setImageResource(R.drawable.ic_play);
        }
    }

    /**
     * Handles the click on a punch in/out in the punch-bar.
     * @param ctx The activity from which the punch in/out action is invoked.
     * @param timeRegistrationService A reference to the {@link TimeRegistrationService}.
     */
    public static void onPunchButtonClick(Activity ctx, TimeRegistrationService timeRegistrationService) {
        TimeRegistration lastTimeRegistration = timeRegistrationService.getLatestTimeRegistration();
        if (lastTimeRegistration != null && lastTimeRegistration.isOngoingTimeRegistration()) {
            Intent intent = new Intent(ctx, StopTimeRegistrationActivity.class);
            ctx.startActivityForResult(intent, Constants.IntentRequestCodes.PUNCH_BAR_END_TIME_REGISTRATION);
        } else {
            Intent intent = new Intent(ctx, StartTimeRegistrationActivity.class);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION_START_ASK_FOR_PROJECT, true);
            ctx.startActivityForResult(intent, Constants.IntentRequestCodes.PUNCH_BAR_START_TIME_REGISTRATION);
        }
    }
}