package in.vikaschaudhary.cloudshareapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vikaschaudhary.cloudshareapi.config.SecurityConfig;
import in.vikaschaudhary.cloudshareapi.dto.ProfileDto;
import in.vikaschaudhary.cloudshareapi.service.ProfileService;
import in.vikaschaudhary.cloudshareapi.service.UserCreditsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class ClerkWebhookController {

    private final SecurityConfig securityConfig;
    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
	@Value("${clerk.webhook.secret}")
	private String webhookSecret;

   
	
	@PostMapping("/clerk")
	public ResponseEntity<?> handleclerkWebhook(@RequestHeader("svix-id") String svixId
			,@RequestHeader("svix-timestamp") String svixTimestamp
			,@RequestHeader("svix-signature") String svixSignature
			,@RequestBody String payload){
		
		
		
		 
		   
		
		try {
			
			boolean isValid=verifyWebhooksignature(svixId,svixTimestamp,svixSignature,payload);
			if(!isValid) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
			}
			
			ObjectMapper mapper=new ObjectMapper();
			JsonNode rootNode=mapper.readTree(payload);
			
			String eventType=rootNode.path("type").asText();
			switch(eventType) {
			case "user.created":
				handleUserCreated(rootNode.path("data"));
				break;
			case "user.updated":
				  handleUserUpdated(rootNode.path("data"));
				  break;
			case "user.deleted":
				handleUserDeleted(rootNode.path("data"));
				break;
				
			}
			return ResponseEntity.ok().build();
					
		
		}
		
		catch (Exception e) {
			
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,e.getMessage());
		}
		
	}
	private void handleUserDeleted(JsonNode data) {
		 
		String clerkId=data.path("id").asText();
		 profileService.deleteprofile(clerkId);
	}
	
	private void handleUserUpdated(JsonNode data) {
		 
		String clerkId=data.path("id").asText();
		String email="";
		JsonNode emailAddresses= data.path("email_addresses");
		if(emailAddresses.isArray() && emailAddresses.size()>0) {
			email=emailAddresses.get(0).path("email_address").asText();
			
		}
		
		String firstName=data.path("first_name").asText("");
		String lastName=data.path("last_name").asText("");
		String photoUrl=data.path("image_url").asText("");
		
		ProfileDto updatedProfile= ProfileDto.builder()
		.clerkId(clerkId)
		.email(email)
		.firstName(firstName)
		.lastName(lastName)
		.photoUrl(photoUrl)
		.build();
		updatedProfile= profileService.updateProfile(updatedProfile);
		if(updatedProfile==null) {
			handleUserCreated(data);
		}
	}

	private void handleUserCreated(JsonNode data) {
		 
		String clerkId=data.path("id").asText();
		String email="";
		JsonNode emailAddresses= data.path("email_addresses");
		if(emailAddresses.isArray() && emailAddresses.size()>0) {
			email=emailAddresses.get(0).path("email_address").asText();
			
		}
		
		String firstName=data.path("first_name").asText("");
		String lastName=data.path("last_name").asText("");
		String photoUrl=data.path("image_url").asText("");
		
		ProfileDto newProfile= ProfileDto.builder()
		.clerkId(clerkId)
		.email(email)
		.firstName(firstName)
		.lastName(lastName)
		.photoUrl(photoUrl)
		.build();
		profileService.createProfile(newProfile);
		userCreditsService.createInitialCredits(clerkId);
		
	}

	private boolean verifyWebhooksignature(String svixId,String svixTimestamp,String svixSignature,String payload) {
		return true;
		
	}
 
	
}
