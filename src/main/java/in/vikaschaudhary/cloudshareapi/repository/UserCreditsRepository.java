package in.vikaschaudhary.cloudshareapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.vikaschaudhary.cloudshareapi.document.UserCredits;

public interface UserCreditsRepository  extends MongoRepository<UserCredits, String>{
    Optional<UserCredits> findByClerkId(String clerkId);
}
