package com.techyourchance.mockitofundamentals.exercise5;

import static com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResult;
import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

public class UpdateUsernameUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    private UpdateUsernameUseCaseSync SUT;

    private UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSyncMock;

    private UsersCache mUsersCacheMock;

    private EventBusPoster mEventBusPosterMock;

    @Before
    public void setUp() throws Exception {
        mUpdateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCacheMock = mock(UsersCache.class);
        mEventBusPosterMock = mock(EventBusPoster.class);

        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSyncMock, mUsersCacheMock, mEventBusPosterMock);

        success();
    }

    @Test
    public void updateUsername_success_userIdAndUsernamePassedToEndPoint() throws NetworkErrorException {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUpdateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USERNAME));
    }

    @Test
    public void updateUsername_success_userIsCached() {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mUsersCacheMock).cacheUser(ac.capture());
        List<User> captures = ac.getAllValues();
        User user = captures.get(0);
        assertThat(user.getUserId(), is(USER_ID));
        assertThat(user.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsername_generalError_userNotCached() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }


    @Test
    public void updateUsername_severError_userNotCached() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }
    @Test
    public void updateUsername_authError_userNotCached() throws NetworkErrorException {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_success_userEventPosted() {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(mEventBusPosterMock).postEvent(ac.capture());
        List<Object> captures = ac.getAllValues();
        assertThat(captures.get(0), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsername_generalError_noInteractionWithEventPoster() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUsername_serverError_noInteractionWithEventPoster() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUsername_authError_noInteractionWithEventPoster() throws NetworkErrorException {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUsername_success_successIsReturned() {
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsername_generalError_failureIsReturned() throws NetworkErrorException {
        generalError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_authError_failureIsReturned() throws NetworkErrorException {
        authError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_serverError_failureIsReturned() throws NetworkErrorException {
        serverError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_networkError_networkErrorIsReturned() throws NetworkErrorException {
        networkError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    private void success() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(
                        EndpointResultStatus.SUCCESS,
                        USER_ID,
                        USERNAME
                ));
    }

    private void generalError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(
                        EndpointResultStatus.GENERAL_ERROR, "", ""
                ));
    }

    private void serverError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(
                        EndpointResultStatus.SERVER_ERROR, "", ""
                ));
    }

    private void authError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(
                        EndpointResultStatus.AUTH_ERROR, "", ""
                ));
    }

    private void networkError() throws NetworkErrorException {
        doThrow(new NetworkErrorException())
                .when(mUpdateUsernameHttpEndpointSyncMock)
                .updateUsername(any(String.class), any(String.class));
    }
}