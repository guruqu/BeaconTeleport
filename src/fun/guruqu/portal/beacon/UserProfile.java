package fun.guruqu.portal.beacon;

import java.util.List;
import java.util.Vector;

public class UserProfile {
	String name;
	List<String> ownBeacon;

	public UserProfile() {
		ownBeacon = new Vector<>();
		knownBeacon = new Vector<>();
		beaconLimit = 0;
		bookSize = 9;
	}

	public int getBookSize() {
		return bookSize;
	}

	public void setBookSize(int bookSize) {
		this.bookSize = bookSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getOwnBeacon() {
		return ownBeacon;
	}

	public void setOwnBeacon(List<String> ownBeacon) {
		this.ownBeacon = ownBeacon;
	}

	public List<String> getKnownBeacon() {
		return knownBeacon;
	}

	public void setKnownBeacon(List<String> knownBeacon) {
		this.knownBeacon = knownBeacon;
	}

	public int getBeaconLimit() {
		return beaconLimit;
	}

	public void setBeaconLimit(int beaconLimit) {
		this.beaconLimit = beaconLimit;
	}

	List<String> knownBeacon;

	int beaconLimit;
	int bookSize;
}
