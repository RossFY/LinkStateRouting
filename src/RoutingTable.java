public class RoutingTable {
	
	private String networkName; //destination router name
	private String outgoingLink; //next router name
	private Integer cost; //cost to next router
	private Integer originalCost; //save cost in order to recover
	
	public RoutingTable(){

	}
	
	public RoutingTable(String networkName, String outgoingLink, Integer cost){
		this.networkName = networkName;
		this.outgoingLink = outgoingLink;
		this.cost = cost;	
		this.originalCost = cost;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getOutgoingLink() {
		return outgoingLink;
	}

	public void setOutgoingLink(String outgoingLink) {
		this.outgoingLink = outgoingLink;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	public Integer getOriginalCost() {
		return originalCost;
	}

	public void setOriginalCost(Integer originalCost) {
		this.originalCost = originalCost;
	}
	
}
