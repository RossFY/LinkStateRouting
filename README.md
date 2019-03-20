# LinkStateRouting

The goal of this project is to create a virtual network of routers that send routing data to one another. This assignment models the way that actual dynamic network routing is performed.</br>

Routers should be represented by an appropriately designed class or data type. Each router should maintain a data structure that stores references to other "directly" connected routers, which can be referenced by id, along with the cost of the link. These routers will exchange information necessary to build a routing table. Each router will be advertising access to a particular named network, which is a string that should be stored in the router class. You will need to store other information in the router class to enable the remainder of the algorithm to work, such as an undirected graph representing the network of routers as that router currently understands it.</br>

You will be implementing a "link state routing" algorithm. Link state routing algorithms work on real networks by sending a "link state packet" around the network, with routers forwarding the packet as appropriate. We will be simulating this packet with instances of another class, that will contain the following information:</br>
•	ID of the router that originates the LSP.</br>
•	A sequence number indicating how many LSPs have been sent by the originating router; i.e., each newly originated LSP has a higher sequence number than all previous LSPs. The first sequence number should be 1; ignore the possibility of wraparound.</br>
•	Time to live, decremented each time the LSP is forwarded; with an initial value of 10.</br>
•	Either of the following at your option:</br>
•	A list that indicates each reachable network (indicated by the network name stored in the router's string).</br>
•	A list that indicates each directly connected router, the network behind each one, and the cost to get to that router.</br>

The router class should contain a function/method named something similar to receivePacket that should take as its only arguments - (1) an instance of the link state packet class, (2) ID of the router that forwarded the LSP. A router that receives an LSP should first decrement the LSP's TTL. Next, the receiving router should discard the LSP and not use its information if either (1) the TTL has reached zero, or (2) the receiving router has already seen an LSP from the same originating router with a sequence number higher than or equal to the sequence number in the received LSP. If the LSP is not discarded, its information should be compared to the receiving router’s connectivity graph and the LSP should be sent out (in the form of function calls) to all directly connected routers except the one on which it was received.</br>

The router class should also contain a function named something similar to originatePacket that takes no arguments. This function will perform two functions. It should cause the router to generate an LSP packet based on the current state of the network as it understands it, and send it to all directly connected routers. Before it sends the packet, however, it should also increment a "tick" counter and consider if there are any directly connected routers from which it has not received a packet in 2 ticks. If that occurs, the router should alter its graph to reflect that the cost of the link to the other router is now infinity (or some arbitrarily huge number that you choose to represent infinity, if your language does not have a special infinity value.).</br>

Each router should maintain a "routing table" consisting of <network, outgoing link> triples. If connectivity has changed (as indicated by a received link state packet), the receiving router should run Dijkstra’s all-pairs shortest-path algorithm on its updated connectivity graph to re-compute its routing table of <network, outgoing link> triples. Outgoing link means the directly connected router to which this router would send data in order to route the data using the shortest path. It does not mean the destination router or the (Consider the advantages and disadvantages of implementing the graph using an adjacency matrix or using an adjacency list.)</br>

The initial network will be set up by reading in a file, infile.dat, that has the following format:</br>
[router-id] [network-name]</br>
 [directly-linked-router-id] [link-cost]</br>
 [directly-linked-router-id] [link-cost]</br>
 [directly-linked-router-id] [link-cost]</br>
 etc...</br>
[router-id] [network-name]</br>
 [directly-linked-router-id] [link-cost]</br>
 [directly-linked-router-id] [link-cost]</br>
 [directly-linked-router-id] [link-cost]</br>
 etc...</br>
etc...</br>

Each [bracketed] text above represents the name of a bit of data, and will not appear with brackets around it in the file. "etc..." represents that the pattern above it will be repeated. link-cost is optional and should be assumed to be 1 if missing
An example network, and picture representing that network, follows:</br>
 
0 155.246.80</br>
 1</br>
 3</br>
1 155.246.81</br>
 0</br>
 2</br>
 4</br>
2 155.246.82</br>
 1</br>
 3</br>
 5</br>
3 155.246.83</br>
 0</br>
 2</br>
 4</br>
 6</br>
4 155.246.84</br>
 1</br>
 3</br>
 5</br>
5 155.246.85</br>
 2</br>
 4</br>
 6</br>
6 155.246.86</br>
 3</br>
 5</br>
 
Note, there is no guarantee that the router id numbers will be sequential as they were in the above example. There could be, for example, a two-router setup with id 123 and 84719.</br>

Write a main function that proceeds as follows:</br>
•	At the beginning of the program, create routers and initialize their direct links by reading in infile.dat.</br>
•	Prompt the user if they would like to continue (enter "C"), quit (enter "Q"), print the routing table of a router ("P" followed by the router's id number), shut down a router (enter "S" followed by the id number), or start up a router (enter "T" followed by the id).</br>
•	If the user chooses to continue, you should call the originatePacket() function on every router in whatever order you choose. Then prompt again.</br>
•	If the user shuts down a router, change the router object so that it does not send out any LSP or do anything in response to originatePacket or receivePacket function calls.</br>
•	If the user starts up a router, change the router object so it once again behaves normally. (Routers initially are started, not stopped.)</br>
•	If the user chooses to print the routing table, display the table in the following format, and then prompt again:</br>
network, outgoing link</br>
network, outgoing link</br>
network, outgoing link</br>
network, outgoing link</br>
etc...</br>
•	If the user chooses to quit, exit the program.</br>

In any of the above file formats, all non-newline whitespace may be represented by one or more tab or space characters.</br>
