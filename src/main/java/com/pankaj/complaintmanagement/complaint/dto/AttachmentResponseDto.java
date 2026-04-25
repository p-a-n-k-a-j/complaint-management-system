package com.pankaj.complaintmanagement.complaint.dto;

public class AttachmentResponseDto {
    private Long id;
    private Long complaintId;
    private String attachmentUrl;
    private String publicId;

    private AttachmentResponseDto(Builder builder) {
        this.attachmentUrl = builder.attachmentUrl;
        this.publicId = builder.publicId;
        this.complaintId = builder.complaintId;
        this.id = builder.id;
    }


    public Long getId() {
        return id;
    }
    public Long getComplaintId() {
        return complaintId;
    }
    public String getAttachmentUrls() {
        return attachmentUrl;
    }



    public String getPublicId() {
        return publicId;
    }



    public static class Builder{
        private Long id;
        private Long complaintId;
        private String attachmentUrl;
        private String publicId;

        public Builder id(Long id){
            this.id = id;
            return this;
        }
        public Builder complaintId(Long complaintId){
            this.complaintId = complaintId;
            return this;
        }
        public Builder attachmentUrl(String attachmentUrl){
            this.attachmentUrl=attachmentUrl;
            return this;
        }
        public Builder publicId(String publicId){
            this.publicId = publicId;
            return this;
        }
        public AttachmentResponseDto build(){
            return new AttachmentResponseDto(this);
        }
    }
}
