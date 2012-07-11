/*
 * Copyright 2012 Dirk Vranckaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.service.impl;

import android.content.Context;
import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.WidgetConfigurationDao;
import eu.vranckaert.worktime.dao.impl.ProjectDaoImpl;
import eu.vranckaert.worktime.dao.impl.TaskDaoImpl;
import eu.vranckaert.worktime.dao.impl.TimeRegistrationDaoImpl;
import eu.vranckaert.worktime.dao.impl.WidgetConfigurationDaoImpl;
import eu.vranckaert.worktime.exceptions.AtLeastOneProjectRequiredException;
import eu.vranckaert.worktime.exceptions.ProjectStillInUseException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.WidgetConfiguration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.utils.context.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 06/02/11
 * Time: 04:20
 */
public class ProjectServiceImpl implements ProjectService {
    private static final String LOG_TAG = ProjectServiceImpl.class.getSimpleName();

    @Inject
    private ProjectDao dao;

    @Inject
    private Context ctx;

    @Inject
    private TimeRegistrationDao timeRegistrationDao;

    @Inject
    private TaskDao taskDao;

    @Inject
    private WidgetConfigurationDao widgetConfigurationDao;

    /**
     * Enables the use of this service outside of RoboGuice!
     * @param ctx The context to insert
     */
    public ProjectServiceImpl(Context ctx) {
        this.ctx = ctx;
        dao = new ProjectDaoImpl(ctx);
        timeRegistrationDao = new TimeRegistrationDaoImpl(ctx);
        taskDao = new TaskDaoImpl(ctx);
        widgetConfigurationDao = new WidgetConfigurationDaoImpl(ctx);
    }

    /**
     * Default constructor required by RoboGuice!
     */
    public ProjectServiceImpl() {}

