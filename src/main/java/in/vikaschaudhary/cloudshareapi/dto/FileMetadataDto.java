package in.vikaschaudhary.cloudshareapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class FileMetadataDto {

	
	private String id;
	private String name;
	private String type;
	private Long size;
	private String clerkId;
	private Boolean isPublic;
	private String fileLocation;
	private LocalDateTime uploadedAt;
}
