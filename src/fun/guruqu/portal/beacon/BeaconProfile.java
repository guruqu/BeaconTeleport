package fun.guruqu.portal.beacon;

import java.util.List;

import fun.guruqu.portal.structures.StrippedLocation;


public class BeaconProfile {
	StrippedLocation location;
	String name;
	String guid;
	String owner;
	boolean privateOwn;
	int used;
	double totalEarned;

	public double getLastInformedEarning() {
		return lastInformedEarning;
	}

	public void setLastInformedEarning(double lastInformedEarning) {
		this.lastInformedEarning = lastInformedEarning;
	}

	double lastInformedEarning;

	public double getTotalEarned() {
		return totalEarned;
	}

	public void setTotalEarned(double totalEarned) {
		this.totalEarned = totalEarned;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	int cost;
	List<StrippedLocation> signLocation;

	public StrippedLocation getLocation() {
		return location;
	}

	public void setLocation(StrippedLocation location) {
		this.location = location;
	}

	public List<StrippedLocation> getSignLocation() {
		return signLocation;
	}

	public void setSignLocation(List<StrippedLocation> signLocation) {
		this.signLocation = signLocation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isPrivateOwn() {
		return privateOwn;
	}

	public void setPrivateOwn(boolean privateOwn) {
		this.privateOwn = privateOwn;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
}
