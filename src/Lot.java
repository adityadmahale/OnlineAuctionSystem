
public class Lot {
    private int currentBid;
    private int maximumBid;
    private Bidder bidder;
    
    // Initializes initial value to the dummy bidder 0
    public Lot(Bidder bidder0) {
	bidder = bidder0;
    }
    
    // Returns the current bid value
    public int getCurrentBid() {
	return currentBid;
    }
    
    // Sets the current bid value
    public void setCurrentBid(int currentBid) {
	this.currentBid = currentBid;
    }
    
    // Retrieves the maximum remembered bid value
    public int getMaximumBid() {
	return maximumBid;
    }
    
    // Sets the maximum remembered bid value
    public void setMaximumBid(int maximumBid) {
	this.maximumBid = maximumBid;
    }
    
    // Returns the currently winning bidder
    public Bidder getBidder() {
	return bidder;
    }
    
    // Sets the bidder for this lot
    public void setBidder(Bidder bidder) {
	this.bidder = bidder;
    }

}
