package com.manage.hospital.hmapp.Extras.broadcast_receiver;

public enum FallDetectionState {
    NATURAL("NATURAL"),
    FALL_START("FALL START"),
    HIT_GROUND("HIT GROUND"),
    FALL_DETECTED("FALL DETECTED"),
    WALKING("WALKING");

    private String stateString;

    FallDetectionState(String s) {
        stateString = s;
    }

    public String toString() {
        return stateString;
    }
}
