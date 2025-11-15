package com.taskscheduler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileUploadEvent extends BaseEvent {
    private String bucketName;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String uploadedBy;

    public FileUploadEvent() {
        setEventType("FILE_UPLOAD");
        setSource("S3_STORAGE");
    }
}
