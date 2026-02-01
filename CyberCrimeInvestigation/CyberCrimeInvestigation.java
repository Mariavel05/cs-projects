package investigation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.jar.Attributes.Name; 

/*  
 * This class represents a cyber crime investigation.  It contains a directory of hackers, which is a resizing
 * hash table. The hash table is an array of HNode objects, which are linked lists of Hacker objects.  
 * 
 * The class contains methods to add a hacker to the directory, remove a hacker from the directory.
 * You will implement these methods, to create and use the HashTable, as well as analyze the data in the directory.
 * 
 * @author Colin Sullivan
 */
public class CyberCrimeInvestigation {
       
    private HNode[] hackerDirectory;
    private int numHackers = 0; 

    public CyberCrimeInvestigation() {
        hackerDirectory = new HNode[10];
    }

    /**
     * Initializes the hacker directory from a file input.
     * @param inputFile
     */
    public void initializeTable(String inputFile) { 
        // DO NOT EDIT
        StdIn.setFile(inputFile);  
        while(!StdIn.isEmpty()){
            addHacker(readSingleHacker());
        }
    }

    /**
     * Reads a single hackers data from the already set file,
     * Then returns a Hacker object with the data, including 
     * the incident data.
     * 
     * StdIn.setFile() has already been called for you.
     * 
     * @param inputFile The name of the file to read hacker data from.
     */
     public Hacker readSingleHacker(){ 
        // WRITE YOUR CODE HERE
        String name=StdIn.readLine();
        String ipAddHash=StdIn.readLine();
        String location=StdIn.readLine();
        String os=StdIn.readLine();
        String webServer=StdIn.readLine();
        String date=StdIn.readLine();
        String urlHash=StdIn.readLine();
        
        Incident newIncident =new Incident(os, webServer, date, location, ipAddHash,urlHash);
        Hacker h=new Hacker(name);
        h.addIncident(newIncident);
    
        return h;
    }

    /**
     * Adds a hacker to the directory.  If the hacker already exists in the directory,
     * instead adds the given Hacker's incidents to the existing Hacker's incidents.
     * 
     * After a new insertion (NOT if a hacker already exists), checks if the number of 
     * hackers in the table is >= table length divided by 2. If so, calls resize()
     * 
     * @param toAdd
     */
    public void addHacker(Hacker toAdd) {
        // WRITE YOUR CODE HERE
        int totalHackers=numHackers;
        int indexPos=toAdd.hashCode()% hackerDirectory.length;
        HNode firstHacker=new HNode(toAdd);
        // check linked list 
        if(hackerDirectory[indexPos]==null){
           hackerDirectory[indexPos] = firstHacker;
            numHackers++;
        }
        else if(hackerDirectory[indexPos]!=null){
            HNode curr=hackerDirectory[indexPos];
            while(curr!=null){
                if(curr.getHacker().equals(toAdd)){
                    curr.getHacker().getIncidents().addAll( toAdd.getIncidents() );
                    break;
                    }
                
                
                if (curr.getNext()==null&& !curr.getHacker().equals(toAdd)){
                curr.setNext(firstHacker);
                numHackers++;
                break;
                }
                curr=curr.getNext();
                }
            }
                 if(numHackers>=hackerDirectory.length/2 && totalHackers!=numHackers){
                    resize();
                 }
                
    }
           
       
        
    

    /**
     * Resizes the hacker directory to double its current size.  Rehashes all hackers
     * into the new doubled directory.
     */
    private void resize() {
        // WRITE YOUR CODE HERE 
        HNode [] temp = hackerDirectory ;
        hackerDirectory= new HNode[2*temp.length] ;
        numHackers=0;

        for (int i = 0; i < temp.length; i++) {
            HNode ptr=temp[i];
            while(ptr != null) {
                addHacker(ptr.getHacker());
                ptr=ptr.getNext();
            }
        }

        
    }
    

    /**
     * Searches the hacker directory for a hacker with the given name.
     * Returns null if the Hacker is not found
     * 
     * @param toSearch
     * @return The hacker object if found, null otherwise.
     */
    public Hacker search(String toSearch) {
        // WRITE YOUR CODE HERE 
    int indexPosition = Math.abs(toSearch.hashCode()) % hackerDirectory.length;
    HNode ptr = hackerDirectory[indexPosition];

    while (ptr != null) {
        if (ptr.getHacker().getName().equals(toSearch)) {
            return ptr.getHacker();
        }
        ptr = ptr.getNext();
    }

    return null;
}

