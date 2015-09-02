package com.disney.opa.service;



import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.disney.opa.entity.OPAProperty;

//import com.googlecode.ehcache.annotations.Cacheable;

@Service
public class UtilService {

    //@Autowired
    //private TextKeyDao textKeyDao;
    
    //@Autowired
    //private UtilDao utilDao;       


    //@Autowired
    //private CacheUtils cacheUtils;    
    
	
    public String testDataAccess() throws Exception {
		final Logger log = Logger.getLogger(this.getClass().getName());
		log.info("......begin");

        try {
        	log.debug("Logging out testDataAccess");
/*            List<DownloadQueue> queueRecs = downloadQueueDao.fetchRecordsByWatcherID(download_watcher_id, this.max_rows);
			log.debug("queueRecs.size = " + queueRecs.size());
			for (DownloadQueue rec : queueRecs) {
				log.debug("queue record .id = " + rec.getQueue_id());
				log.debug("timestamp = " + rec.getQueue_date());
			}
*/			
			
            return "success";

        } catch (Exception ex) {
            log.error("Exception: " + ex + " stacktrace: " + ex.getMessage());
            throw ex;
        } finally {
            log.info("........end");
        }
    }
	
	//@Cacheable(cacheName="textKeyCache")
	@Transactional
    public HashMap<String,String> getTextKeys(Long languageId) throws Exception {
		final Logger log = Logger.getLogger(this.getClass().getName());
		log.info("......begin");
		//List<TextKey> textItems;
		HashMap<String,String> hashTextKeys = new HashMap<String,String>();
        //try {
        	//textItems = textKeyDao.fetchTextKeysByLanguageId(languageId);
        	//for (TextKey tk : textItems) {
        	//	hashTextKeys.put(tk.getTextKey(), tk.getTextTranslation());
        	//}
        	
            return hashTextKeys;
        //} catch (Exception ex) {
          //  log.error("Exception: " + ex + " stacktrace: " + Utils.getStackTrace(ex));
         //   throw ex;
       // } finally {
       //     log.info("........end");
       // }
    }	

	
	
	//@Cacheable(cacheName="OPAPropertyByName")
	@Transactional
    public OPAProperty getOPAPropertyByName(String name) throws Exception {
		final Logger log = Logger.getLogger(this.getClass().getName());
		log.info("......begin");
		OPAProperty property = null;
        try {
        	//property = utilDao.getOPAPropertyByName(name);
            return property;
        } catch (Exception ex) {
            log.error("Exception: " + ex + " stacktrace: " + ex.getMessage());
            throw ex;
        } finally {
            log.info("........end");
        }
    }	
	
}
