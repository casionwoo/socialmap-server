package ssomap.Photos;

import java.util.Arrays;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@XmlRootElement(name = "Photos")
@Entity
public class Photos {
	
	@XmlTransient
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Key key;
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	@XmlAttribute
	public Long getId() {
		if (key == null) {
			return null;
		}
		return key.getId();
	}

	public void setId(Long id) {
		key = KeyFactory.createKey(PhotosDao.ENTITY, id);
	}
	
	// Attributes
		@Basic
		@Column()
		String pictureID;				
		@Basic									
		double latitude;
		@Basic
		double longitude;
		@Basic
		String commentURL;	
		@Basic
		String imageURL;	
		@Basic
		String smallImageURL;	


		@Basic
		String makeTimeS;	
		@Basic											
		String record;
		@Basic
		String ownerName;
		@Basic
		String ownerId;
		@Basic
		double makeTime;
		@Basic
		String isPublic;
		@Basic
		String ownerThumURL;	
		@Basic
		List<String> geocell;
		@Basic
		String placeName;
		
		@Basic
		String HotKeyWord;
		
		public String getHotKeyWord() {
			return HotKeyWord;
		}

		public void setHotKeyWord(String hotKeyWord) {
			HotKeyWord = hotKeyWord;
		}

		public String getPlaceName() {
			return placeName;
		}

		public void setPlaceName(String placeName) {
			this.placeName = placeName;
		}

		public Photos(){
			
		}
		
		public Photos(String contents){
			
			String[] content = contents.split(" ");
			this.makeTimeS = content[3];
			this.ownerId = content[1];
			this.pictureID = content[0];
			this.geocell = Arrays.asList(content[2], content[2],
					content[2], content[2], content[2],
					content[2], content[2], content[2],
					content[2], content[2], content[2],
					content[2], content[2]);
		}
		
		
		
		public List<String> getGeocell() {
			return geocell;
		}

		public void setGeocell(List<String> geocell) {
			this.geocell = geocell;
		}

		public String getPictureID() {
			return pictureID;
		}

		public void setPictureID(String pictureID) {
			this.pictureID = pictureID;
		}
		public double getMakeTime() {
			return makeTime;
		}

		public void setMakeTime(double makeTime) {
			this.makeTime = makeTime;
		}

		public String getIsPublic() {
			return isPublic;
		}

		public void setIsPublic(String isPublic) {
			this.isPublic = isPublic;
		}

		public String getMakeTimeS() {
			return makeTimeS;
		}

		public void setMakeTimeS(String makeTimeS) {
			this.makeTimeS = makeTimeS;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public String getCommentURL() {
			return commentURL;
		}

		public void setCommentURL(String commentURL) {
			this.commentURL = commentURL;
		}

		public String getImageURL() {
			return imageURL;
		}

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}

		public String getSmallImageURL() {
			return smallImageURL;
		}

		public void setSmallImageURL(String smallImageURL) {
			this.smallImageURL = smallImageURL;
		}


		public String getRecord() {
			return record;
		}

		public void setRecord(String record) {
			this.record = record;
		}

		public String getOwnerName() {
			return ownerName;
		}

		public void setOwnerName(String ownerName) {
			this.ownerName = ownerName;
		}

		public String getOwnerId() {
			return ownerId;
		}

		public void setOwnerId(String ownerId) {
			this.ownerId = ownerId;
		}

		public String getOwnerThumURL() {
			return ownerThumURL;
		}

		public void setOwnerThumURL(String ownerThumURL) {
			this.ownerThumURL = ownerThumURL;
		}		
}
