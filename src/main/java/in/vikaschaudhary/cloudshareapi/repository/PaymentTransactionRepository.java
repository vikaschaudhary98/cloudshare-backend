package in.vikaschaudhary.cloudshareapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.vikaschaudhary.cloudshareapi.document.PaymentTransaction;
import in.vikaschaudhary.cloudshareapi.dto.PaymentDTO;

public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String>{
   List<PaymentTransaction> findByClerkId(String clerkId);
   List<PaymentTransaction> findByClerkIdOrderByTransactionDateDesc(String clerkId);
   List<PaymentTransaction> findByClerkIdAndStatusOrderByTransactionDateDesc(String clerkId,String status);
}
