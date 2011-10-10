package eu.vranckaert.worktime.dao.generic;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import eu.vranckaert.worktime.dao.utils.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * generic DAO implementation to retrieve a DAO object and retrieving certain data from the DB.
 * In order to extend from this class you should implement a default constructor which calls the
 * generic dao's constructor <b>GenericDaoImpl(java.lang.Class<T> clazz)</b>
 *
 * @author Dirk Vranckaert
 */
public abstract class GenericDaoImpl<T, ID> implements GenericDao<T, ID> {
    /**
     * Logging
     */
    private static final String LOG_TAG = GenericDaoImpl.class.getSimpleName();

    /**
     * The doa to access all of your entities.
     */
    public Dao<T, ID> dao;

    private Context context;

    /**
     * This constructor should always be called in order to have a DAO!
     * @param clazz The entity-class for which the DAO should be created!
     */
    public GenericDaoImpl(final java.lang.Class<T> clazz, final Context context) {
        Log.d(LOG_TAG, "Creating DAO for " + clazz.getSimpleName() + " from " + getClass().getSimpleName());

        OrmLiteSqliteOpenHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            dao = helper.getDao(clazz);
        } catch (SQLException e) {
            throw new RuntimeException("Could not instantiate a DAO for class " + clazz.getName(), e);
        }
        //helper.close();
        //OpenHelperManager.releaseHelper();
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    /**
     * Handles the throwing of fatal exceptions during basic SQL commands.
     * @param e The exception.
     */
    protected void throwFatalException(SQLException e) {
        String message = "An unknown SQL exception occured while executing a basic SQL command!";
        Log.e(LOG_TAG, message, e);
        throw new RuntimeException(message, e);
    }

    /**
     * @Override
     */
    public T findById(ID id) {
        T result = null;
        try {
            result = dao.queryForId(id);
        } catch (SQLException e) {
            throwFatalException(e);
        }
        return result;
    }

    /**
     * @Override
     */
    public List<T> findAll() {
        List<T> results = null;
        try {
            results = dao.queryForAll();
        } catch (SQLException e) {
            throwFatalException(e);
        }
        return results;
    }

    /**
     * @Override
     */
    public T save(T entity) {
        try {
            dao.create(entity);
        } catch (SQLException e) {
            throwFatalException(e);
        }
        return entity;
    }

    /**
     * @Override
     */
    public T update(T entity) {
        try {
            dao.update(entity);
        } catch (SQLException e) {
            throwFatalException(e);
        }
        return entity;
    }

    /**
     * @Override
     */
    public void delete(T entity) {
        int result = 0;
        try {
            result = dao.delete(entity);
        } catch (SQLException e) {
            throwFatalException(e);
        }
        Log.d(LOG_TAG, result + " records are deleted!");
    }

    /**
     * {@inheritDoc}
     */
    public int refresh(T entity) {
        int result = -1;
        try {
             result = dao.refresh(entity);
        } catch (SQLException e) {
            throwFatalException(e);
        }
        return result;
    }
}