package com.pankaj.complaintmanagement.user.service;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.common.services.CloudinaryService;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.entity.UserProfile;
import com.pankaj.complaintmanagement.exception.custom.UserNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UserProfileNotFoundException;
import com.pankaj.complaintmanagement.user.dto.UpdateProfileRequest;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import com.pankaj.complaintmanagement.util.UserRole;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthRepository authRepository;
    private final CloudinaryService cloudinaryService;
    @Autowired
    public UserProfileService(AuthRepository authRepository, UserProfileRepository userProfileRepository, CloudinaryService cloudinaryService){
       this.authRepository = authRepository;
        this.userProfileRepository = userProfileRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public UserDto getUser(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return mapUserTOUserDto(user, user.getUserProfile());

    }
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDto> getAllUser(int page, int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
    return authRepository.findAllByRolesWithProfile(UserRole.ROLE_USER, pageable).map(user -> mapUserTOUserDto(user, user.getUserProfile()));
    }


    private UserDto mapUserTOUserDto(User user, UserProfile userProfile){
        return new UserDto.Builder()
                .id(user.getId())
                .name(userProfile.getFullName())
                .email(user.getEmail())
                .address(userProfile.getAddress())
                .city(userProfile.getCity())
                .phone(userProfile.getPhone())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(userProfile.getLastUpdate())
                .state(userProfile.getState())
                .status(user.getStatus())
                .imageUrl((userProfile.getImageUrl() !=null && !userProfile.getImageUrl().isBlank())? userProfile.getImageUrl(): null)
                .bio(userProfile.getBio())
                .publicId(userProfile.getPublicId())
                .pinCode(userProfile.getPinCode())
                .build();


    }
@Transactional
    public void updateUserProfile(UpdateProfileRequest profileRequest, User user) {
        UserProfile userProfile =userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("user Profile not found"));
        if(profileRequest.getName() !=null)userProfile.setFullName(profileRequest.getName());
        if(profileRequest.getCity()!=null) userProfile.setCity(profileRequest.getCity());
        if(profileRequest.getAddress() !=null)userProfile.setAddress(profileRequest.getAddress());
        if(profileRequest.getImage() != null && !profileRequest.getImage().isEmpty()){
            setImageUrl(profileRequest.getImage(), user);
        }
        userProfile.setLastUpdate(LocalDateTime.now());
        if(profileRequest.getPhone() !=null )userProfile.setPhone(profileRequest.getPhone());
        if(profileRequest.getPinCode() != 0)userProfile.setPinCode(profileRequest.getPinCode());
        if(profileRequest.getState() != null)userProfile.setState(profileRequest.getState());
        if(profileRequest.getBio() != null)userProfile.setBio(profileRequest.getBio());
    }

    public String setImageUrl(MultipartFile file,User user){
        UserProfile userProfile = userProfileRepository.findByUser(user).orElseThrow(()-> new UserProfileNotFoundException("user profile not found"));
        Map upload = cloudinaryService.upload(file);
       String url= upload.get("secure_url").toString();
        userProfile.setImageUrl(url);
        userProfile.setPublicId(upload.get("public_id").toString());
        return url;
    }


/*    private String getImageUrl(String imageName){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(imageName)
                .toUriString();
    }*/
    @RolesAllowed({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public UserDto getUserProfileById(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
        UserProfile profile = userProfileRepository.findByUser(user).orElseThrow(()-> new UserProfileNotFoundException("user profile not found!"));
        return this.mapUserTOUserDto(user, profile);
    }

    @Transactional
    public void removeProfileImage(User  user) {
        UserProfile profile = userProfileRepository.findByUser(user).orElseThrow(()-> new UserProfileNotFoundException("User Profile not found!"));
        String publicId = profile.getPublicId();
        cloudinaryService.delete(publicId);
        profile.setImageUrl(null);
        profile.setPublicId(null);

    }

   @Transactional
    public void deleteUserProfile(Long id)  {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
        user.setStatus(AccountStatus.DELETED);

        UserProfile userProfile =userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("UserProfile not found"));
       if(userProfile.getPublicId() != null && userProfile.getPublicId().isBlank()){
           cloudinaryService.delete(userProfile.getPublicId());
       }
       userProfileRepository.delete(userProfile);
    }
    //todo: these method are designed for folder storage now I don't need at all, because we sift to cloud
       /*
    @Transactional
    public String setImageUrl(MultipartFile file, String path, User user) {
        if(file.isEmpty()) throw new RuntimeException("File is empty");
        //sabse phle file ka original file name nikalenge.
        String fileName = file.getOriginalFilename();
        if(fileName == null || !fileName.matches(".*\\.(png|jpg|jpeg|gif)$")){
            throw new RuntimeException("Invalid image format");
        }
        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image allowed");
        }


        //rename file name
        String randomId = UUID.randomUUID().toString();
        String fileRandomName = randomId.concat(fileName.substring(fileName.lastIndexOf('.')));
        //file ka random name bnayenge
        String filePath = path + File.separator + fileRandomName;

        //image tak ka path denge or check karenge hai ya nhi .
        File newFile = new File(path);
        if(!newFile.exists()){
            newFile.mkdirs();
        }

        //ab file ko copy
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException ex){
            throw new RuntimeException("File upload fail");
        }
        UserProfile profile = userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("UserProfile not found"));
        if(profile.getImageName() != null){
            File old = new File( path + File.separator +profile.getImageName());
            if(old.exists())old.delete();
        }
        profile.setImageName(fileRandomName);

        return getImageUrl(fileRandomName);

    }*/
}
