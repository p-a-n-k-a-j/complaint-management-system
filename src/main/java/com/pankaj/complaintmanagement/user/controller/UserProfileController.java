package com.pankaj.complaintmanagement.user.controller;

import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import com.pankaj.complaintmanagement.user.dto.UpdateProfileRequest;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {
    @Value("${project.image.path}")
    private String folder;
    private final UserProfileService userProfileService;
    @Autowired
    public UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserDto userDto =userProfileService.getUser(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("successfully found user data", userDto));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<UserDto> allUserProfile =userProfileService.getAllUser();
        return ResponseEntity.ok(ApiResponse.success("successfully found user data", allUserProfile));
    }
    @PatchMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest profileRequest, @AuthenticationPrincipal CustomUserDetails userDetails){
        userProfileService.updateUserProfile(profileRequest, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("UserProfile updated successfully"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long userId){
        userProfileService.deleteUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("UserProfile deleted Successfully"));
    }
    @DeleteMapping
    public ResponseEntity<?> deleteCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        userProfileService.deleteUserProfile(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("UserProfile deleted Successfully"));
    }

    @PatchMapping("/image")
    public ResponseEntity<?> setImage(@RequestParam("image")MultipartFile image ,@AuthenticationPrincipal CustomUserDetails userDetails){
        String path = System.getProperty("user.dir") + File.separator + folder;
        String imageUrl=userProfileService.setImageUrl(image, path, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Image Successfully updated",imageUrl));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserProfileById(@PathVariable Long id){
      UserDto userDto =  userProfileService.getUserProfileById(id);
      return ResponseEntity.ok(ApiResponse.success("user found!", userDto));
    }
    @DeleteMapping("/image")
    public ResponseEntity<ApiResponse<?>> removeProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails){
        userProfileService.removeProfileImage(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Profile Image Successfully removed!"));
    }
}
