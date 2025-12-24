package com.blogger._blog.CustomExceptions;

public class TotalSizeLimitExceededException extends RuntimeException {
    private final long totalSize;
    private final long maxSize;

    public TotalSizeLimitExceededException(long totalSize, long maxSize) {
        super(String.format("Total upload size (%.2fMB) exceeds maximum of %.2fMB",
            totalSize / (1024.0 * 1024.0),
            maxSize / (1024.0 * 1024.0)));
        this.totalSize = totalSize;
        this.maxSize = maxSize;
    }

    public long getTotalSize() { return totalSize; }
    public long getMaxSize() { return maxSize; }
}
