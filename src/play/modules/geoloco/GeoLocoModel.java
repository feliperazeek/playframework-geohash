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

import java.util.Collections;
import java.util.Iterator;
import java.util.*;

import javax.persistence.ElementCollection;
import javax.persistence.MappedSuperclass;
import javax.persistence.*;

import play.Logger;
import play.db.jpa.JPABase;
import play.db.jpa.*;
import play.libs.F.Matcher;
import play.libs.F.Option;
import play.libs.F.Tuple;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.LocationCapable;
import com.beoui.geocell.model.Point;

/**
 * The Class GeofredoModel.
 */
@SuppressWarnings("deprecation")
@MappedSuperclass
public abstract class GeoLocoModel extends Model implements com.beoui.geocell.model.LocationCapable {

	/** The latitude. */
	@com.beoui.geocell.annotations.Latitude
	public Double latitude;

	/** The longitude. */
	@com.beoui.geocell.annotations.Longitude
	public Double longitude;
	
	/** The geocells. */
	//@OneToMany
	@ElementCollection
	@com.beoui.geocell.annotations.Geocells
	public Set<String> geocells = new HashSet();
	
	/**
	 * Get Location
	 * 
	 * @see com.beoui.geocell.model.LocationCapable#getLocation()
	 */
	public Point getLocation() {
		return new Point(latitude, longitude);
	}
	
	/**
	 * Get Location Key
	 * 
	 * @see com.beoui.geocell.model.LocationCapable#getKeyString()
	 */
	public String getKeyString() {
		return getLocationAddress();
	}

	/**
	 * Proximity search.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param latitude
	 *            the latitude
	 * @param longitude
	 *            the longitude
	 * @param maxResults
	 *            the max results
	 * @param maxDistance
	 *            the max distance
	 * @return the list
	 */
	public static final <T extends JPABase> List<T> findByProximity(double latitude, double longitude, int maxResults, double maxDistance) {
		throw new UnsupportedOperationException("Please make sure GeoLocoEnhancer did its job! He can be lazy sometimes!");
	}
	
	/**
	 * Find by bounding box.
	 *
	 * @param <T> the generic type
	 * @param latitudeSouth the latitude south
	 * @param latitudeNorth the latitude north
	 * @param longitudeEast the longitude east
	 * @param longitudeWest the longitude west
	 * @return the list
	 */
	public static final <T extends JPABase> List<T> findByBoundingBox(double latitudeSouth, double latitudeNorth, double longitudeEast, double longitudeWest) {
		throw new UnsupportedOperationException("Please make sure GeoLocoEnhancer did its job! He can be lazy sometimes!");
	}

	/**
	 * Gets the location address.
	 * 
	 * @return the location address
	 */
	public abstract String getLocationAddress();

	/**
	 * Geo Lookup and Save Model
	 * 
	 * @see play.db.jpa.GenericModel#save()
	 */
	@Override
	public <T extends JPABase> T save() {
		// Basically here we are going to get the address defined by
		// getLocationAddress(), lookup the geocode, define the list of geocells
		// from the lat/lng coords and call save() on the super
		try {
			// Request Geocode
			Geocoder geocoder = GeocodeProvider.get();
			Option<Tuple<Double, Double>> geocode = geocoder.geocode(this.getLocationAddress());

			// Log Debug
			Logger.info("Address: %s, Geocode: %s", this.getLocationAddress(), geocode);

			// Define Iterator
			Iterator<Tuple<Double, Double>> iterator = geocode.iterator();
			while (iterator.hasNext()) {
				// Get Tuple
				Tuple<Double, Double> some = iterator.next();
				for (Tuple<Double, Double> t : Matcher.ClassOf(Tuple.class).match(some)) {
					// Set Lat/Lng
					this.latitude = t._1;
					this.longitude = t._2;
					Logger.info("Latitude: %s", this.latitude);
					Logger.info("Longitude: %s", this.longitude);

					// Define Geocells (GeoHash)
					Point p = new Point(this.latitude, this.latitude);
					for (String c : GeocellManager.generateGeoCell(p)) {
						this.geocells.add(c);
					}
					Logger.info("Geocells (%s) - Latitude: %s, Longitude: %s", this.geocells, this.latitude, this.longitude);

				}
			}
		} catch (Throwable t) {
			// Ok go ahead bitch out
			Exceptions.errorsWithNoException(t);
		}

		// Do what you gotta do my friend
		return (T)super.save();
	}

	/**
	 * Get List of Geocells (GeoHash)
	 * 
	 * @see com.beoui.geocell.model.LocationCapable#getGeocells()
	 */
	public List<String> getGeocells() {
		List<String> l = new ArrayList<String>();
		for (String s : this.geocells) {
			l.add(s);
		}
		return l;
	}
	
	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public Double getLatitude() {
		return this.latitude;
	}
	
	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public Double getLongitude() {
		return this.longitude;
	}
	
	/**
	 * Sets the latitude.
	 *
	 * @param d the new latitude
	 */
	public void setLatitude(Double d) {
		this.latitude = d;
	}
	
	/**
	 * Sets the longitude.
	 *
	 * @param d the new longitude
	 */
	public void setLongitude(Double d) {
		this.longitude = d;
	}
}
