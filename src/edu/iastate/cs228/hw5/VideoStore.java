package edu.iastate.cs228.hw5;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 
 * @author Andrew Pester
 *
 */

public class VideoStore 
{
	protected SplayTree<Video> inventory;     // all the videos at the store
	
	// ------------
	// Constructors 
	// ------------
	
    /**
     * Default constructor sets inventory to an empty tree. 
     */
    public VideoStore()
    {
    	
    	inventory = new SplayTree<>();
    }
    
    
	/**
	 * Constructor accepts a video file to create its inventory.  Refer to Section 3.2 of  
	 * the project description for details regarding the format of a video file. 
	 * 
	 * Calls setUpInventory(). 
	 * 
	 * @param videoFile  no format checking on the file
	 * @throws FileNotFoundException
	 */
    public VideoStore(String videoFile) throws FileNotFoundException  
    {
    	
    	inventory = new SplayTree<>();
    	setUpInventory(videoFile);	
    }
    
   /**
     * Accepts a video file to initialize the splay tree inventory.  To be efficient, 
     * add videos to the inventory by calling the addBST() method, which does not splay. 
     * @param  videoFile  correctly formated if exists
     * @throws FileNotFoundException 
     */
    public void setUpInventory(String videoFile) throws FileNotFoundException
    {
    	
    	inventory.clear();
    	bulkImport(videoFile);
    }
	
    
    // ------------------
    // Inventory Addition
    // ------------------
    
    /**
     * Find a Video object by film title. 
     * 
     * @param film
     * @return the video if it is found or null if the video is not found
     */
	public Video findVideo(String film) 
	{
		
		Video v = new Video(film);
		return inventory.findElement(v); 
	}


	/**
	 * Updates the splay tree inventory by adding a number of video copies of the film.  
	 * (Splaying is justified as new videos are more likely to be rented.) 
	 * 
	 * Calls the add() method of SplayTree to add the video object.  
	 * 
	 *     a) If true is returned, the film was not on the inventory before, and has been added.  
	 *     b) If false is returned, the film is already on the inventory. 
	 *     
	 * The root of the splay tree must store the corresponding Video object for the film. Update 
	 * the number of copies for the film.  
	 * 
	 * @param film  title of the film
	 * @param n     number of video copies 
	 */
	public void addVideo(String film, int n)  
	{
		
		Video v = new Video(film,n);
		if(inventory.add(v)) {
			//means it already added
		}else {
			inventory.findEntry(v).data.addNumCopies(n);
		}
			

	}
	

	/**
	 * Add one video copy of the film. 
	 * 
	 * @param film  title of the film
	 */
	public void addVideo(String film)
	{
		
		Video v = new Video(film);
		if(inventory.add(v)) {
			//means it already added
		}else {
			inventory.findEntry(v).data.addNumCopies(1);
		} 
	}
	

	/**
     * Update the splay trees inventory by adding videos.  Perform binary search additions by 
     * calling addBST() without splaying. 
     * @param videoFile  correctly formated if exists 
     * @throws FileNotFoundException
     */
    public void bulkImport(String videoFile) throws FileNotFoundException 
    {
    	
    	File f = new File(videoFile);
    	Scanner sc = new Scanner(f);
    	String tmp = "";
    	String film = "";
    	int num = 0;
    	while(sc.hasNextLine()) {
    		tmp = sc.nextLine();
    		film = parseFilmName(tmp);
    		num = parseNumCopies(tmp);
    		if(inventory.addBST(new Video(film,num))) {
    			
    		}else {
    			inventory.findEntry(new Video(film,1)).data.addNumCopies(num);
    		}
    	}
    }

    
    // ----------------------------
    // Video Query, Rental & Return 
    // ----------------------------
    
	/**
	 * Search the splay tree inventory to determine if a video is available. 
	 * 
	 * @param  film
	 * @return true if available
	 */
	public boolean available(String film)
	{
		
		Video v = new Video(film);
		if(inventory.findElement(v) == null) {
			return false;
		}else {
			if(inventory.findElement(v).getNumAvailableCopies() != 0) {
				return true;
			}else {
				return false;
			}
		}
	}

	
	
