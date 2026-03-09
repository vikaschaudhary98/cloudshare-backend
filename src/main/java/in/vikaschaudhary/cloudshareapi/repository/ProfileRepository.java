package in.vikaschaudhary.cloudshareapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.vikaschaudhary.cloudshareapi.document.ProfileDocument;

public interface ProfileRepository  extends MongoRepository<ProfileDocument, String>{

	Optional<ProfileDocument> findByEmail(String email);
	
	ProfileDocument findByClerkId(String clerkId);
	 Boolean  existsByClerkId(String clerkId);
}
