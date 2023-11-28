package com.techyourchance.testdrivendevelopment.exercise6;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.*;
import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.*;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    UsersCache mUserCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.mFetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.mUserCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        User user;
        user = mUserCache.getUser(userId);
        if (user != null)
            return new UseCaseResult(Status.SUCCESS, user);

        EndpointResult result;
        try {
            result = mFetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException exception) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

        if (result.getStatus() == EndpointStatus.SUCCESS) {
            user = new User(result.getUserId(), result.getUsername());
            mUserCache.cacheUser(user);

            return new UseCaseResult(Status.SUCCESS, user);
        }

        return new UseCaseResult(Status.FAILURE, null);
    }
}
