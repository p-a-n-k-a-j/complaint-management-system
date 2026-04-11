package com.pankaj.complaintmanagement.common.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    public CloudinaryService(Cloudinary cloudinary){
        this.cloudinary = cloudinary;
    }
//common upload method
    public Map upload(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    }

    //common delete method
    public Map delete(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public Map updateFile(String publicId, MultipartFile file) throws IOException {
      //trying to delete an old file
        if(publicId != null && !publicId.isEmpty()){
          this.delete(publicId);
          //we don't check the result
            //is file deleted, or not we still upload the file
      }
        //upload the new file
      return this.upload(file);
    }

    //todo: this is the method for learning purpose like when I want to upload video then it helps
//    public Map upload(MultipartFile file) throws IOException {
//        // "resource_type", "auto" add karna best hai taaki video bhi handle ho jaye
//        return cloudinary.uploader().upload(file.getBytes(),
//                ObjectUtils.asMap("resource_type", "auto"));
//    }
}
