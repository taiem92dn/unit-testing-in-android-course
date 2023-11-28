package com.techyourchance.testdoublesfundamentals.exercise4;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "userId";
    private UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSyncTd;

    private UsersCacheTd mUsersCacheTd;
    private FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTd, mUsersCacheTd);
        System.out.println("======= Setup =========");
    }

    // pass userId to UserProfileHttpEndpointSync

    @Test
    public void fetchUserProfile_success_passedUserIdToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUserProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }


    // fetching UserProfile succeed, save UserProfile to UsersCache

    @Test
    public void fetchUserProfile_success_saveUserProfileToUsersCache() {
        SUT.fetchUserProfileSync(USER_ID);
        User result = mUsersCacheTd.getUser(USER_ID);
        assertThat(result, notNullValue());
        assertThat(result.getUserId(), is(USER_ID));
    }

    // fetching UserProfile fail, UserCache doesn't change
    @Test
    public void fetchUserProfile_generalError_UserNotCached() {
        mUserProfileHttpEndpointSyncTd.isGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User result = mUsersCacheTd.getUser(USER_ID);
        assertThat(result, nullValue());
    }

    @Test
    public void fetchUserProfile_authError_UsersCacheNotChange() {
        mUserProfileHttpEndpointSyncTd.isAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User result = mUsersCacheTd.getUser(USER_ID);
        assertThat(result, nullValue());
    }

    @Test
    public void fetchUserProfile_serverError_UsersCacheNotChange() {
        mUserProfileHttpEndpointSyncTd.isServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User result = mUsersCacheTd.getUser(USER_ID);
        assertThat(result, nullValue());
    }

    // fetching UserProfile succeed, success value is returned
    @Test
    public void fetchUserProfile_success_successValueReturned() {
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }
    // fetching UserProfile get auth error, failure error value is returned
    @Test
    public void fetchUserProfile_authError_failureValueReturned() {
        mUserProfileHttpEndpointSyncTd.isAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    // fetching UserProfile get server error, failure error value is returned
    @Test
    public void fetchUserProfile_serverError_failureValueReturned() {
        mUserProfileHttpEndpointSyncTd.isServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    // fetching UserProfile get general error, failure error value is returned
    @Test
    public void fetchUserProfile_generalError_failureValueReturned() {
        mUserProfileHttpEndpointSyncTd.isGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    // fetching UserProfile get network error, network error value is returned
    @Test
    public void fetchUserProfile_networkError_failureValueReturned() {
        mUserProfileHttpEndpointSyncTd.isNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        public String mUserId;
        public boolean isGeneralError;
        public boolean isAuthError;
        public boolean isServerError;
        public boolean isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (isGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            }
            else if (isAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            }
            else if (isServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            }
            else if (isNetworkError) {
                throw new NetworkErrorException();
            }
            return new EndpointResult(EndpointResultStatus.SUCCESS, userId, "", "");
        }
    }

    private static class UsersCacheTd implements UsersCache {

        public User mUser;

        @Override
        public void cacheUser(User user) {
            mUser = user;
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            return mUser;
        }
    }
}