    /**
     * Removes a hacker from the directory.  Returns the removed hacker object.
     * If the hacker is not found, returns null.
     * 
     * @param toRemove
     * @return The removed hacker object, or null if not found.
     */
    public Hacker remove(String toRemove) {
        // WRITE YOUR CODE HERE 
        Hacker del =search(toRemove);
        if(del==null){
            return null;
        }
            int index = Math.abs(toRemove.hashCode()) % hackerDirectory.length;
            HNode curr=hackerDirectory[index];
            HNode prev=null;
            while( curr!=null ){
               
                if (curr.getHacker().getName().equals(toRemove)){ //checks if its the element to remove
                    
                    if (prev!=null){ //its not first element 
                        prev.setNext((curr.getNext()));// skips current node by pointing previous to curr.getNext()
                    }
                    else { // if it is the first element 
                       hackerDirectory[index]=curr.getNext();// need to change head to the second element
                    }
                    
                   
                        numHackers--;
                        return del;
                    
                }
            
                prev=curr; // save curr as  pervious
                curr=curr.getNext();   
        }
                    
       
    return null;
}


    /**
     * Merges two hackers into one based on number of incidents.
     * 
     * @param hacker1 One hacker
     * @param hacker2 Another hacker to attempt merging with
     * @return True if the merge was successful, false otherwise.
     */
    public boolean mergeHackers(String hacker1, String hacker2) {  
        // WRITE YOUR CODE HERE 
        Hacker hackerA=search(hacker1);
        Hacker hackerB=search(hacker2);
        
        if(hackerA==null||hackerB==null){
            return false;
        }
       
            if(hackerA.numIncidents()<hackerB.numIncidents()){
                hackerB.getIncidents().addAll(hackerA.getIncidents());
                hackerB.getAliases().add(hackerA.getName());
                remove(hacker1);
                return true; 
            }
            else {
                hackerA.getIncidents().addAll(hackerB.getIncidents());
                hackerA.getAliases().add(hackerB.getName());
                remove(hacker2);
                return true;
                
            }
         }

    /**
     * Gets the top n most wanted Hackers from the directory, and
     * returns them in an arraylist. 
     * 
     * You should use the provided MaxPQ class to do this. You can
     * add all hackers, then delMax() n times, to get the top n hackers.
     * 
     * @param n
     * @return Arraylist containing top n hackers
     */
    public ArrayList<Hacker> getNMostWanted(int n) {
        // WRITE YOUR CODE HERE 
        
        if(n<=0){
            return null; 
        }
       MaxPQ<Hacker> m= new MaxPQ<>();
       
       for(int i=0; i<hackerDirectory.length;i++){
        HNode curr= hackerDirectory[i];
        while(curr!=null){
        m.insert(curr.getHacker());
        curr=curr.getNext();
        }
       }
       ArrayList<Hacker> mostWantedList = new ArrayList<>(n);
       
       for(int i=n; i>0;i-- ){
        mostWantedList.add(m.delMax());
       }
       return mostWantedList;
        

        
    }

    /**
     * Gets all hackers that have been involved in incidents at the given location.
     * 
     * You should check all hackers, and ALL of each hackers incidents.
     * You should not add a single hacker more than once.
     * 
     * @param location
     * @return Arraylist containing all hackers who have been involved in incidents at the given location.
     */
    public ArrayList<Hacker> getHackersByLocation(String location) {
        // WRITE YOUR CODE HERE 
        ArrayList<Hacker> hackersByLocation = new ArrayList<>();

        for (int i=0; i<hackerDirectory.length;++i){
            HNode ptr=hackerDirectory[i];
            while(ptr!=null){
                Hacker h =ptr.getHacker();
            
                for(int j=0; j<h.getIncidents().size();++j){
                Incident currInc= h.getIncidents().get(j);
                if(currInc.getLocation().equals(location)){
                    hackersByLocation.add(h);
                    break;
                }
                }
            
            ptr=ptr.getNext();
            }
            
        }
        return hackersByLocation;
        
    }
  

    /**
     * PROVIDED--DO NOT MODIFY!
     * Outputs the entire hacker directory to the terminal. 
     */
     public void printHackerDirectory() { 
        System.out.println(toString());
    } 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.hackerDirectory.length; i++) {
            HNode headHackerNode = hackerDirectory[i];
            while (headHackerNode != null) {
                if (headHackerNode.getHacker() != null) {
                    sb.append(headHackerNode.getHacker().toString()).append("\n");
                    ArrayList<Incident> incidents = headHackerNode.getHacker().getIncidents();
                    for (Incident incident : incidents) {
                        sb.append("\t" +incident.toString()).append("\n");
                    }
                }
                headHackerNode = headHackerNode.getNext();
            } 
        }
        return sb.toString();
    }

    public HNode[] getHackerDirectory() {
        return hackerDirectory;
    }
}
