package ssomap.TotalPhotos;

public class GeoAndCount implements Comparable<GeoAndCount> {
	private String Geo;
	private int Count;


	public String getGeo() {
		return Geo;
	}

	public void setGeo(String geo) {
		Geo = geo;
	}

	public int getCount() {
		return Count;
	}
	public void IncreaseCount()
	{
		this.Count = this.Count+1;
	}
	public void setCount(int count) {
		Count = count;
	}

	public GeoAndCount(String Geo, int Count){
		this.Geo = Geo;
		this.Count= Count;
	}
	
	public String toString(){
		return "Geo : "+Geo +"  Count : " + Count;
	}
	
	public int compareTo(GeoAndCount m) {

		if (this.Count < m.Count) {
			return -1;
		} else if (this.Count == m.Count) {
			return 0;
		} else {
			return 1;
		}
	}
}
