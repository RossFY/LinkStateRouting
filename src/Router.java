import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Router {
	
	private String routerId;
	private String networkName;
	private boolean routerState; //if router is on or off
	private HashMap<String, Integer> connectedRouters; //conn router's id, cost of link
	private LinkedList<RoutingTable> routingTable; //networkName, outgoingLink, cost
	private LinkedHashMap<String, Router> networkGraph; //router id, router
	private Integer lspSequenceNumber; //sequence of originate lsp
	private HashMap<String, Integer> receivedLSP; //originateRouterId, sequenceNumber
	private HashMap<String, Integer> tick; //conn router's id, tick
		
	public Router() {
		super();
	}
	
	public Router(String routerId, String networkName) {
		this.routerId = routerId;
		this.networkName = networkName;
		this.routerState = true;
		this.connectedRouters = new HashMap<>();
		this.routingTable = new LinkedList<RoutingTable>();
		this.networkGraph = new LinkedHashMap<String, Router>();
		this.lspSequenceNumber = 1;
		this.receivedLSP = new HashMap<>();
		this.tick = new HashMap<>();
	}

	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public boolean getRouterState() {
		return routerState;
	}

	public void setRouterState(boolean routerState) {
		this.routerState = routerState;
	}

	public HashMap<String, Integer> getConnectedRouters() {
		return connectedRouters;
	}

	public void setConnectedRouters(HashMap<String, Integer> connectedRouters) {
		this.connectedRouters = connectedRouters;
	}

	public LinkedList<RoutingTable> getRoutingTable() {
		return routingTable;
	}

	public void setRoutingTable(LinkedList<RoutingTable> routingTable) {
		this.routingTable = routingTable;
	}
	
	public LinkedHashMap<String, Router> getNetworkGraph() {
		return networkGraph;
	}

	public void setNetworkGraph(LinkedHashMap<String, Router> networkGraph) {
		this.networkGraph = networkGraph;
	}

	public Integer getLSPSequenceNumber() {
		return lspSequenceNumber;
	}

	public void setLSPSequenceNumber(Integer lspSequenceNumber) {
		this.lspSequenceNumber = lspSequenceNumber;
	}

	public HashMap<String, Integer> getReceivedLSP() {
		return receivedLSP;
	}

	public void setReceivedLSP(HashMap<String, Integer> receivedLSP) {
		this.receivedLSP = receivedLSP;
	}

	public HashMap<String, Integer> getTick() {
		return tick;
	}

	public void setTick(HashMap<String, Integer> tick) {
		this.tick = tick;
	}
	
	public void originatePacket() {
	
		if(this.getRouterState() == false) {
			return;
		}
		
		LinkStatePacket lsp = new LinkStatePacket(this.getRouterId());
		
		lsp.setSequenceNumber(this.getLSPSequenceNumber());
		
		HashMap<String, Boolean> reachableNetwork = new HashMap<String, Boolean>();
		for(String string: this.getNetworkGraph().keySet()) {
			reachableNetwork.put(this.getNetworkGraph().get(string).getNetworkName(), false);
		}
		lsp.setReachableNetworkName(reachableNetwork);

		for(String string: this.getConnectedRouters().keySet()) {
			HashMap<String, Integer> result = this.getNetworkGraph().get(string).receivePacket(lsp, this.getRouterId());
			
			Iterator<Entry<String, Integer>> iterator = result.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> entry = (HashMap.Entry<String, Integer>) iterator.next();
				if (entry.getValue() == 200) {
					lsp.getReachableNetworkName().replace(entry.getKey(), true);
				}
				if (entry.getValue() == 500) {
					if (this.getTick().get(entry.getKey()) != null) {
						this.getConnectedRouters().remove(entry.getKey());
						dijkstra(this.getNetworkGraph());
					}
					this.getTick().put(entry.getKey(), 1);
				}
			}
		}
		
		this.setLSPSequenceNumber(this.getLSPSequenceNumber()+1);
	}
	
	public HashMap<String, Integer> receivePacket(LinkStatePacket lsp, String forwardedRouterId) {
		
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put(this.getNetworkName(), 204);
		
		if(this.getRouterState() == false) {
			result.replace(this.getNetworkName(), 500);
			return result;
		}
		
		lsp.setTTL(lsp.getTTL() - 1);
		
		if (lsp.getTTL() == 0) {
			result.replace(this.getNetworkName(), 204);
			return result;
		}
		
		if(this.getReceivedLSP().containsKey(lsp.getOriginateRouterId())){
			if(this.getReceivedLSP().get(lsp.getOriginateRouterId()) >= lsp.getSequenceNumber()){
				result.replace(this.getNetworkName(), 204);
				return result;
			}else{
				this.getReceivedLSP().put(lsp.getOriginateRouterId(), lsp.getSequenceNumber());
			}
		}else{
			this.getReceivedLSP().put(lsp.getOriginateRouterId(), lsp.getSequenceNumber());
		}
		
		if (lsp.getReachableNetworkName().size() != this.getNetworkGraph().size()) {
			dijkstra(this.getNetworkGraph());
		}
		
		if (lsp.getReachableNetworkName().values().contains(false)) {
			for(String string: this.getConnectedRouters().keySet()) {
				this.getNetworkGraph().get(string).receivePacket(lsp, this.getRouterId());
			}
		} else {
			result.replace(this.getNetworkName(), 200);
			return result;
		}
		
		return result;
	}
	
	public void dijkstra(LinkedHashMap<String, Router> networkGraph) {
		LinkedList<RoutingTable> routingTable = new LinkedList<RoutingTable>();
		Router currentRouter;
		String shortestConnRouterId;
		Integer cost;
//		Set<String> key = new HashSet<String>(networkGraph.keySet());while(key.size() >1){
//			String closestRouterId = "";
//			Integer shortestDistance = Integer.MAX_VALUE;
//			
//			for(String s : key){
//				for(RoutingTable rTable: routingTable) {
//					if (rTable.getCost() < shortestDistance) {
//						shortestDistance = rTable.getCost();
//						shortestConnRouterId = s;
//					}
//				}
//			}
//			
////			key.remove(shortestConnRouterId);
////			currentRouter = networkGraph.get(shortestConnRouterId);
//			
//			Iterator<Entry<String, Integer>> iterator = currentRouter.getConnectedRouters().entrySet().iterator();
//			while (iterator.hasNext()) {
//				Entry<String, Integer> entry = (HashMap.Entry<String, Integer>) iterator.next();
//				String connRouterId = entry.getKey();
//				Integer connRouterCost = entry.getValue();
//				if(key.contains(connRouterId)){
////					if((connRouterCost + cost) < routingTable.getCost()){
////						connRouterCost = connRouterCost + cost;
////						routingTable.add(new RoutingTable("", connRouterId, connRouterCost));
////					}
//				}
//			}
//		}		
		
		networkGraph.get(routerId).setRoutingTable(routingTable);
	}
	
}