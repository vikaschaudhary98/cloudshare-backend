package in.vikaschaudhary.cloudshareapi.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "payment_transactions")
public class PaymentTransaction {
	@Id
	private String id;
	private String clerkId;
	private String orderId;
	private String paymentId;
	private String planId;
	private int amount;
	private String currency;
	private int creditsAdded;
	private String status;
	private LocalDateTime transactionDate;
	
	private String userEmail;
	private String userName;
	

}
