package in.vikaschaudhary.cloudshareapi.service;

import java.time.Instant;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import in.vikaschaudhary.cloudshareapi.document.ProfileDocument;
import in.vikaschaudhary.cloudshareapi.dto.ProfileDto;
import in.vikaschaudhary.cloudshareapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
  private final ProfileRepository profileRepository;
  public ProfileDto createProfile(ProfileDto profileDto) {
	  
	  if(profileRepository.existsByClerkId(profileDto.getClerkId())) {
		  return updateProfile(profileDto);
	  }
	  
	  ProfileDocument profile=ProfileDocument.builder()
			  .clerkId(profileDto.getClerkId())
			  .email(profileDto.getEmail())
			  .firstName(profileDto.getFirstName())
			  .lastName(profileDto.getLastName())
			  .photoUrl(profileDto.getPhotoUrl())
			  .credits(5)
			  .createdAt(Instant.now())
			  .build();
	  profile=profileRepository.save(profile) ; 
			  return ProfileDto.builder()
			 .id(profile.getId())
			 .clerkId(profile.getClerkId())
			 .email(profile.getEmail())
			 .firstName(profile.getFirstName())
			 .lastName(profile.getLastName())
			 .photoUrl(profile.getPhotoUrl())
			 .credits(profile.getCredits())
			 .createdAt(profile.getCreatedAt())
			 .build();
	  
  }
  
  
  public ProfileDto updateProfile(ProfileDto profileDto) {
	   ProfileDocument existingProfile= profileRepository.findByClerkId(profileDto.getClerkId());
   
          if(existingProfile!=null) {
        	  
        	  if(profileDto.getEmail()!=null && !profileDto.getEmail().isEmpty()) {
        		  existingProfile.setEmail(profileDto.getEmail());
        		  
        	  }
        	  if(profileDto.getFirstName()!=null && !profileDto.getFirstName().isEmpty()) {
        		  existingProfile.setFirstName(profileDto.getFirstName());
        		  
        	  }
        	  
        	  if(profileDto.getLastName()!=null && !profileDto.getLastName().isEmpty()) {
        		  existingProfile.setLastName(profileDto.getLastName());
        		  
        	  }
        	  
        	  if(profileDto.getPhotoUrl()!=null && !profileDto.getPhotoUrl().isEmpty()) {
        		  existingProfile.setPhotoUrl(profileDto.getPhotoUrl());
        		  
        	  }
        	  
        	  profileRepository.save(existingProfile);
        	  
        	  
        	  return ProfileDto.builder()
        			  .id(existingProfile.getId())
        			  .email(existingProfile.getEmail())
        			  .clerkId(existingProfile.getClerkId())
        			  .firstName(existingProfile.getFirstName())
        			  .lastName(existingProfile.getLastName())
        			  .photoUrl(existingProfile.getPhotoUrl())
        			  .credits(existingProfile.getCredits())
        			  .createdAt(existingProfile.getCreatedAt())
        			  .build();
          }
		 
          return null;
  }


  public boolean existsByClerkId(String clerkId) {
	  return profileRepository.existsByClerkId(clerkId);
	  
  }
  
  public void deleteprofile(String clerkId) {
	  ProfileDocument existingProfile=profileRepository.findByClerkId(clerkId);
	  if(existingProfile!=null) {
		  profileRepository.delete(existingProfile);
	  }
	  
  }
  
  
  public ProfileDocument getCurrentProfile(){

	    if(SecurityContextHolder.getContext().getAuthentication() == null){
	        throw new UsernameNotFoundException("User Not authenticated");
	    }

	    String clerkId = SecurityContextHolder.getContext()
	            .getAuthentication()
	            .getName();

	    ProfileDocument profile = profileRepository.findByClerkId(clerkId);

	    if(profile == null){
	       throw new UsernameNotFoundException("Profile not found");

	         
	    }

	    return profile;
	}
}