    /**
     * {@inheritDoc}
     */
    public Project save(Project project) {
        return dao.save(project);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> findAll() {
        return dao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Project project) throws AtLeastOneProjectRequiredException, ProjectStillInUseException {
        if (dao.count() > 1L) {
            int taskCount = taskDao.countTasksForProject(project);
            if (taskCount > 0) {
                throw new ProjectStillInUseException("The project is still linked with " + taskCount + " tasks! Remove them first!");
            }
            dao.delete(project);
            if (project.isDefaultValue()) {
                changeDefaultProjectUponProjectRemoval(project);
            }
            checkSelectedProjectUponProjectRemoval(project);
        } else {
            throw new AtLeastOneProjectRequiredException("At least on project is required so this project cannot be removed");
        }
    }

    /**
     * Change the default project upon removing a project which is set to be the default.
     * @param projectForRemoval The default project to be removed.
     */
    private void changeDefaultProjectUponProjectRemoval(Project projectForRemoval) {
        if (!projectForRemoval.isDefaultValue()) {
            return;
        }
        Log.d(ctx, LOG_TAG, "Trying to remove project " + projectForRemoval.getName() + " while it's a default project");

        List<Project> availableProjects = dao.findAll();
        availableProjects.remove(projectForRemoval);

        if (availableProjects.size() > 0) {
            Log.d(ctx, LOG_TAG, availableProjects.size() + " projects found to be the new default project");
            Project newDefaultProject = availableProjects.get(0);
            Log.d(ctx, LOG_TAG, "New default project is " + newDefaultProject.getName());
            newDefaultProject.setDefaultValue(true);
            dao.update(newDefaultProject);
        }
    }

    /**
     * Checks if the removed project was selected for a widget, if so the default project will be used from now on for
     * all those widgets.
     * @param project The project that is removed.
     */
    private void checkSelectedProjectUponProjectRemoval(Project project) {
        Log.d(ctx, LOG_TAG, "Checking if the project (" + project.getId() + " - " + project.getName() + ") is configured in widgets upon project removal");
        List<WidgetConfiguration> widgetConfigurations = widgetConfigurationDao.findPerProjectId(project.getId());
        Log.d(ctx, LOG_TAG, "Found " + widgetConfigurations + " widget configurations for this project.");
        for (WidgetConfiguration wc : widgetConfigurations) {
            if (wc.getProjectId().equals(project.getId())) {
                Log.d(ctx, LOG_TAG, "The project is used in widget with id " + wc.getWidgetId());
                Project newSelectedProject = dao.findDefaultProject();
                Log.d(ctx, LOG_TAG, "The new project that will be selected is " + newSelectedProject.getId() + " - " + newSelectedProject.getName());
                setSelectedProject(wc.getWidgetId(), newSelectedProject);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNameAlreadyUsed(String projectName) {
        return dao.isNameAlreadyUsed(projectName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNameAlreadyUsed(String projectName, Project excludedProject) {
        if (excludedProject.getName().equals(projectName)) {
            return false;
        }
        return dao.isNameAlreadyUsed(projectName);
    }

    /**
     * {@inheritDoc}
     */
    public Project getSelectedProject(int widgetId) {
        WidgetConfiguration wc = widgetConfigurationDao.findById(widgetId);
        if (wc == null) {
            Log.w(ctx, LOG_TAG, "No widget configuration is found for widget with id " + widgetId + ". One will be created with the default project");

            wc = new WidgetConfiguration(widgetId);
            Project defaultProject = dao.findDefaultProject();
            wc.setProjectId(defaultProject.getId());
            widgetConfigurationDao.save(wc);

            return defaultProject;
        }

        Project project = null;

        if (wc.getProjectId() != null) {
            Log.d(ctx, LOG_TAG, "Selected project id found is " + wc.getProjectId());
            project = dao.findById(wc.getProjectId());
            Log.d(ctx, LOG_TAG, "Selected project has id " + project.getId() + " and name " + project.getName());
        }

        if (project == null) {
            Log.w(ctx, LOG_TAG, "No project is found for widget with id " + widgetId + ", updating the widget configuration to use the default project!");
            project = dao.findDefaultProject();
            wc.setProjectId(project.getId());
            widgetConfigurationDao.update(wc);
            Log.w(ctx, LOG_TAG, "The default project is now used as selected project for widget " + widgetId + " and has id " + project.getId() + " and name " + project.getName());
        }

        return project;
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedProject(int widgetId, Project project) {
        WidgetConfiguration wc = widgetConfigurationDao.findById(widgetId);
        if (wc == null) {
            wc = new WidgetConfiguration(widgetId);
            widgetConfigurationDao.save(wc);
        } else {
            wc.setProjectId(project.getId());
            widgetConfigurationDao.update(wc);
        }
    }

    @Override
    public List<Integer> changeSelectedProject(Project fromProject, Project toProject) {
        List<Integer> widgetIds = new ArrayList<Integer>();

        List<WidgetConfiguration> wcs = widgetConfigurationDao.findPerProjectId(fromProject.getId());
        for (WidgetConfiguration wc : wcs) {
            wc.setProjectId(toProject.getId());
            widgetConfigurationDao.update(wc);

            widgetIds.add(wc.getWidgetId());
        }

        return widgetIds;
    }

    /**
     * {@inheritDoc}
     */
    public Project update(Project project) {
        return dao.update(project);
    }

    /**
     * {@inheritDoc}
     */
    public void refresh(Project project) {
        dao.refresh(project);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> findUnfinishedProjects() {
        return dao.findProjectsOnFinishedFlag(false);
    }

    /**
     * {@inheritDoc}
     */
    public Project changeDefaultProjectUponProjectMarkedFinished(Project projectMarkedFinished) {
        if (!projectMarkedFinished.isDefaultValue()) {
            return dao.findDefaultProject();
        }
        Log.d(ctx, LOG_TAG, "Trying to mark project " + projectMarkedFinished.getName() + " finished while it's a default project");

        List<Project> availableProjects = findUnfinishedProjects();
        availableProjects.remove(projectMarkedFinished);

        projectMarkedFinished.setDefaultValue(false);
        dao.update(projectMarkedFinished);

        if (availableProjects.size() > 0) {
            Log.d(ctx, LOG_TAG, availableProjects.size() + " projects found to be the new default project");
            Project newDefaultProject = availableProjects.get(0);
            Log.d(ctx, LOG_TAG, "New default project is " + newDefaultProject.getName());
            newDefaultProject.setDefaultValue(true);
            dao.update(newDefaultProject);
            return newDefaultProject;
        }
        return dao.findDefaultProject();
    }

    @Override
    public void removeAll() {
        dao.deleteAll();
    }

    @Override
    public void insertDefaultProjectAndTaskData() {
        dao.insertDefaultData();
    }
}
