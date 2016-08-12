package ssomap.hotQueue;

import ssomap.Photos.Photos;

public class HotQueueItem {
	private String date;
	private String GeoCell;
	private String pictureID;
	
	
	
	public HotQueueItem(String[] arr) {
		super();
		this.date = arr[2];
		GeoCell = arr[1];
		this.pictureID = arr[0];
	}
	public String getPictureID() {
		return pictureID;
	}
	public void setPictureID(String pictureID) {
		this.pictureID = pictureID;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getGeoCell() {
		return GeoCell;
	}
	public void setGeoCell(String geoCell) {
		GeoCell = geoCell;
	}
	
	public HotQueueItem(Photos photo) {
		this.date = photo.getMakeTimeS();
		this.GeoCell = photo.getGeocell().get(9);
		this.pictureID = photo.getPictureID();
	}
}
