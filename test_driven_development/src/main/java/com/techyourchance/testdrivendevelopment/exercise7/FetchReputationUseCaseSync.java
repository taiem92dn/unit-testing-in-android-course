package com.techyourchance.testdrivendevelopment.exercise7;

import static com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.*;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public UseCaseResult fetchReputation() {
        EndpointResult result = getReputationHttpEndpointSync.getReputationSync();

        switch (result.getStatus()) {
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new UseCaseResult(UseCaseStatus.FAILURE, result.getReputation());
            case SUCCESS:
                return new UseCaseResult(UseCaseStatus.SUCCESS, result.getReputation());
            default:
                throw new RuntimeException("Not supported this status " + result.getStatus());
        }
    }

    public enum UseCaseStatus {
        SUCCESS, FAILURE;
    }

    public class UseCaseResult {

        private final UseCaseStatus status;
        private final int reputation;

        public UseCaseResult(UseCaseStatus status, int reputation) {
            this.status = status;
            this.reputation = reputation;
        }

        public int getReputation() {
            return reputation;
        }

        public UseCaseStatus getStatus() {
            return status;
        }
    }
}
