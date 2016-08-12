package ssomap.Photos;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class EMF {
	private static EntityManagerFactory emf;
	
	public static EntityManagerFactory get(){
		if(emf==null){
			emf = Persistence.createEntityManagerFactory("transactions-optional");
		}
		return emf;
	}
}