	/**
     * Update inventory. 
     * 
     * Search if the film is in inventory by calling findElement(new Video(film, 1)). 
     * 
     * If the film is not in inventory, prints the message "Film <film> is not 
     * in inventory", where <film> shall be replaced with the string that is the value 
     * of the parameter film.  If the film is in inventory with no copy left, prints
     * the message "Film <film> has been rented out".
     * 
     * If there is at least one available copy but n is greater than the number of 
     * such copies, rent all available copies. In this case, no AllCopiesRentedOutException
     * is thrown.  
     * 
     * @param film   
     * @param n 
     * @throws IllegalArgumentException      if n <= 0 or film == null or film.isEmpty()
	 * @throws FilmNotInInventoryException   if film is not in the inventory
	 * @throws AllCopiesRentedOutException   if there is zero available copy for the film.
	 */
	public void videoRent(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException,  
									     			 AllCopiesRentedOutException 
	{
		
		if(n<=0 || film == null || film.isEmpty()) {
			throw new IllegalArgumentException("Film "+film+" has an invalid request");
		}
		Video v = inventory.findElement(new Video(film, 1));
		if(v == null) {
			throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
		}else if(v.getNumAvailableCopies() == 0) {
			throw new AllCopiesRentedOutException("Film "+film+" has been rented out");
		}
		else{
			if(n > v.getNumAvailableCopies()) {
				v.rentCopies(v.getNumAvailableCopies());
			}else {
				v.rentCopies(n);
			}
		}
	}

	
	/**
	 * Update inventory.
	 * 
	 *    1. Calls videoRent() repeatedly for every video listed in the file.  
	 *    2. For each requested video, do the following: 
	 *       a) If it is not in inventory or is rented out, an exception will be 
	 *          thrown from videoRent().  Based on the exception, prints out the following 
	 *          message: "Film <film> is not in inventory" or "Film <film> 
	 *          has been rented out." In the message, <film> shall be replaced with 
	 *          the name of the video. 
	 *       b) Otherwise, update the video record in the inventory.     
	 * @param videoFile  correctly formatted if exists
	 * @throws FileNotFoundException
     * @throws IllegalArgumentException     if the number of copies of any film is <= 0
	 * @throws FilmNotInInventoryException  if any film from the videoFile is not in the inventory 
	 * @throws AllCopiesRentedOutException  if there is zero available copy for some film in videoFile
	 */
	public void bulkRent(String videoFile) throws FileNotFoundException, IllegalArgumentException, 
												  FilmNotInInventoryException, AllCopiesRentedOutException 
	{
		
		File f = new File(videoFile);
		Scanner sc = new Scanner(f);
		String tmp = "";
		String filmname  = "";
		String ret = "";
		boolean illegal = false;
		boolean film = false;
		boolean all = false;
		int num = 0;
		while(sc.hasNextLine()) {
			tmp = sc.nextLine();
			filmname = parseFilmName(tmp);
			num = parseNumCopies(tmp);
			try {
			this.videoRent(filmname, num);
			}catch(IllegalArgumentException e) {
				illegal = true;
				ret += e.getMessage()+ "\n";
			}catch(FilmNotInInventoryException e) {
				film = true;
				ret += e.getMessage()+"\n";
			}catch(AllCopiesRentedOutException e) {
				all = true;
				ret += e.getMessage()+"\n";
			}
			
		}
		ret = ret.trim();
		if(illegal) {
			throw new IllegalArgumentException(ret);
		}else if(film) {
			throw new FilmNotInInventoryException(ret);
		}else if(all) {
			throw new AllCopiesRentedOutException(ret);
		}
	}

	
	/**
	 * Update inventory.
	 * 
	 * If n exceeds the number of rented video copies, accepts up to that number of rented copies
	 * while ignoring the extra copies. 
	 * 
	 * @param film
	 * @param n
	 * @throws IllegalArgumentException     if n <= 0 or film == null or film.isEmpty()
	 * @throws FilmNotInInventoryException  if film is not in the inventory
	 */
	public void videoReturn(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException 
	{
		
		if(n <= 0 || film == null || film.isEmpty()) {
			throw new IllegalArgumentException("Film "+film+" has an invalid request");
		}
		Video v = new Video(film,1);
		if(!inventory.contains(v)) {
			throw new FilmNotInInventoryException("Film "+ film +" is not in inventory");	
		}else {
			inventory.findElement(v).returnCopies(n);
		}
	}
	
	
	/**
	 * Update inventory. 
	 * 
	 * Handles excessive returned copies of a film in the same way as videoReturn() does.
	 * @param videoFile
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException    if the number of return copies of any film is <= 0
	 * @throws FilmNotInInventoryException if a film from videoFile is not in inventory
	 */
	public void bulkReturn(String videoFile) throws FileNotFoundException, IllegalArgumentException,
													FilmNotInInventoryException												
	{
		File f = new File(videoFile);
		Scanner sc = new Scanner(f);
		String tmp = "";
		String film = "";
		int num = 0;
		boolean illegal = false;
		boolean filmIn = false;
		String ret  = "";
		while(sc.hasNextLine()) {
			tmp = sc.nextLine();
			film = parseFilmName(tmp);
			num = parseNumCopies(tmp);
			try {
				videoReturn(film, num);
			}catch(IllegalArgumentException e) {
				illegal = true;
				ret+= e.getMessage()+"\n";
			}catch(FilmNotInInventoryException e) {
				filmIn = true;
				ret+= e.getMessage()+"\n";
			}
		} 
		ret = ret.trim();
		if(illegal) {
			throw new IllegalArgumentException(ret);
		}else if(filmIn) {
			throw new FilmNotInInventoryException(ret);
		}
	}
		
	

	// ------------------------
	// Methods without Splaying
	// ------------------------
		
	/**
	 * Performs inorder traversal on the splay tree inventory to list all the videos by film 
	 * title, whether rented or not.  Below is a sample string if printed out: 
	 * 
	 * 
	 * Films in inventory: 
	 * 
	 * A Streetcar Named Desire (1) 
	 * Brokeback Mountain (1) 
	 * Forrest Gump (1)
	 * Psycho (1) 
	 * Singin' in the Rain (2)
	 * Slumdog Millionaire (5) 
	 * Taxi Driver (1) 
	 * The Godfather (1) 
	 * 
	 * 
	 * @return
	 */
	public String inventoryList()
	{
		
		String ret ="Films in inventory:\n\n";
		Iterator<Video> i = inventory.iterator();
		while(i.hasNext()) {
			Video tmp = i.next();
			ret += tmp.getFilm() + " (" + tmp.getNumCopies() + ")\n"; 
		}
		ret = ret.trim();
		return ret; 
	}

	
	/**
	 * Calls rentedVideosList() and unrentedVideosList() sequentially.  For the string format, 
	 * see Transaction 5 in the sample simulation in Section 4 of the project description. 
	 *   
	 * @return 
	 */
	public String transactionsSummary()
	{
		
		String ret  = rentedVideosList() + "\n\n" + unrentedVideosList();
		return ret; 
	}	
	
	/**
	 * Performs inorder traversal on the splay tree inventory.  Use a splay tree iterator.
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * Rented films: 
	 * 
	 * Brokeback Mountain (1)
	 * Forrest Gump (1) 
	 * Singin' in the Rain (2)
	 * The Godfather (1)
	 * 
	 * 
	 * @return
	 */
	private String rentedVideosList()
	{
		
		String ret ="Rented films:\n\n";
		Iterator<Video> i = inventory.iterator();
		while(i.hasNext()) {
			Video tmp = i.next();
			if(tmp.getNumRentedCopies()!=0) {
				ret += tmp.getFilm() + " (" + tmp.getNumRentedCopies() + ")\n"; 
			}
		}
		ret = ret.trim();
		return ret; 
	}

	
	/**
	 * Performs inorder traversal on the splay tree inventory.  Use a splay tree iterator.
	 * Prints only the films that have unrented copies. 
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * 
	 * Films remaining in inventory:
	 * 
	 * A Streetcar Named Desire (1) 
	 * Forrest Gump (1)
	 * Psycho (1) 
	 * Slumdog Millionaire (4) 
	 * Taxi Driver (1) 
	 * 
	 * 
	 * @return
	 */
	private String unrentedVideosList()
	{
		
		String ret ="Films remaining in inventory:\n\n";
		Iterator<Video> i = inventory.iterator();
		while(i.hasNext()) {
			Video tmp = i.next();
			if(tmp.getNumAvailableCopies()!=0) {
				ret += tmp.getFilm() + " (" + tmp.getNumAvailableCopies() + ")\n"; 
			}
		}
		ret = ret.trim();
		return ret; 
	}	

	
	/**
	 * Parse the film name from an input line. 
	 * 
	 * @param line
	 * @return
	 */
	public static String parseFilmName(String line) 
	{
		
		String ret = "";
		for(int i = 0;i<line.length();i++) {
			if(line.charAt(i) != '(') {
				ret += line.charAt(i);
			}else if(line.charAt(i) == '(') {
				break;
			}
		}
		ret = ret.trim();
		return ret; 
	}
	
	
	/**
	 * Parse the number of copies from an input line. 
	 * 
	 * @param line
	 * @return
	 */
	public static int parseNumCopies(String line) 
	{
		
		int ret = 0;
		if(line.contains("(")) {
			ret = Integer.parseInt(line.substring(line.indexOf("(")+1,line.indexOf(")")));	
		}else {
			ret  = 1;
		}
		
		return ret; 
	}
}
