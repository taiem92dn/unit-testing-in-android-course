package com.techyourchance.testdrivendevelopment.exercise6;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.*;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncTest {
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final User USER = new User(USER_ID, USERNAME);

    // region constants

    // endregion constants

    // region helper fields
    @Mock
    FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;

    @Mock
    UsersCache mUsersCache;
    // endregion helper fields

    FetchUserUseCaseSyncImpl SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(mFetchUserHttpEndpointSync, mUsersCache);
        success();
    }

    // correct parameters passed to endpoint Sync
    @Test
    public void fetchUser_notInCacheEndpointSuccess_userIdPassedToEndpoint() throws NetworkErrorException {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mFetchUserHttpEndpointSync).fetchUserSync(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    // fetch user success - success returned
    @Test
    public void fetchUser_notInCacheEndpointSuccess_successReturned() {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    // fetch user success - not in cached - correct user returned
    @Test
    public void fetchUser_notInCacheEndpointSuccess_correctUserReturned() {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        User user = result.getUser();
        assertThat(user, is(notNullValue()));
        assertThat(user.getUsername(), is(USERNAME));
        assertThat(user.getUserId(), is(USER_ID));
    }

    // fetch user success, not in cache - user is cached
    @Test
    public void fetchUser_notInCacheEndpointSuccess_userIsCached() {
        // Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mUsersCache).cacheUser(ac.capture());
        User user = ac.getValue();
        assertThat(user.getUserId(), is(USER_ID));
        assertThat(user.getUsername(), is(USERNAME));
    }

    // fetch user auth error - failure returned
    @Test
    public void fetchUser_notInCacheEndpointAuthError_failureReturned() throws NetworkErrorException {
        // Arrange
        authError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    // fetch user auth error - not in cache - null user returned
    @Test
    public void fetchUser_notInCacheEndpointAuthError_nullUserReturned() throws NetworkErrorException {
        // Arrange
        authError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(nullValue()));
    }

    // fetch user auth error - user is not cached
    @Test
    public void fetchUser_notInCacheEndpointAuthError_userIsNotCached() throws NetworkErrorException {
        // Arrange
        authError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mUsersCache, never()).cacheUser(any(User.class));
    }

    // fetch user general error - failure returned
    @Test
    public void fetchUser_generalError_failureReturned() throws NetworkErrorException {
        // Arrange
        generalError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
        assertThat(result.getUser(), is(nullValue()));
    }

    // fetchUser not in cache , general error - null user returned
    @Test
    public void fetchUser_notInCacheGeneralError_nullUserReturned() throws NetworkErrorException {
        // Arrange
        generalError();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(nullValue()));
    }

    // fetch user general error - user is not cached
    @Test
    public void fetchUser_generalError_userIsNotCached() throws NetworkErrorException {
        // Arrange
        generalError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mUsersCache, never()).cacheUser(any(User.class));
    }
    // no need to call endPointSync if user is already available in UserCache

    // fetch user network exception - network error returned
    @Test
    public void fetchUser_networkException_networkErrorReturned() throws NetworkErrorException {
        // Arrange
        networkException();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
        assertThat(result.getUser(), is(nullValue()));
    }

    // fetch user - not in cache, endpoint network exception - null user returned
    @Test
    public void fetchUser_notInCacheNetworkException_nullUserReturned() throws NetworkErrorException {
        // Arrange
        networkException();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(nullValue()));
    }

    // fetch user - not in cache, endpoint network exception - nothing cached
    @Test
    public void fetchUser_notInCacheNetworkException_nothingCached() throws NetworkErrorException {
        // Arrange
        networkException();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mUsersCache, never()).cacheUser(any(User.class));
    }

    // fetchUser - correct user passed to UsersCache
    @Test
    public void fetchUser_notInCache_correctUserIdPassedToCache() {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mUsersCache).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    // fetchUser - user in cache - success returned
    @Test
    public void fetchUser_userInCache_successReturned() {
        // Arrange
        userInCache();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    // fetchUser - user in cache - cache User returned
    @Test
    public void fetchUser_userInCache_cacheUserReturned() {
        // Arrange
        userInCache();
        // Act
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(USER));
    }

    // fetchUser - user in cache - endpoint not polled
    @Test
    public void fetchUser_userInCache_endPointNotPolled() throws NetworkErrorException {
        // Arrange
        userInCache();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(mFetchUserHttpEndpointSync, never()).fetchUserSync(any(String.class));
    }


    // region correct test cases
    // region test the interaction with Endpoint
//    fetchUserSync_notInCache_correctUserIdPassedToEndpoint

    // endregion test the interaction with Endpoint

    // region test the output (status, user object returned, user cached) 4 status * (2 type output + 1 UsersCache)
//    fetchUserSync_notInCacheEndpointSuccess_successStatus

//    fetchUserSync_notInCacheEndpointSuccess_correctUserReturned ----

//    fetchUserSync_notInCacheEndpointSuccess_userCached

//    fetchUserSync_notInCacheEndpointAuthError_failureStatus

//    fetchUserSync_notInCacheEndpointAuthError_nullUserReturned ----

//    fetchUserSync_notInCacheEndpointAuthError_nothingCached

//    fetchUserSync_notInCacheEndpointServerError_failureStatus

//    fetchUserSync_notInCacheEndpointServerError_nullUserReturned ----

//    fetchUserSync_notInCacheEndpointServerError_nothingCached

//    fetchUserSync_notInCacheEndpointNetworkError_failureStatus

//    fetchUserSync_notInCacheEndpointNetworkError_nullUserReturned ----

//    fetchUserSync_notInCacheEndpointNetworkError_nothingCached

    // endregion test the output (status, user object returned, user cached)

//    fetchUserSync_correctUserIdPassedToCache ---

//    fetchUserSync_inCache_successStatus ----

//    fetchUserSync_inCache_cachedUserReturned ---

//    fetchUserSync_inCache_endpointNotPolled ---
    // endregion correct test cases

    // region helper methods
    private void userInCache() {
        when(mUsersCache.getUser(USER_ID))
                .thenReturn(USER);
    }

    private void success() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void authError() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenReturn(new EndpointResult(EndpointStatus.AUTH_ERROR, "", ""));
    }

    private void generalError() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, "", ""));
    }

    private void networkException() throws NetworkErrorException {
        when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenThrow(new NetworkErrorException());
    }

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}