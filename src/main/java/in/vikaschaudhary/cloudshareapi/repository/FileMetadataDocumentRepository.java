package in.vikaschaudhary.cloudshareapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.vikaschaudhary.cloudshareapi.document.FileMetadataDocument;

public interface FileMetadataDocumentRepository extends MongoRepository<FileMetadataDocument, String>{

	
	List<FileMetadataDocument> findByClerkId(String clerkId);
	Long countByClerkId(String clerkId);
}
