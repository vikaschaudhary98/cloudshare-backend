package in.vikaschaudhary.cloudshareapi.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import in.vikaschaudhary.cloudshareapi.document.FileData;
 

public interface FileRepository extends MongoRepository<FileData,String>{

}
 