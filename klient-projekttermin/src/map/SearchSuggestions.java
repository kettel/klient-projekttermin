package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import com.nutiteq.components.KmlPlace;
import com.nutiteq.services.GeocodingResultWaiter;
import com.nutiteq.services.GeocodingService;

/**
 * Tar fram sökförslag utifrån given sökterm.
 * 
 * @author nicklas
 *
 */
public class SearchSuggestions extends Observable implements
		GeocodingResultWaiter {

	//private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private ArrayList<String> list=new ArrayList<String>();
	/**
	 * Tar in en text och startar en sökning på matchande termer. 
	 * 
	 * @param text
	 */
	public void updateSearch(String text) {
		final GeocodingService service = new GeocodingService(this,
				GeocodingService.DEFAULT_URL, "et", null, text,
				GeocodingService.SEARCH_TYPE_GEOCODING, null, 20, false);
		service.execute();
	}

	public void errors(int arg0) {
	}

	/**
	 * Lägger till funna sökord i en lista, max 10 sökord skrivs ut 
	 */
	public void searchResults(KmlPlace[] kmlPlaces) {
		list.clear();
		int results=kmlPlaces.length;
		if (results>10){
			results=10;
		}
		for (int i = 0; i < results; i++) {
//			HashMap<String, String> item = new HashMap<String, String>();
//			item.put("line1", kmlPlaces[i].getName());
//			item.put("line2", kmlPlaces[i].getDescription());
//			list.add(item);
			list.add(kmlPlaces[i].getName());
		}
		/**
		 * Notifierar alla observers om att datan har ändrats
		 */
		setChanged();
		notifyObservers();
	}

	public ArrayList<String> getList() {
		return list;
	}
}
