package map;

import com.nutiteq.components.WgsPoint;
import com.nutiteq.services.GeocodingResultWaiter;
import com.nutiteq.services.GeocodingService;

public class CustomGeocodingService extends GeocodingService{

	public CustomGeocodingService(GeocodingResultWaiter arg0, String arg1,
			String arg2, WgsPoint arg3, String arg4, String arg5, int[] arg6,
			int arg7, boolean arg8) {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int maxResults() {
		// TODO Auto-generated method stub
		return 3;
	}

}
