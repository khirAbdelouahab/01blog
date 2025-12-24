package com.blogger._blog.CustomExceptions;

public class FileSizeLimitExceededException extends RuntimeException {
    private final String filename;
    private final long fileSize;
    private final long maxSize;

    public FileSizeLimitExceededException(String filename, Long fileSize, Long maxSize) {
        super(String.format("File '%s' (%.2fMB) exceeds maximum size of %.2fMB",
                filename,
                fileSize / (1024.0 * 1024.0),
                maxSize / (1024.0 * 1024.0)));
        this.filename = filename;
        this.fileSize = fileSize;
        this.maxSize = maxSize;
    }

    public String getFilename() { return filename; }
    public long getFileSize() { return fileSize; }
    public long getMaxSize() { return maxSize; }
}
