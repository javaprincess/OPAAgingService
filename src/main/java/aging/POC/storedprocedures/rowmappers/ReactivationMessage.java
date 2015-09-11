package aging.POC.storedprocedures.rowmappers;

public class ReactivationMessage {

	private Integer productId;
	private String status;
	private String message;
	
	public Integer getProductId() {
		return productId;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
