package map;

import java.util.ArrayList;
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
	private CustomGeocodingService service;
	private ArrayList<KmlPlace> list=new ArrayList<KmlPlace>();
	/**
	 * Tar in en text och startar en sökning på matchande termer. 
	 * 
	 * @param text
	 */
	public void updateSearch(String text) {
		if (service!=null) {
			service.cancel();
		}
		service = new CustomGeocodingService(this,
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
		for (int i = 0; i < kmlPlaces.length; i++) {
			list.add(kmlPlaces[i]);
		}
		/**
		 * Notifierar alla observers om att datan har ändrats
		 */
		setChanged();
		notifyObservers();
	}

	public ArrayList<KmlPlace> getList() {
		return list;
	}
}
