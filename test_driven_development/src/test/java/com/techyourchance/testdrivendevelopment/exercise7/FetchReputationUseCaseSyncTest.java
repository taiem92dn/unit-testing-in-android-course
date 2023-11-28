package com.techyourchance.testdrivendevelopment.exercise7;

import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {
    public static final int REPUTATION = 1;

    // region constants

    // endregion constants

    // region helper fields
    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSync;

    // endregion helper fields

    FetchReputationUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSync(mGetReputationHttpEndpointSync);

        success();
    }

    // fetchReputation success, success returned
    @Test
    public void fetchReputation_success_successReturned() {
        // Arrange
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseStatus.SUCCESS));
    }

    // fetchReputation success, fetched reputation returned
    @Test
    public void fetchReputation_success_fetchedReputationReturned() {
        // Arrange
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result.getReputation(), is(REPUTATION));
    }

    // fetchReputation general error, failure returned
    @Test
    public void fetchReputation_generalError_failureReturned() {
        // Arrange
        generalError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseStatus.FAILURE));
    }


    // fetchReputation general error, zero reputation returned

    @Test
    public void fetchReputation_generalError_zeroReputationReturned() {
        // Arrange
        generalError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result.getReputation(), is(0));
    }


    // fetchReputation network error, failure returned
    @Test
    public void fetchReputation_networkError_failureReturned() {
        // Arrange
        networkError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result.getStatus(), is(FetchReputationUseCaseSync.UseCaseStatus.FAILURE));
    }

    // fetchReputation network error, zero reputation returned
    @Test
    public void fetchReputation_networkError_zeroReputationReturned() {
        // Arrange
        networkError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result = SUT.fetchReputation();
        // Assert
        assertThat(result.getReputation(), is(0));
    }

    // region helper methods
    private void success() {
        when(mGetReputationHttpEndpointSync.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, REPUTATION));
    }

    private void generalError() {
        when(mGetReputationHttpEndpointSync.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, 0));
    }

    private void networkError() {
        when(mGetReputationHttpEndpointSync.getReputationSync())
                .thenReturn(new EndpointResult(EndpointStatus.NETWORK_ERROR, 0));
    }
    // endregion helper methods

    // region helper classes

    // endregion helper classes
}