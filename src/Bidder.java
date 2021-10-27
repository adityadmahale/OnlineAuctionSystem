
public class Bidder {
    private String name;
    private int bidderId;
    private int lotsWon;
    private int feesOwed;

    public Bidder(String name, int bidderId) {
	this.name = name;
	this.bidderId = bidderId;
    }

    public int getBidderId() {
	return bidderId;
    }
    
    // The function returns the lots won by this bidder
    public int getLotsWon() {
	return lotsWon;
    }
    
    // The function sets the lots won by this bidder
    public void setLotsWon(int lotsWon) {
	this.lotsWon = lotsWon;
    }
    
    // The function returns the fees owed by this bidder
    public int getFeesOwed() {
	return feesOwed;
    }
    
    // The function sets the fees owed by this bidder
    public void setFeesOwed(int feesOwed) {
	this.feesOwed = feesOwed;
    }
    
    // Returns name of the bidder
    public String getName() {
	return name;
    }
}
