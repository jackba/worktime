/*
 *  Copyright 2011 Dirk Vranckaert
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
package eu.vranckaert.worktime.guice;

import eu.vranckaert.worktime.R;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import roboguice.application.GuiceApplication;

import java.util.List;

@ReportsCrashes(formKey = "",
                mailTo = "",
                customReportContent = {},
                mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.acra_crash_report_send)
public class Application extends GuiceApplication {
    @Override
    protected void addApplicationModules(List<com.google.inject.Module> modules) {
        modules.add(new Module());
    }

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
}
