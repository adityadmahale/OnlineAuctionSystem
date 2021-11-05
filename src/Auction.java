
public class Auction {
    // Constants defining auction status values
    private static final String NEW = "new";
    private static final String OPEN = "open";
    private static final String CLOSE = "closed";

    private String name;
    private String status;
    private int firstLotnumber;
    private int lastLotNumber;
    private int minBidIncrement;
    private Lot[] lots;

    public Auction(String name, int firstLotNumber, int lastLotNumber, int minBidIncrement,
	    Bidder bidder0) {
	this.name = name;
	this.firstLotnumber = firstLotNumber;
	this.lastLotNumber = lastLotNumber;
	this.minBidIncrement = minBidIncrement;
	// Set the initial status of the auction to new
	this.status = NEW;
	// Initialize each spot in the lots array to a Lot object
	initializeLots(bidder0);
    }

    private void initializeLots(Bidder bidder0) {
	// Assigns each index in the lots array to a new Lot object
	lots = new Lot[lastLotNumber - firstLotnumber + 1];
	for (int i = 0; i < lots.length; i++) {
	    lots[i] = new Lot(bidder0);
	}
    }

    public boolean openAuction() {
	// Checks if the auction is already open
	if (isOpen()) {
	    return true;
	}
	// Sets auction status to open
	this.status = OPEN;
	return true;
    }
    
    // Returns true if the auction status is "open"
    public boolean isOpen() {
	return this.status.equals(OPEN);
    }

    public boolean closeAuction() {
	// Checks if the auction status is other than open
	if (!isOpen()) {
	    return false;
	}
	// Closes the auction and updates the bidder information
	this.status = CLOSE;
	updateBidderStatus();
	return true;
    }

    private void updateBidderStatus() {
	// Loops over each lot in this auction and sets if the bidder won the lot and adds to
	// the total fees owed by the bidder.
	Bidder bidder;
	for (var lot : lots) {
	    bidder = lot.getBidder();
	    bidder.setLotsWon(bidder.getLotsWon() + 1);
	    bidder.setFeesOwed(bidder.getFeesOwed() + lot.getCurrentBid());							    
	}
    }

    public String winningBids() {
	// Loops over each lot and appends the winning bids for each lot
	StringBuffer winners = new StringBuffer();
	int lotNumber = firstLotnumber;
	for (var lot : lots) {
	    winners.append(lotNumber++ + "\t" + lot.getCurrentBid() + "\t"
		    + lot.getBidder().getBidderId() + "\n");
	}
	return winners.toString();
    }
    
    // Returns the name of the auction
    public String getName() {
	return this.name;
    }
    
    // Returns the status of the auction
    public String getStatus() {
	return this.status;
    }
    
    // Checks if a lot number lies in the range of lots numbers of this auction
    public boolean isLotNumberInRange(int lotNumber) {
	if (lotNumber >= firstLotnumber && lotNumber <= lastLotNumber) {
	    return true;
	}

	return false;
    }
    
    // Returns the cumulative currently winning bids of this auction
    public int getCumulativeWinningBids() {
	int sum = 0;
	// The loop adds currently winning bids of each lot
	for (var lot : lots) {
	    sum += lot.getCurrentBid();
	}
	return sum;
    }
    
    // Returns lot object for a given lot number
    public Lot getLot(int lotNumber) {
	return lots[lotNumber - firstLotnumber];
    }
    
    // Checks if a bid value is a valid next bid
    public boolean isValidNextBid(Lot lot, int bid) {
	return bid >= lot.getCurrentBid() + minBidIncrement;
    }
    
    // Returns minimum bid increment value
    public int getMinBidIncrement() {
	return minBidIncrement;
    }
}
