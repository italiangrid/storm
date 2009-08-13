/**
 * 
 */
package it.grid.storm.tape.recalltable.model;


/**
 * @author zappi
 *
 */
public enum RecallTaskStatus {

    SUCCESS(0, "Success"),
    QUEUED(1, "Queued"),
    IN_PROGRESS(2, "In Progress"),
    ERROR(3, "Error"),
    ABORTED(4, "Aborted"),
    UNDEFINED(5, "Undefined");

    private final int taskStatusRepresentation;
    private final String statusName;

    private RecallTaskStatus(int statusId, String statusName) {
        taskStatusRepresentation = statusId;
        this.statusName = statusName;
    }

    public static RecallTaskStatus getRecallTaskStatus(int statusId) {
        switch (statusId) {
        case 0:
            return SUCCESS;
        case 1:
            return QUEUED;
        case 2:
            return IN_PROGRESS;
        case 3:
            return ERROR;
        case 4:
            return ABORTED;
        default:
            return UNDEFINED;
        }
    }

    public int getStatusId() {
        return taskStatusRepresentation;
    }
    
    @Override
    public String toString() {
        return statusName;
    }
    
}
