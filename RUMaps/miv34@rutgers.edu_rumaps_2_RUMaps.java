package rumaps;

import java.util.*;

/**
 * This class represents the information that can be attained from the Rutgers University Map.
 * 
 * The RUMaps class is responsible for initializing the network, streets, blocks, and intersections in the map.
 * 
 * You will complete methods to initialize blocks and intersections, calculate block lengths, find reachable intersections,
 * minimize intersections between two points, find the fastest path between two points, and calculate a path's information.
 * 
 * Provided is a Network object that contains all the streets and intersections in the map
 * 
 * @author Vian Miranda
 * @author Anna Lu
 */
public class RUMaps {
    
    private Network rutgers;

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Constructor for the RUMaps class. Initializes the streets and intersections in the map.
     * For each block in every street, sets the block's length, traffic factor, and traffic value.
     * 
     * @param mapPanel The map panel to display the map
     * @param filename The name of the file containing the street information
     */
    public RUMaps(MapPanel mapPanel, String filename) {
        StdIn.setFile(filename);
        int numIntersections = StdIn.readInt();
        int numStreets = StdIn.readInt();
        StdIn.readLine();
        rutgers = new Network(numIntersections, mapPanel);
        ArrayList<Block> blocks = initializeBlocks(numStreets);
        initializeIntersections(blocks);

        for (Block block: rutgers.getAdjacencyList()) {
            Block ptr = block;
            while (ptr != null) {
                ptr.setLength(blockLength(ptr));
                ptr.setTrafficFactor(blockTrafficFactor(ptr));
                ptr.setTraffic(blockTraffic(ptr));
                ptr = ptr.getNext();
            }
        }
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     * 
     * @param filename The name of the file containing the street information
     */
    public RUMaps(String filename) {
        this(null, filename);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Overloaded constructor for testing.
     */
    public RUMaps() { 
        
    }

    /**
     * Initializes all blocks, given a number of streets.
     * the file was opened by the constructor - use StdIn to continue reading the file
     * @param numStreets the number of streets
     * @return an ArrayList of blocks
     */
    public ArrayList<Block> initializeBlocks(int numStreets) {
        // WRITE YOUR CODE HERE

        ArrayList<Block> blocks =new ArrayList<>();
        for(int i=0;i<numStreets;i++){
            String streetname=StdIn.readLine();
            int numOfBlocks= StdIn.readInt();
            StdIn.readLine();
            for(int j=0; j<numOfBlocks;j++){
                int blockNumber=StdIn.readInt();
                StdIn.readLine();
                int numberOfPoints=StdIn.readInt();
                StdIn.readLine();
                double roadsize= StdIn.readDouble();
                StdIn.readLine();
                Block block= new Block(roadsize,streetname,blockNumber);
                    for(int k=0; k<numberOfPoints; k++){
                        
                        int x= StdIn.readInt();
                        int y= StdIn.readInt();
                        StdIn.readLine();
                        Coordinate c= new Coordinate(x, y);

                        if(k==0){
                            block.startPoint(c);
                        }
                        else{
                            block.nextPoint(c);
                        }


                    }
                    
                blocks.add(block);
                    
            }
            
        }
        return blocks;     
    }

    /**
     * This method traverses through each block and finds
     * the block's start and end points to create intersections. 
     * 
     * It then adds intersections as vertices to the "rutgers" graph if
     * they are not already present, and adds UNDIRECTED edges to the adjacency
     * list.
     * 
     * Note that .addEdge(__) ONLY adds edges in one direction (a -> b). 
     */
    public void initializeIntersections(ArrayList<Block> blocks) {
        // WRITE YOUR CODE HERE

        for(Block b :blocks){
            List<Coordinate> c= b.getCoordinatePoints();
            Coordinate startPoint= c.get(0);
            Coordinate endingPoint =c.get(c.size()-1);

            int r= rutgers.findIntersection(startPoint);
           int s= rutgers.findIntersection(endingPoint);

           if (r==-1){
            Intersection i =new Intersection(startPoint);
            b.setFirstEndpoint(i);
            rutgers.addIntersection(i);
            r = rutgers.getNextIndex() - 1;
           }

           if (s==-1){
            Intersection j= new Intersection(endingPoint);
            b.setLastEndpoint(j);
            rutgers.addIntersection(j);
            s = rutgers.getNextIndex() - 1;
           }

           if(r!=-1 ){
            Intersection currStart= rutgers.getIntersections()[r];
            b.setFirstEndpoint(currStart);
            
           } 
           
           if (s!=-1){

            Intersection currEnd=rutgers.getIntersections()[s];
            b.setLastEndpoint(currEnd);

           }

            Block copy =b.copy();
          
            copy.setFirstEndpoint(rutgers.getIntersections()[r]) ;
            copy.setLastEndpoint(rutgers.getIntersections()[s]);
            rutgers.addEdge(r,copy);
           
           Block rev =b.copy();
           rev.setFirstEndpoint(rutgers.getIntersections()[s]);
           rev.setLastEndpoint(rutgers.getIntersections()[r]);

            rutgers.addEdge(s, rev);

           }
       
}

    /**
     * Calculates the length of a block by summing the distances between consecutive points for all points in the block.
     * 
     * @param block The block whose length is being calculated
     * @return The total length of the block
     */
    public double blockLength(Block block) {
        // WRITE YOUR CODE HERE
        
        List<Coordinate> allPoints =block.getCoordinatePoints();
        double sum=0.0;

        for (int i=0; i<allPoints.size()-1;i++){
            
            Coordinate c1=allPoints.get(i);
            Coordinate c2=allPoints.get(i+1);
            
           sum =sum + coordinateDistance(c1, c2);
        }
        return sum;
    }

    /**
     * Use a DFS to traverse through blocks, and find the order of intersections
     * traversed starting from a given intersection (as source).
     * 
     * Implement this method recursively, using a helper method.
     */
    public ArrayList<Intersection> reachableIntersections(Intersection source) {
        // WRITE YOUR CODE HERE

          ArrayList<Intersection> outputOfIntersections =new ArrayList<>();
          boolean[]v=new boolean[rutgers.getIntersections().length];
          int startingPoint= findI(source);
          if (startingPoint!=-1){
          dfs(source,v,outputOfIntersections);
          }
          return outputOfIntersections;
    }
        
            private int findI(Intersection target) {
                Coordinate targetCoord = target.getCoordinate();
                Intersection[] intersections = rutgers.getIntersections();

                for (int i = 0; i < intersections.length; i++) {
                    if (intersections[i] != null && 
                        intersections[i].getCoordinate().equals(targetCoord)) {
                        return i;
                    }
                }
                return -1;
            
        }
          
         private void dfs(Intersection current, boolean[] v, ArrayList<Intersection> r) {
            
            int ptr=findI(current);
            v[ptr] = true;
            r.add(rutgers.getIntersections()[ptr]);
        
            
            Block n=rutgers.adj(ptr) ;       
            while(n!=null){
                Intersection currentIntersection =n.other(current);
                int nIndex=findI(currentIntersection);

                if (nIndex != -1 && !v[nIndex]) {
                    dfs(currentIntersection, v, r);
                }
                
                if(!v[nIndex]){
                    dfs(currentIntersection,v,r);
                }
                n=n.getNext();
            }
        }
     

    /**
     * Finds and returns the path with the least number of intersections (nodes) from the start to the end intersection.
     * 
     * - If no path exists, return an empty ArrayList.
     * - This graph is large. Find a way to eliminate searching through intersections that have already been visited.
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least number of turns, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> minimizeIntersections(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        ArrayList<Intersection> pathWithLeastIntersections= new ArrayList<>();
        Intersection [] edgeTo= new Intersection[rutgers.getIntersections().length];
        boolean[]v=new boolean[rutgers.getIntersections().length];

        Queue<Intersection> queue = new Queue<>();
       
        int startingPositon =findI(start);
        int endPostion=findI(end);

        if(startingPositon==-1||endPostion==-1){
            return pathWithLeastIntersections;
        }
        
         
            queue.enqueue(start);
            v[startingPositon] = true;
        
            
            while (!queue.isEmpty()) {
               
                Intersection current =  queue.dequeue();
                int currIndex= findI(current);
        
                
                Block neighboringBlock= rutgers.adj(currIndex);
                
                while(neighboringBlock!=null) {
                   
                    Intersection neighbor = neighboringBlock.other(current);
                    int neighborIndex=findI(neighbor);
                    
                    if (!v[neighborIndex]) {
                        queue.enqueue(neighbor);
                        v[neighborIndex] = true;
                        edgeTo[neighborIndex]=current;
                    }
                    neighboringBlock=neighboringBlock.getNext();
                }
            }

        Intersection curr = end;
            while (curr!=null ){
                pathWithLeastIntersections.add(curr);
                int currIndex = findI(curr);
                curr= edgeTo[currIndex];
            }
            Collections.reverse(pathWithLeastIntersections);
            return pathWithLeastIntersections; 
    }

    /**
     * Finds the path with the least traffic from the start to the end intersection using a variant of Dijkstra's algorithm.
     * The traffic is calculated as the sum of traffic of the blocks along the path.
     * 
     * What is this variant of Dijkstra?
     * - We are using traffic as a cost - we extract the lowest cost intersection from the fringe.
     * - Once we add the target to the done set, we're done. 
     * 
     * @param start The starting intersection
     * @param end The destination intersection
     * @return The path with the least traffic, or an empty ArrayList if no path exists
     */
    public ArrayList<Intersection> fastestPath(Intersection start, Intersection end) {
        // WRITE YOUR CODE HERE
        
        int numberOfVertices=rutgers.getIntersections().length;
       
        ArrayList<Intersection> done= new ArrayList<>();
        Intersection [] pred= new Intersection[numberOfVertices];
        double [] d =new double[numberOfVertices];
        boolean[]visited =new boolean[numberOfVertices];
        ArrayList<Intersection>fringe=new ArrayList<>();

        
        int startingPositon =findI(start);
        int endPostion=findI(end);

        if(startingPositon==-1||endPostion==-1){
            return done;
        }

        for (int i = 0; i <numberOfVertices; i++) {
            if(i!=startingPositon){
                d[i] = Double.POSITIVE_INFINITY;
                pred[i]=null;
            }
        }

        d[startingPositon]=0;
        pred[startingPositon] = null;
        fringe.add(start);
        
         
            while (!fringe.isEmpty()) {
                
                Intersection minIntersection = fringe.get(0);
                int minIndex = findI(minIntersection);

                for (int i = 0; i < fringe.size(); i++) {
                    Intersection curr = fringe.get(i);
                    int currIndex = findI(curr);
                    if (d[currIndex] < d[minIndex]) {
                        minIntersection = curr;
                        minIndex = currIndex;
                    }
                }
                fringe.remove(minIntersection);
                done.add(minIntersection);
                visited[minIndex]=true;
                
                Block neighboringBlock= rutgers.adj(minIndex);
                
                while(neighboringBlock!=null) {
                   
                    Intersection neighbor = neighboringBlock.other(minIntersection);
                    int neighborIndex=findI(neighbor);
                    if (neighborIndex == -1) {
                        neighboringBlock = neighboringBlock.getNext();
                        continue;
                    }
                    
                    double w =neighboringBlock.getTraffic();
                    
                    if (d[neighborIndex]==Double.POSITIVE_INFINITY){
                        d[neighborIndex]=d[minIndex]+w;
                        fringe.add(neighbor);
                        pred[neighborIndex]=minIntersection;
                    }
                    else if (d[neighborIndex]>(d[minIndex]+w)){
                        d[neighborIndex]=d[minIndex]+w;
                        pred[neighborIndex]=minIntersection;
                    }
                    neighboringBlock=neighboringBlock.getNext();
                }
            }

            Intersection curr = end;
            ArrayList<Intersection> path=new ArrayList<>();
            while (curr!=null && !curr.equals(start) ){
                path.add(curr);
                int currIndex = findI(curr);
                if (currIndex == -1 || pred[currIndex] == null) {
                    return new ArrayList<>(); 
                }
                
                curr= pred[currIndex];
            }
            path.add(start);
            Collections.reverse(path);
            return path; 

    }
    
    
  

    /**
     * Calculates the total length, average experienced traffic factor, and total traffic for a given path of blocks.
     * 
     * You're given a list of intersections (vertices); you'll need to find the edge in between each pair.
     * 
     * Compute the average experienced traffic factor by dividing total traffic by total length.
     *  
     * @param path The list of intersections representing the path
     * @return A double array containing the total length, average experienced traffic factor, and total traffic of the path (in that order)
     */
    public double[] pathInformation(ArrayList<Intersection> path) {
        // WRITE YOUR CODE HERE
        
        double totalLength = 0; 
        double totalTrafficFactor = 0; 
        double totalTraffic = 0; 
    
        for (int i = 0; i < path.size()-1; i++) {
            Intersection intOne = path.get(i);
            Intersection intTwo = path.get(i + 1);
    
            int indexA = findI(intOne);
            if (indexA == -1) {
                continue;
            }
    
            Block currBlock = rutgers.adj(indexA);
            while (currBlock != null) {
                Intersection firstIntersection = currBlock.getFirstEndpoint();
                Intersection destinationIntersection = currBlock.getLastEndpoint();
    
                if ((firstIntersection.equals(intOne) && destinationIntersection.equals(intTwo)) || (firstIntersection.equals(intTwo) && destinationIntersection.equals(intOne))) {
                    totalLength = totalLength+ currBlock.getLength();
                    totalTraffic = totalTraffic+ currBlock.getTraffic();
                   
                    break; 
                }
    
                currBlock = currBlock.getNext();
            }
        }

        totalTrafficFactor = totalTraffic / totalLength;

        return new double[] { totalLength, totalTrafficFactor, totalTraffic };
    }


    /**
     * Calculates the Euclidean distance between two coordinates.
     * PROVIDED - do not modify
     * 
     * @param a The first coordinate
     * @param b The second coordinate
     * @return The Euclidean distance between the two coordinates
     */
    private double coordinateDistance(Coordinate a, Coordinate b) {
        // PROVIDED METHOD

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * **DO NOT MODIFY THIS METHOD**
     * 
     * Calculates and returns a randomized traffic factor for the block based on a Gaussian distribution.
     * 
     * This method generates a random traffic factor to simulate varying traffic conditions for each block:
     * - < 1 for good (faster) conditions
     * - = 1 for normal conditions
     * - > 1 for bad (slower) conditions
     * 
     * The traffic factor is generated with a Gaussian distribution centered at 1, with a standard deviation of 0.2.
     * 
     * Constraints:
     * - The traffic factor is capped between a minimum of 0.5 and a maximum of 1.5 to avoid extreme values.
     * 
     * @param block The block for which the traffic factor is calculated
     * @return A randomized traffic factor for the block
     */
    public double blockTrafficFactor(Block block) {
        double rand = StdRandom.gaussian(1, 0.2);
        rand = Math.max(rand, 0.5);
        rand = Math.min(rand, 1.5);
        return rand;
    }

    /**
     * Calculates the traffic on a block by the product of its length and its traffic factor.
     * 
     * @param block The block for which traffic is being calculated
     * @return The calculated traffic value on the block
     */
    public double blockTraffic(Block block) {
        // PROVIDED METHOD
        
        return block.getTrafficFactor() * block.getLength();
    }

    public Network getRutgers() {
        return rutgers;
    }




    
    








}
