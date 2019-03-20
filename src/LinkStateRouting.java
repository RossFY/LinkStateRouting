import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
* @author Linlei Lie - 10431934 
* 		  Ye Fang - 10431002
* 		  Bolin Chen - 10427071
* @date Nov 20, 2018 
* @version 
*/
public class LinkStateRouting {

	
	public static void main(String[] args) {
		init();
	}
	
	public static void init() {
		System.out.println("==========Link State Routing==========");
		Scanner scanner=new Scanner(System.in);
		LinkedHashMap<String, Router> network = new LinkedHashMap<String, Router>();
		
		try {
			network = initNetwork();
			initRoutingTable(network);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
        } catch (IOException e) {
        	System.out.println("Cannot read or write file.");
        } catch (Exception e) {
			System.out.println("Unknown mistake.");
        }
		
		String input = "";
		do{
			try {
				System.out.println("\nChoose one of the following things: ");
				System.out.println("Continue (enter \"C\")");
				System.out.println("Print the routing table of a router (enter \"P\" followed by the router's id)");
				System.out.println("sTart up a router (enter \"T\" followed by the router's id).");
				System.out.println("Shut down a router (enter \"S\" followed by the router's id)");
				System.out.println("Quit (enter \"Q\")\n");
				
				input = scanner.nextLine();
				String checkResult= inputFormatCheck(input, network);
				String choice = checkResult.split("\\s+")[0];
				String routerId = checkResult.split("\\s+")[1];
				
				switch (choice) {
					case "C":
					case "c":
						originateLSP(network);
						break;
					case "P":
					case "p":
						printRouteTable(routerId, network);	
						break;
					case "T":
					case "t":
						startRouter(routerId, network);
						break;
					case "S":
					case "s":
						shutDownRouter(routerId, network);
						break;
					case "Q":
					case "q":
					default:
						break;
				}

			} catch (InvalidInputException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
//				System.out.println("Unknown mistake.");
				e.printStackTrace();
	        }
		}while(!"Q".equalsIgnoreCase(input));
		scanner.close();
	}
	
	private static LinkedHashMap<String, Router> initNetwork() 
			throws FileNotFoundException, IOException, Exception {
		String inputPath = System.getProperty("user.dir") + "/" + "infile.dat";
		BufferedReader reader = null;
		LinkedHashMap<String, Router> network = new LinkedHashMap<String, Router>();
		Router router = null;
		try {
			reader = new BufferedReader(new FileReader(inputPath));
			String line = new String();
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				if (line.matches("\\d+\\s(\\d+.)+\\d+")) {
					String[] routerInfo = line.trim().split("\\s+");
					router = new Router(routerInfo[0], routerInfo[1]);
					network.put(router.getRouterId(), router);
					router.setNetworkGraph(network);
					continue;
				}
				if(line.matches("\\d+") && router != null){
					network.get(router.getRouterId()).getConnectedRouters().put(line.trim(), 1);
					continue;
				}
				if(line.matches("\\d+\\s+\\d+") && router != null){
					String[] connRouterInfo = line.trim().split("\\s+");
					network.get(router.getRouterId()).getConnectedRouters().put(connRouterInfo[0], Integer.parseInt(connRouterInfo[1]));
					continue;
				}
			}
		} finally {
			try {
                if (reader != null) {
                    reader.close();
                }
            }catch (IOException e) {
            	System.out.println("File can not be closed");
            }
		} 
		
		return network;
	}
	
	private static void initRoutingTable(LinkedHashMap<String, Router> network) {
		for(Router router : network.values()) {
			for(Router r: network.values()) {
				if (router.getRouterId().equals(r.getRouterId())) {
					continue;
				}
				if (router.getConnectedRouters().containsKey(r.getRouterId())) {
					router.getRoutingTable().add(new RoutingTable(r.getNetworkName(), r.getNetworkName(), router.getConnectedRouters().get(r.getRouterId())));
				} else {
					router.getRoutingTable().add(new RoutingTable(r.getNetworkName(), "", Integer.MAX_VALUE));
				}
			}
			
			LinkedHashMap<String, Router> networkGraph = network;
			router.setNetworkGraph(networkGraph);
		}
	}
	
	private static String inputFormatCheck(String input, LinkedHashMap<String, Router> network)
			throws InvalidInputException {
		input = input.trim();
		if (input.matches("[CcPpTtSsQq]")) {
			return input;
		} else if (input.matches("[CcPpTtSsQq]\\s+\\d+")) {
			if (!network.containsKey(input.split("\\s+")[1])) {
				throw new InvalidInputException("Wrong router id.");
			}
		} else if (input.matches("[CcPpTtSsQq]\\d+")) {
			if (!network.containsKey(input.substring(1))) {
				throw new InvalidInputException("Wrong router id.");
			}
		}else {
			throw new InvalidInputException("Invalid input.");
		}
		return input.substring(0, 1) + " " + input.substring(1);
	}
	
	private static void originateLSP(LinkedHashMap<String, Router> network) {
		for(Router r : network.values()){
			if(r.getRouterState()) {
				r.originatePacket();
			}
		}
	}

	private static void printRouteTable(String routerId, LinkedHashMap<String, Router> network) {
		Router router = network.get(routerId);
		System.out.println();
		System.out.format("%-15s%-20s%-15s", "Destination", "Outgoing Link", "Cost");
		System.out.println();
		for(RoutingTable rTable: router.getRoutingTable()) {
			System.out.format("%-15s%-20s%-15s", rTable.getNetworkName(), rTable.getOutgoingLink(), rTable.getCost());
			System.out.println();
		}
		System.out.println();
	}
	
	private static void startRouter(String routerId, LinkedHashMap<String, Router> network) {
		network.get(routerId).setRouterState(true);
		network.get(routerId).originatePacket();
		System.out.println("the router " + routerId + " starts.");
	}
	
	private static void shutDownRouter(String routerId, LinkedHashMap<String, Router> network) {
		network.get(routerId).setRouterState(false);
		System.out.println("the router " + routerId + " is shut down.");
	}
}

class InvalidInputException extends Exception {	
	private static final long serialVersionUID = 1L;
	private int idnumber;				

	public InvalidInputException(String string) {
		super(string);
	}

	public int getId() {
		return idnumber;
	}
}