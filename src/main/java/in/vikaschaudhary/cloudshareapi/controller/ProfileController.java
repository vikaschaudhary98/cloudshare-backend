package in.vikaschaudhary.cloudshareapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.vikaschaudhary.cloudshareapi.dto.ProfileDto;
import in.vikaschaudhary.cloudshareapi.service.ProfileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {

	private final  ProfileService profileService;
	
	@PostMapping("/register")
	public ResponseEntity<?> registerProfile(@RequestBody ProfileDto profileDto){
		HttpStatus status=	profileService.existsByClerkId(profileDto.getClerkId())?HttpStatus.OK :HttpStatus.CREATED;
		ProfileDto savedProfileDto=profileService.createProfile(profileDto);
	
		return ResponseEntity.status(status).body(savedProfileDto);
	}
	
	
}
