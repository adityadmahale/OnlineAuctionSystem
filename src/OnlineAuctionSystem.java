
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OnlineAuctionSystem {
    // The online auction system maintains a list of auctions and bidders
    private List<Auction> auctions = new ArrayList<>();
    private List<Bidder> bidders = new ArrayList<>();

    public OnlineAuctionSystem() {
	// Adds a dummy bidder at the first position of the bidders' list
	bidders.add(new Bidder("bidder0", bidders.size()));
    }

    public Auction createAuction(String auctionName, int firstLotNumber, int lastLotNumber,
	    int minBidIncrement) {
	// Handles bad inputs and returns null
	if (auctionName == null || auctionName == "" || minBidIncrement < 1)
	    return null;
	
	// Checks if the lot range is overlapping with other auctions' lot ranges. 
	// Also, Check if the lot range is valid.
	if (!isValidLotRange(firstLotNumber, lastLotNumber)
		|| isLotRangeOverlapping(firstLotNumber, lastLotNumber))
	    return null;
	
	// At this point, all the passed parameters are correct. Hence, create a new auction object
	// and add it to the auctions list.
	var auction = new Auction(auctionName, firstLotNumber, lastLotNumber, minBidIncrement,
		bidders.get(0));
	auctions.add(auction);
	return auction;
    }

    private boolean isLotRangeOverlapping(int firstLotNumber, int lastLotNumber) {
	// Loops over each auction and checks if the passed lot range is overlapping or not
	for (var auction : auctions) {
	    if (auction.isLotNumberInRange(firstLotNumber)
		    || auction.isLotNumberInRange(lastLotNumber)) {
		return true;
	    }
	}
	return false;
    }
    
    // Returns true if the lot range is valid
    private boolean isValidLotRange(int firstLotNumber, int lastLotNumber) {
	return firstLotNumber > 0 && lastLotNumber > 0 && firstLotNumber <= lastLotNumber;
    }
    
    public Bidder createBidder(String bidderName) {
	// Checks for bad inputs and returns null.
	if (bidderName == null || bidderName == "") return null;
	
	// Creates a new auction and adds it to the bidders list.
	var bidder = new Bidder(bidderName, bidders.size());
	bidders.add(bidder);
	return bidder;
    }
    
    // Loops over each auction to return the auction status for each auction
    public String auctionStatus() {
	StringBuffer summary = new StringBuffer();
	for (var auction : auctions) {
	    summary.append(auction.getName() + "\t" + auction.getStatus() + "\t"
		    + auction.getCumulativeWinningBids() + "\n");
	}
	return summary.toString();
    }

    public int loadBids(String fileName) {
	Scanner input = null;
	int validBids = 0;
	int status;
	// Try catch block handles null and invalid fileName
	try {
	    File file = new File(fileName);
	    input = new Scanner(file);
	    while (input.hasNextLine()) {
		String[] line = input.nextLine().split("\t");
		// Passes parsed values to the place bid method
		status = placeBid(Integer.parseInt(line[1]), Integer.parseInt(line[0]),
			Integer.parseInt(line[2]));
		if (status > 1) {
		    validBids++;
		}
	    }
	} catch (FileNotFoundException e) {
	    return 0;
	} catch (NullPointerException e) {
	    return 0;
	}
	finally {
	    if (input != null) {
		input.close();
	    }
	}
	return validBids;
    }
    
    // Get return code for the scenarios when bid value is greater than max remembered bid
    // If maxBid - currentBid + minBidIncrement >= 0 ,then automatic bid is possible
    private int getReturnCode(int maxBid, int currentBid, int minBidIncrement) {
	if ((maxBid - (currentBid + minBidIncrement)) >=  0) return 4;
	return 3;
    }
    
    private int processWinningBiddersBid(int bid, int maximumRememberedBid, int minBidIncrement, Lot lot) {
	// The bid value is greater than the maximum remembered bid.
	// Max remembered bid gets updated to the bid value
	if (bid > maximumRememberedBid) {
	    lot.setMaximumBid(bid);
	    return getReturnCode(lot.getMaximumBid(), lot.getCurrentBid(), minBidIncrement);
	}
	
	// The bid value is less than or equal to the maximum remembered bid.
	// The bid gets accepted, but the bidder's previous bid remains valid.
	return 2;
    }
    
    private int processOtherBiddersBid(int bid, int maximumRememberedBid, int minBidIncrement, Lot lot, Bidder bidder) {
	int autoBidValue;
	if (bid < maximumRememberedBid) {
	    // Case when the bid value is less than the max bid amount
	    // If the next valid bid value is less than or equal to the max remembered bid, then set the current bid
	    // to the next bid value else set it to the bid value.
	    autoBidValue = bid + minBidIncrement;
	    if (autoBidValue <= maximumRememberedBid) {
		lot.setCurrentBid(autoBidValue);
		return 2;
	    }
	    lot.setCurrentBid(bid);
	    return 3;
	} else if (bid == maximumRememberedBid) {
	    // Case when the bid value is equal to the max bid amount. If they're same,then set the current bid
	    // to the bid value.
	    lot.setCurrentBid(bid);
	    return 2;
	} else {
	    // Case when the bid value is more than the max bid amount
	    // In this case, the bid amount is the winning bid. Hence, update the maximum
	    // remembered bid and bidder.
	    autoBidValue = maximumRememberedBid + minBidIncrement;
	    lot.setMaximumBid(bid);
	    lot.setBidder(bidder);
		    
	    // If the next bid value is less than or equal to the bid value, then set the current bid to
	    // the next valid bid value. Otherwise, set it to the maximum remembered bid value.
	    if (autoBidValue <= bid) {
		lot.setCurrentBid(autoBidValue);
	    } else {
		lot.setCurrentBid(maximumRememberedBid);
	    }
	    return getReturnCode(lot.getMaximumBid(), lot.getCurrentBid(), minBidIncrement);
	}
    }
    
    public int placeBid(int lotNumber, int bidderId, int bid) {
	var auction = getAuction(lotNumber);
	var bidder = getBidder(bidderId);
	// Handles if the given input is a bad input by returning 1
	if (bidderId < 1 || bid < 1 || auction == null || bidder == null || !auction.isOpen())
	    return 0;

	var lot = auction.getLot(lotNumber);
	int maximumRememberedBid = lot.getMaximumBid();
	int minBidIncrement = auction.getMinBidIncrement();
	
	// Scenario when the winning bidder bids again
	if (bidder == lot.getBidder()) {
	    return processWinningBiddersBid(bid, maximumRememberedBid, minBidIncrement, lot);
	}
	
	// Check for other bidders if the bid value is a valid next bid.
	// If not, then return 1
	if (!auction.isValidNextBid(lot, bid)) return 2;
	
	// Scenario when a bidder other than the winner bidder bids on a lot
	return processOtherBiddersBid(bid, maximumRememberedBid, minBidIncrement, lot, bidder);
    }

    private Bidder getBidder(int bidderId) {
	// Checks if the bidder id is invalid. If not, then the function returns the bidder object.
	if (bidderId < 0 || bidderId > bidders.size() - 1)
	    return null;
	return bidders.get(bidderId);
    }
    
    // Returns the auction object for a given lot number
    private Auction getAuction(int lotNumber) {
	for (var auction : auctions) {
	    if (auction.isLotNumberInRange(lotNumber)) {
		return auction;
	    }
	}

	return null;
    }
    
    // Returns the string containing information about the fees owed by each bidder
    public String feesOwed() {
	if (bidders.size() < 2) return "";
	
	// Bidder object includes the information about the lots won and fees owed
	// The loop gets the information for each bidder and appends it to a single string.
	StringBuffer bidderStatus = new StringBuffer();
	for (var bidder : bidders.subList(1, bidders.size())) {
	    bidderStatus.append(bidder.getName() + "\t" + bidder.getLotsWon() + "\t"
		    + bidder.getFeesOwed() + "\n");
	}
	return bidderStatus.toString();
    }
}
