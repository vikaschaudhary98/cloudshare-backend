package in.vikaschaudhary.cloudshareapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import in.vikaschaudhary.cloudshareapi.document.FileMetadataDocument;
import in.vikaschaudhary.cloudshareapi.document.ProfileDocument;
import in.vikaschaudhary.cloudshareapi.dto.FileMetadataDto;
import in.vikaschaudhary.cloudshareapi.repository.FileMetadataDocumentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileMetadataService {
	private final ProfileService profileService;
	private final UserCreditsService userCreditsService;
	private final FileMetadataDocumentRepository fileMetadataDocumentRepository;
    List<FileMetadataDocument> savedfiles=new ArrayList<>();
	public List<FileMetadataDto> uploadFiles(MultipartFile files[]) throws IOException{
		ProfileDocument currentprofile=profileService.getCurrentProfile();
		
		if(!userCreditsService.hasEnoughCredits(files.length)) {
			throw new RuntimeException("Not enough credits to upload files.");
		
		
	}
		Path uploadPath=Paths.get("upload").toAbsolutePath().normalize();
		Files.createDirectories(uploadPath);
		
	for(MultipartFile file:files) {
		String fileName=UUID.randomUUID()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());
	
		
		Path targetLocation=uploadPath.resolve(fileName);
		Files.copy(file.getInputStream(),targetLocation ,StandardCopyOption.REPLACE_EXISTING);
		
		
		FileMetadataDocument fileMetadata=FileMetadataDocument.builder()
				.fileLocation(targetLocation.toString())
				.name(file.getOriginalFilename())
				.size(file.getSize())
				.type(file.getContentType())
				.clerkId(currentprofile.getClerkId())
				.isPublic(false)
				.uploadedAt(LocalDateTime.now())
				.build();
		
		//TODO: consume one credit for each file
		userCreditsService.consumeCredit();
		savedfiles.add(fileMetadataDocumentRepository.save(fileMetadata));
		
		
	}
	
	 return savedfiles.stream().map(fileMetadataDocument -> mapToDto(fileMetadataDocument))
	.collect(Collectors.toList());
		
}
	
	private FileMetadataDto mapToDto(FileMetadataDocument fileMetadataDocument) {
		return  FileMetadataDto.builder()
			.id(fileMetadataDocument.getId())	
		    .fileLocation(fileMetadataDocument.getFileLocation())
			.name(fileMetadataDocument.getName())
			.size(fileMetadataDocument.getSize())
			.type(fileMetadataDocument.getType())
			.clerkId(fileMetadataDocument.getClerkId())
			.isPublic(fileMetadataDocument.getIsPublic())
			.uploadedAt(fileMetadataDocument.getUploadedAt())
			.build();
		
	}
	public  List<FileMetadataDto> getFiles(){
		ProfileDocument currentProfile=profileService.getCurrentProfile();
		List<FileMetadataDocument> files=fileMetadataDocumentRepository.findByClerkId(currentProfile.getClerkId());
	return 	files.stream().map(this::mapToDto).collect(Collectors.toList());
	}
	
	public FileMetadataDto getPublicfile(String id) {
	Optional<FileMetadataDocument>fileOptional=	fileMetadataDocumentRepository.findById(id);
	
	  if(fileOptional.isEmpty() || !fileOptional.get().getIsPublic() ) {
		  throw new RuntimeException("Unable To get The File");
		  
		  
	  }
	
	               FileMetadataDocument document= fileOptional.get();
	             return mapToDto(document);
	}
	
	public FileMetadataDto getDownloadableFile(String id) {
	FileMetadataDocument file=	fileMetadataDocumentRepository.findById(id).orElseThrow(()->new RuntimeException("File Not found"));
      return mapToDto(file);
	
	}
	
	
	public void deleteFile(String id) {
		
		try {
			
		ProfileDocument currentProfile= profileService.getCurrentProfile();	
			FileMetadataDocument file=fileMetadataDocumentRepository.findById(id)
					.orElseThrow(()-> new RuntimeException("file not found"));
			
			 if(!file.getClerkId().equals(currentProfile.getClerkId())) {
				 throw new RuntimeException("File is not being to current User");
			 }
			 
			 Path filePath=Paths.get(file.getFileLocation());
			 Files.deleteIfExists(filePath);
			 
			fileMetadataDocumentRepository.deleteById(id);
			
			
		} catch (Exception e) {
			 
			throw new RuntimeException("Error deleting the file ");
		}
	}
	
	public FileMetadataDto togglePublic(String id) {
	FileMetadataDocument file=	fileMetadataDocumentRepository.findById(id).orElseThrow(()->new RuntimeException("File Not Found"));
	  file.setIsPublic(!file.getIsPublic());
	  fileMetadataDocumentRepository.save(file);
	  return mapToDto(file);
	
	}
	
	
	
	
	
	
	
	
	
	
}