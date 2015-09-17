package com.disney.compliance.hateaos.resources;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.disney.compliance.aging.POC.enforcers.AgedUserEntryRepository;
import com.disney.compliance.aging.POC.queue.entry.AgedUserEntry;

@RestController
@RequestMapping("/deactivationReport")
public class OPAComplianceRestController {

		@Resource(name="agedUserEntryRepository")
	    private AgedUserEntryRepository agedUserEntryRepository;

		@Autowired
		OPAComplianceRestController(AgedUserEntryRepository agedUserEntryRepository) {
		        this.agedUserEntryRepository = agedUserEntryRepository;
		       
		}


	    @RequestMapping(method = RequestMethod.GET)
	    //ResponseEntity<?> reactivate(@PathVariable String userId) {
	    OPAComplianceResource listDeactivatedUsers(@PathVariable String userId) {

	    	
	    	List<AgedUserEntry> deactivatedUsersList;
	    	
	        this.validateUser(userId);

	        deactivatedUsersList = agedUserEntryRepository.findByUserId(new Long(userId));
	        
	                /*.map(account -> {
	                            Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

	                            HttpHeaders httpHeaders = new HttpHeaders();

	                            Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
	                            httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

	                            return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	                        }
	                ).get();*/
	        
	        return new OPAComplianceResource(deactivatedUsersList);
	    }

	    @RequestMapping(value = "/reactivate/{userId}", method = RequestMethod.GET)
	    AgedUserEntry reactivate(@PathVariable String userId) {
	        this.validateUser(userId);
	       // return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId));
	        return null;
	    }


	    /*@RequestMapping(method = RequestMethod.GET)
	    Resources<BookmarkResource> readBookmarks(@PathVariable String userId) {

	        this.validateUser(userId);

	        List<BookmarkResource> bookmarkResourceList = bookmarkRepository.findByAccountUsername(userId)
	                .stream()
	                .map(BookmarkResource::new)
	                .collect(Collectors.toList());
	        return new Resources<BookmarkResource>(bookmarkResourceList);
	    }

	    @Autowired
	    BookmarkRestController(BookmarkRepository bookmarkRepository) {
	        this.bookmarkRepository = bookmarkRepository;
	       
	    }*/

	    private void validateUser(String userId) {
	        this.agedUserEntryRepository.findByUserId(new Long(userId));
	                //.orElseThrow(() -> new UserNotFoundException(userId));
	    }	
}
