/*
 * Copyright 2013 Dirk Vranckaert
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

package eu.vranckaert.worktime.service;

import eu.vranckaert.worktime.exceptions.backup.BackupException;
import eu.vranckaert.worktime.exceptions.network.NoNetworkConnectionException;
import eu.vranckaert.worktime.exceptions.network.WifiConnectionRequiredException;
import eu.vranckaert.worktime.exceptions.worktime.account.*;
import eu.vranckaert.worktime.exceptions.worktime.sync.SyncAlreadyBusyException;
import eu.vranckaert.worktime.exceptions.worktime.sync.SynchronizationFailedException;
import eu.vranckaert.worktime.model.SyncHistory;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.web.json.exception.GeneralWebException;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 12/12/12
 * Time: 20:04
 */
public interface AccountService {
    /**
     * Check if the user is logged in on this device or not.
     * @return True if logged in, false if not.
     */
    boolean isUserLoggedIn();

    /**
     * Log the user in using the provided email and password.
     * @param email The email of the user.
     * @param password The password of the user in plain text.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws LoginCredentialsMismatchException The credentials provided are not correct and so the user is not logged
     * in!
     */
    void login(String email, String password) throws GeneralWebException, NoNetworkConnectionException, LoginCredentialsMismatchException;

    /**
     * Register a new user-account with the provided details.
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param password The passwod of the user.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws eu.vranckaert.worktime.exceptions.worktime.account.RegisterEmailAlreadyInUseException If an account already exists for this email address.
     * @throws eu.vranckaert.worktime.exceptions.worktime.account.PasswordLengthValidationException If the password length is invalid (< 6 or > 30 characters).
     * @throws eu.vranckaert.worktime.exceptions.worktime.account.RegisterFieldRequiredException If one of the required fields is missing.
     */
    void register(String email, String firstName, String lastName, String password) throws GeneralWebException, NoNetworkConnectionException, RegisterEmailAlreadyInUseException, PasswordLengthValidationException, RegisterFieldRequiredException;

    /**
     * Change the password of the currently logged in user.
     * @param newPassword The old password for the currently logged in user.
     * @param newPassword The new password for the currently logged in user.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws LoginCredentialsMismatchException The provided original password is not correct.
     */
    void changePassword(String oldPassword, String newPassword) throws GeneralWebException, NoNetworkConnectionException, LoginCredentialsMismatchException;

    /**
     * Retrieve the user data that is stored offline. If the user data is incomplete (no firstname, lastname, registered
     * since date, logged in since date or role) this method will return null.
     * @return The {@link User} object or null.
     */
    User getOfflineUserData();

    /**
     * Loads the user data (full profile) from the database and updates it with information from the webservice.
     * @return The full {@link User} instance.
     * @throws NoNetworkConnectionException No working network connection is found.
     * @throws GeneralWebException Some kind of exception occurred during the web request.
     * @throws eu.vranckaert.worktime.exceptions.worktime.account.UserNotLoggedInException The user is not logged in, authentication failed...
     */
    User loadUserData(boolean retryWhenNotLoggedIn) throws UserNotLoggedInException, GeneralWebException, NoNetworkConnectionException;

    /**
     * Sync all application data (or at least the application data that has changed since the last synchronization, if
     * any) with the remote WorkTime server.
     * @throws UserNotLoggedInException This exception is thrown if the user is not logged in or the session key and
     * email address do not match.
     * @throws GeneralWebException This exception is thrown if the a network error occurred or an uncaught exception on
     * the remote server occurred.
     * @throws NoNetworkConnectionException This exception is thrown if no network connection is available when
     * executing the call(s) to the remote server.
     * @throws WifiConnectionRequiredException This exception is only thrown if a WiFi connection is excepted in order
     * to start the call(s) to the remote server.
     * @throws BackupException This exception is only thrown if a backup should be created before starting to sync and
     * if creating the backup failed for whatever reason.
     * @throws SyncAlreadyBusyException This exception is thrown when a local
     * {@link eu.vranckaert.worktime.model.SyncHistory} is found with a status
     * {@link eu.vranckaert.worktime.model.SyncHistoryStatus#BUSY} or when the remote server is in a blocking state
     * because for this user account a sync is already in progress. In case of the local sync-block the time-out is set
     * to 10 minutes. In case of server sync-block the timeout is set to 5 minutes.
     * @throws SynchronizationFailedException This exception is throw if the synchronization failed for some reason on
     * the remote server.
     */
    void sync(boolean retryWhenNotLoggedIn) throws UserNotLoggedInException, GeneralWebException, NoNetworkConnectionException, WifiConnectionRequiredException, BackupException, SyncAlreadyBusyException, SynchronizationFailedException;

    /**
     * Checks if a synchronisation is going on or not. It also checks for the timeout to be reached or not.
     * @return {@link Boolean#TRUE} if the synchronization is going on, {@link Boolean#FALSE} if not.
     */
    boolean isSyncBusy();

    /**
     * Get the latest sync history object.
     * @return The latest {@link SyncHistory} object or null if none.
     */
    SyncHistory getLastSyncHistory();

    /**
     * Log the current logged in user out, remove all the account-data stored locally and remove all the sync-keys from
     * the entities.
     */
    void logout();

    /**
     * Remove all account-data (such as the {@link eu.vranckaert.worktime.model.SyncRemovalCache}, the
     * {@link eu.vranckaert.worktime.model.SyncHistory} and the {@link User}.
     */
    void removeAll();

    /**
     * Find the chronologically ordered synchronization history objects.
     * @return The list of synchronization history objects in chronological order.
     */
    List<SyncHistory> findAllSyncHistories();

    /**
     * Request a reset of the password for a certain user account.
     * @param email The email address for which the reset request should be made. If the password is not know no error
     *              will be returned.
     * @throws NoNetworkConnectionException Throw if no network connection is available when making the call to the
     * remote server.
     * @throws GeneralWebException Thrown if anything goes wrong while calling the remote server or if anything went
     * wrong on the server internally during the call.
     */
    void resetPasswordRequest(String email) throws GeneralWebException, NoNetworkConnectionException;

    /**
     * Reset the password based on a unique password reset key.
     * @param passwordResetRequestKey The password reset key.
     * @param newPassword             The new password.
     * @throws NoNetworkConnectionException Throw if no network connection is available when making the call to the
     * remote server.
     * @throws GeneralWebException Thrown if anything goes wrong while calling the remote server or if anything went
     * wrong on the server internally during the call.
     * @throws PasswordLengthValidationException Thrown if the password length is invalid (< 6 or > 30 chars).
     * @throws InvalidPasswordResetKeyException Thrown if the password reset key is invalid.
     * @throws PasswordResetKeyAlreadyUsedException Thrown if the password reset key is already  used before.
     * @throws PasswordResetKeyExpiredException Throw if the password reset key has expired (> 24 hours).
     */
    void resetPassword(String passwordResetRequestKey, String newPassword) throws PasswordResetKeyExpiredException, InvalidPasswordResetKeyException, PasswordLengthValidationException, GeneralWebException, PasswordResetKeyAlreadyUsedException, NoNetworkConnectionException;

    /**
     * Clears the entire synchronization history.
     */
    void clearSyncHistory();
}
