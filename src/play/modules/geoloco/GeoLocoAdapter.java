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

import java.util.*;

import javax.persistence.EntityManager;

import play.Logger;
import play.db.jpa.JPABase;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.GeocellQuery;
import com.beoui.geocell.model.*;
import com.beoui.geocell.*;
import javax.persistence.*;

/**
 * The Class GeoLocoAdapter.
 */
public class GeoLocoAdapter {
	
	/**
	 * Find by bounding box. Incoming data: latitude and longitude of south-west and north-east points.
	 *
	 * @param <T> the generic type
	 * @param em the em
	 * @param entityName the entity name
	 * @param latS the lat s
	 * @param latN the lat n
	 * @param latE the lat e
	 * @param latW the lat w
	 * @return the list
	 */
	public static final <T extends JPABase> List<T> findByBoundingBox(EntityManager em, String entityName, double latS, double latN, double lonE, double lonW) {
		// Transform this to a bounding box
		com.beoui.geocell.model.BoundingBox bb = new com.beoui.geocell.model.BoundingBox(latN, lonE, latS, lonW);

        // Calculate the geocells list to be used in the queries (optimize list of cells that complete the given bounding box)
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);
        Logger.info("Bounding Box Cells: %s", cells);

        // Execute Query
        String query = String.format("SELECT w FROM %s w JOIN w.geocells s WHERE s in (:cells)", entityName);
	    return em.createQuery(query).setParameter("cells", cells).getResultList();
	}

	/**
	 * Proximity search.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param pm
	 *            the pm
	 * @param entityClass
	 *            the entity class
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
	public static final <T extends JPABase> List<T> findByProximity(EntityManager em, String entityName, double latitude, double longitude, int maxResults, double maxDistance) {
		try {
			// Log Debug
			Logger.info("Entity Manager: " + em);
			Logger.info("Entity Name: " + entityName);
			Logger.info("Latitude: " + latitude);
			Logger.info("Longitude: " + longitude);
			Logger.info("Max Results: " + maxResults);
			Logger.info("Max Distance: " + maxDistance);
			
			// Define Params
			Point pt = new Point(latitude, longitude);
			GeocellQuery query = getQuery(entityName);
			Class clazz = Class.forName(entityName);
			
			// Check Values
			if ( clazz == null ) {
				throw new RuntimeException("Invalid Entity Name: " + entityName);
			}
			
			// Execute Query
			return GeocellManager.proximitySearch(pt, maxResults, maxDistance, clazz, query, em);

		} catch (Throwable t) {
			Exceptions.errorAndThrow(t);
			return null;
		}
	}
	
	/**
	 * Gets the query.
	 *
	 * @return the query
	 */
	private static GeocellQuery getQuery(String entityName) {
		List<Object> params = new java.util.ArrayList<Object>();
       // GeocellQuery baseQuery = new GeocellQuery("", "", params);
		GeocellQuery baseQuery = new GeocellQuery("SELECT e FROM " + entityName + " e join fetch e.geocells geocells");
        return baseQuery;
	}
}
