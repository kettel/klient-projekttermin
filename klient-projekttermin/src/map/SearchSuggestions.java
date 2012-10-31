package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.nutiteq.components.KmlPlace;
import com.nutiteq.services.GeocodingResultWaiter;
import com.nutiteq.services.GeocodingService;

public class SearchSuggestions extends Observable implements GeocodingResultWaiter{

    private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();;
	
	public void updateSearch(String text){
		GeocodingService service = new GeocodingService(this,
				GeocodingService.DEFAULT_URL, "et", null, text,
				GeocodingService.SEARCH_TYPE_GEOCODING, null, 20, false);
				// Do something
				service.execute();
	}
	
	public void errors(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public void searchResults(KmlPlace[] kmlPlaces) {
        list.clear();
        for (int i=0;i<kmlPlaces.length;i++){
            HashMap<String,String> item = new HashMap<String,String>();
            item.put( "line1",kmlPlaces[i].getName());
            item.put( "line2",kmlPlaces[i].getDescription());
            list.add( item );
            System.out.println(kmlPlaces[i].getName());
        }        
        setChanged();
		notifyObservers();
	}

	public ArrayList<HashMap<String, String>> getList() {
		return list;
	}

}
