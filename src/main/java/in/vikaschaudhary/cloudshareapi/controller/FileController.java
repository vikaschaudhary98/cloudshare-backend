package in.vikaschaudhary.cloudshareapi.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import in.vikaschaudhary.cloudshareapi.config.StaticResourceConfig;
import in.vikaschaudhary.cloudshareapi.document.UserCredits;
import in.vikaschaudhary.cloudshareapi.dto.FileMetadataDto;
import in.vikaschaudhary.cloudshareapi.service.FileMetadataService;
import in.vikaschaudhary.cloudshareapi.service.UserCreditsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final StaticResourceConfig staticResourceConfig;

             private final ClerkWebhookController clerkWebhookController;
	
            private final FileMetadataService fileMetadataService;
            private final UserCreditsService userCreditsService;

   
            @PostMapping("/upload")
            public ResponseEntity<?> uploadFiles(@RequestPart("files") MultipartFile files[]) throws IOException {
            Map<String , Object> response=new HashMap<>();	
            List<FileMetadataDto> list =fileMetadataService.uploadFiles(files);
            
         UserCredits finalCredits=userCreditsService.getUserCredits();
            
            response.put("files", list);
            response.put("remainingCredits", finalCredits.getCredits());
            return ResponseEntity.ok(response);
            	
            }
            @GetMapping("/my")
            public ResponseEntity<?> getfilesForCurrentUser(){
            List<FileMetadataDto>files=	fileMetadataService.getFiles();
            	return ResponseEntity.ok(files);
            }
            @GetMapping("/public/{id}")
            public ResponseEntity<?> getPublicFile(@PathVariable String id){
            	FileMetadataDto file= fileMetadataService.getPublicfile(id);
            	return ResponseEntity.ok(file);
            }
            
            @GetMapping("/download/{id}")
            public ResponseEntity<Resource> download(@PathVariable String id) throws MalformedURLException {
            	FileMetadataDto downloadableFile=fileMetadataService.getDownloadableFile(id);
            	Path path=Paths.get(downloadableFile.getFileLocation());
                 Resource resource= new UrlResource(path.toUri());
            	
            	
            	return ResponseEntity.ok()
            			.contentType(MediaType.APPLICATION_OCTET_STREAM)
            			.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+downloadableFile.getName()+"\"")
            			.body(resource);
            }
            
            @DeleteMapping("/{id}")
            public ResponseEntity<?> deleteFile(@PathVariable String id){
            	
            	
            	fileMetadataService.deleteFile(id);
            	return ResponseEntity.noContent().build();
            }
            
            @PatchMapping("/{id}/toggle-public")
            public ResponseEntity<?> togglePublic(@PathVariable String id){
            	FileMetadataDto file=fileMetadataService.togglePublic(id);
            	return ResponseEntity.ok(file);
            	
            }
            
            
            
            
            
            
}
