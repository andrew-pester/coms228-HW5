package edu.iastate.cs228.hw5;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *  
 * @author Andrew Pester
 *
 */

/**
 * 
 * The Transactions class simulates video transactions at a video store.
 *
 */
public class Transactions {

	/**
	 * The main method generates a simulation of rental and return activities.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		VideoStore vs = new VideoStore();
		System.out.println("Transactions at a Video Store");
		System.out.print("keys: 1 (rent)    2 (bulk rent)\r\n" + "      3 (return)  4 (bulk return)\r\n"
				+ "      5 (summary) 6 (exit)\r\n\n");
		while (true) {
			System.out.print("Transaction: ");
			int key = in.nextInt();
			in.nextLine();
			if (key == 1) {

				try {
					System.out.print("Film to rent: ");
					String tmp = in.nextLine();
					vs.videoRent(vs.parseFilmName(tmp), vs.parseNumCopies(tmp));
					System.out.print("\n");
				} catch (IllegalArgumentException | FilmNotInInventoryException | AllCopiesRentedOutException e) {
					System.out.println(e.getMessage() + "\n");
				}
			} else if (key == 2) {

				System.out.print("Video file (rent): ");
				try {
					vs.bulkRent(in.nextLine());
					System.out.print("\n");
				} catch (IllegalArgumentException | FilmNotInInventoryException | AllCopiesRentedOutException e) {
					System.out.println(e.getMessage() + "\n");
				}
			} else if (key == 3) {

				System.out.print("Film to return: ");
				String tmp = in.nextLine();
				try {
					vs.videoReturn(vs.parseFilmName(tmp), vs.parseNumCopies(tmp));
					System.out.print("\n");
				} catch (IllegalArgumentException | FilmNotInInventoryException e) {
					System.out.println(e.getMessage() + "\n");
				}
			} else if (key == 4) {

				System.out.print("Video file (return): ");
				try {
					vs.bulkReturn(in.nextLine());
					System.out.print("\n");
				} catch (IllegalArgumentException | FilmNotInInventoryException e) {
					System.out.println(e.getMessage() + "\n");
				}

			} else if (key == 5) {

				System.out.println(vs.transactionsSummary() + "\n");
			} else {
				break;
			}

		}
	}
}
