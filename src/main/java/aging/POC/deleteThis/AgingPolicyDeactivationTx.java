package aging.POC.deleteThis;

import org.springframework.transaction.annotation.Transactional;



@Transactional
public class AgingPolicyDeactivationTx {

	//public void deactivate(User user) {
	public void deactivate(Object user) {
		//if user is MRCH-associate
		//	if (user.hasProducts()) {
		//   if (user.hasActiveLicensee())
		//	  	user.getProducts.resubmit();
		//   else
		//    	user.getProducts().suspend();
		//   if (user.hasTasks())
		//	  	user.getTasks().cancel();
		//   }
		//
		//if user is PUB-associate
		//	if (user.hasProducts()) {
		//   	user.getProducts().reassign();
		//      if (user.hasTasks())
		//	  	  user.getTasks().reassign();
		//  }
		//
		//if user is a reviewer (PUB || MRCH)
		// 	user.getTasks().cancel();
		//if user is licensee (PUB || MRCH)
		// if (user.hasProducts()) {
		//	  user.getProducts().cancel();
		//    if (user.hasTasks())
		//      user.getProducts().cancel();
		// }
	}
	
	public void rollback() {
		
	}
}
