package aging.POC.enforcers;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import aging.POC.User;
import aging.POC.queue.entry.AgedUserEntry;
import aging.POC.storedprocedures.rowmappers.AgedUser;

@Document(collection="AgedUserEntryRepository")
public interface AgedUserEntryRepository extends MongoRepository<AgedUserEntry, String> {

	
	public List<AgedUserEntry> findByJob(String job);
    public AgedUserEntry findById(long id);
    
    @Query("{ 'jsonData.user.userId' : ?0 }")
    public List<User> findByUserId(long userId);
  
    @Query("{ 'jsonData.user.notificationFlag' :  ?0  }")
    public List<AgedUser> findAllAgingCandidatesByAge(int age);
    
}
