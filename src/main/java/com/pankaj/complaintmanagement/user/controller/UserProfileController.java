package com.pankaj.complaintmanagement.user.controller;

import com.pankaj.complaintmanagement.common.response.ApiResponse;
import com.pankaj.complaintmanagement.security.CustomUserDetails;
import com.pankaj.complaintmanagement.user.dto.UpdateProfileRequest;
import com.pankaj.complaintmanagement.user.dto.UserDto;
import com.pankaj.complaintmanagement.user.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user/")
public class UserProfileController {
    private UserProfileService userProfileService;
    public UserProfileController(UserProfileService userProfileService){
        this.userProfileService = userProfileService;
    }
    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserDto userDto =userProfileService.getUser(userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("successfully found user data", userDto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<UserDto> allUserProfile =userProfileService.getAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("successfully found user data", allUserProfile));
    }
    @PatchMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest profileRequest, @AuthenticationPrincipal CustomUserDetails userDetails){
        userProfileService.updateUserProfile(profileRequest, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("UserProfile updated successfully"));
    }


}
