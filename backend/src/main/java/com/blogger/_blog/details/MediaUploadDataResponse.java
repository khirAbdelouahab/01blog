package com.blogger._blog.details;
import java.util.ArrayList;
import java.util.List;

import com.blogger._blog.enums.MediaType;
import com.blogger._blog.model.MediaUpload;
public class MediaUploadDataResponse {
    private Long id;
    private String content;
    private MediaType type;
    public MediaUploadDataResponse(){}
    public MediaUploadDataResponse(Long id,String content,MediaType type) {
        this.id=id;
        this.type=type;
        this.content=content;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id=id;
    }
    public String getContent() {
        return this.content;
    }
    public MediaType getType() {
        return this.type;
    }
    public void setType(MediaType type) {
        this.type=type;
    }
    public void setContent(String content) {
        this.content=content;
    }
    public static MediaUploadDataResponse convert(MediaUpload mediaUpload) {
        return new MediaUploadDataResponse(mediaUpload.getId(),mediaUpload.getContent(),mediaUpload.getMedia_type());
    }
    
    @Override
    public String toString() {
        return String.format("{id: %d, content: %s, type: %s}", this.id,this.content,this.type);
    }
    public static List<MediaUploadDataResponse> convert(List<MediaUpload> mediaUploads) {
        List<MediaUploadDataResponse> mediaUploadDataResponses = new ArrayList<>();
        for (int i = 0; i < mediaUploads.size(); i++) {
            mediaUploadDataResponses.add(MediaUploadDataResponse.convert(mediaUploads.get(i)));
        }
        return mediaUploadDataResponses;
    }
}
