import java.util.HashMap;

public class LinkStatePacket {
	
	private String originateRouterId;
	private Integer sequenceNumber;
	private Integer ttl; //Time to live
	private HashMap<String, Boolean> reachableNetworkName; //network name and mark if it's visited
	
	public LinkStatePacket() {
		super();
	}

	public LinkStatePacket(String originateRouterId) {
		super();
		this.originateRouterId = originateRouterId;
		this.sequenceNumber = 1;
		this.ttl = 10;
		this.reachableNetworkName = new HashMap<String, Boolean>();
	}
	
	public String getOriginateRouterId() {
		return originateRouterId;
	}
	
	public void setOriginateRouterId(String originateRouterId) {
		this.originateRouterId = originateRouterId;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public Integer getTTL() {
		return ttl;
	}
	
	public void setTTL(Integer ttl) {
		this.ttl = ttl;
	}

	public HashMap<String, Boolean> getReachableNetworkName() {
		return reachableNetworkName;
	}
	
	public void setReachableNetworkName(HashMap<String, Boolean> reachableNetworkName) {
		this.reachableNetworkName = reachableNetworkName;
	}
	
}
