/** 
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Felipe Oliveira (http://mashup.fm)
 * 
 */
package play.modules.geoloco;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import play.Logger;
import play.libs.F.Matcher;
import play.libs.F.None;
import play.libs.F.Option;
import play.libs.F.Some;
import play.libs.F.Tuple;
import play.libs.WS;
import play.libs.XPath;

/**
 * The Interface Geocoder.
 */
public interface Geocoder {

	/**
	 * Gets the geocode.
	 * 
	 * @param address
	 *            the address
	 * @return the geocode
	 */
	public Option<Tuple<Double, Double>> geocode(String address);

	/**
	 * The Class GoogleGeocoder.
	 */
	public static class GoogleGeocoder implements Geocoder {

		/**
		 * Gets the geocode.
		 * 
		 * @param address
		 *            the address
		 * @return the geocode
		 */
		@Override
		public Option<Tuple<Double, Double>> geocode(String address) {
			// Define Params
			boolean sensor = false;
			String url = String.format("http://maps.googleapis.com/maps/api/geocode/xml?address=%s&sensor=%s", StringUtils.replace(address, " ", "+"), sensor);

			// Log Debug
			Logger.info("Geocode Lookup - address: %s, url: %s", address, url);

			// Request XML
			Document xml = WS.url(url).get().getXml();
			Logger.info("XML: %s", xml);

			// Let's have some F fun - Make sure the var xml is of type
			// Document.class (and not null)
			for (Document doc : Matcher.ClassOf(Document.class).match(xml)) {
				// Do a XPath lookup for latitude and longitude
				String lat = XPath.selectText("/GeocodeResponse/result[1]/geometry/location/lat", doc);
				String lng = XPath.selectText("/GeocodeResponse/result[1]/geometry/location/lng", doc);

				// Make sure lat/lng matches per data type
				for (String latitude : Matcher.ClassOf(String.class).match(lat)) {
					for (String longitude : Matcher.ClassOf(String.class).match(lng)) {
						// Log Debug
						Logger.info("Address: %s, Latitude: %s, Longitude: %s", address, latitude, longitude);

						// Wrap latitude and longitude into a tuple
						return new Some(new Tuple<Double, Double>(Double.valueOf(latitude), Double.valueOf(longitude)));
					}
				}
			}

			// We haven't been able to find a geocode for this address
			Logger.info("No geocode found for address: %s", address);
			return new None<Tuple<Double, Double>>();
		}
	}

}