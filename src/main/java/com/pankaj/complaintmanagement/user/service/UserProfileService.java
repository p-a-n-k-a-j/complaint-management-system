package com.pankaj.complaintmanagement.user.service;

import com.pankaj.complaintmanagement.auth.repository.AuthRepository;
import com.pankaj.complaintmanagement.common.enums.AccountStatus;
import com.pankaj.complaintmanagement.entity.User;
import com.pankaj.complaintmanagement.entity.UserProfile;
import com.pankaj.complaintmanagement.exception.custom.UserNotFoundException;
import com.pankaj.complaintmanagement.exception.custom.UserProfileNotFoundException;
import com.pankaj.complaintmanagement.user.dto.UpdateProfileRequest;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.repository.UserProfileRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthRepository authRepository;
    @Value("${project.image.path}")
    private String folder;
    @Autowired
    public UserProfileService(AuthRepository authRepository, UserProfileRepository userProfileRepository){
       this.authRepository = authRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public UserDto getUser(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("user profile not found"));
        return mapUserTOUserDto(user, userProfile);

    }
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public List<UserDto> getAllUser(){
        return authRepository.findAll()
                .stream().map(user ->{
                    UserProfile userProfile = userProfileRepository.findByUser(user)
                            .orElseThrow(() -> new UserProfileNotFoundException("user profile not found"));
                    return mapUserTOUserDto(user, userProfile);
                }).toList();
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
                .imageUrl((userProfile.getImageName() !=null && !userProfile.getImageName().isBlank())? getImageUrl(userProfile.getImageName()): null)
                .bio(userProfile.getBio())
                .pinCode(userProfile.getPinCode())
                .build();


    }
@Transactional
    public void updateUserProfile(UpdateProfileRequest profileRequest, User user) {
        UserProfile userProfile =userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("user Profile not found"));
        if(profileRequest.getName() !=null)userProfile.setFullName(profileRequest.getName());
        if(profileRequest.getCity()!=null) userProfile.setCity(profileRequest.getCity());
        if(profileRequest.getAddress() !=null)userProfile.setAddress(profileRequest.getAddress());
        if(profileRequest.getImageName() != null && !profileRequest.getImageName().isBlank())userProfile.setImageName(profileRequest.getImageName());
        userProfile.setLastUpdate(LocalDateTime.now());
        if(profileRequest.getPhone() !=null )userProfile.setPhone(profileRequest.getPhone());
        if(profileRequest.getPinCode() != 0)userProfile.setPinCode(profileRequest.getPinCode());
        if(profileRequest.getState() != null)userProfile.setState(profileRequest.getState());
        if(profileRequest.getBio() != null)userProfile.setBio(profileRequest.getBio());
    }

    @Transactional
    public void deleteUserProfile(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("user not found"));
        user.setStatus(AccountStatus.DELETED);

        UserProfile userProfile =userProfileRepository.findByUser(user).orElseThrow(() -> new UserProfileNotFoundException("UserProfile not found"));
        String path = System.getProperty("user.dir")+ File.separator + folder + File.separator +userProfile.getImageName();
        File old = new File(path);
        if(old.exists())old.delete();
        userProfileRepository.delete(userProfile);
    }
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

    }

    private String getImageUrl(String imageName){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/")
                .path(imageName)
                .toUriString();
    }
    @RolesAllowed({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public UserDto getUserProfileById(Long id) {
        User user = authRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
        UserProfile profile = userProfileRepository.findByUser(user).orElseThrow(()-> new UserProfileNotFoundException("user profile not found!"));
        return mapUserTOUserDto(user, profile);
    }

    @Transactional
    public void removeProfileImage(User  user) {
        UserProfile profile = userProfileRepository.findByUser(user).orElseThrow(()-> new UserProfileNotFoundException("User Profile not found!"));
        profile.setImageName(null);

    }
}
