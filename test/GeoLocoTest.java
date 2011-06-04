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
import java.util.List;

import models.FredoCorleone;

import org.junit.Assert;
import org.junit.Test;

import play.Logger;
import play.libs.F.Option;
import play.libs.F.Tuple;
import play.modules.geoloco.GeocodeProvider;
import play.modules.geoloco.Geocoder;
import play.test.UnitTest;

/**
 * The Class GeofredoModelTest.
 */
public class GeoLocoTest extends UnitTest {

	/**
	 * Test geocoder.
	 */
	@Test
	public void testGeocoder() {
		// Geocode lookup with a valid address
		Geocoder geocoder = GeocodeProvider.get();
		Option<Tuple<Double, Double>> some = geocoder.geocode("22192 Majestic Woods Way Boca Raton, FL 33428");
		Logger.info("Geocode: %s", some);
		Assert.assertNotNull(some);
		Assert.assertTrue(some.isDefined());

		// Geocode lookup with an invalid address
		some = geocoder.geocode("some invalid address");
		Logger.info("Geocode: %s", some);
		Assert.assertNotNull(some);
		Assert.assertFalse(some.isDefined());
	}

	/**
	 * Test bounding box query.
	 */
	@Test
	public void testBoundingBoxQuery() {
		// Define Model
		FredoCorleone f = new FredoCorleone();
		f.address = "22192 Majestic Woods Way";
		f.city = "Boca Raton";
		f.state = "FL";
		f.zip = "33428";
		f = f.save();

		FredoCorleone f2 = new FredoCorleone();
		f2.address = "22192 Majestic Woods Way";
		f2.city = "Boca Raton";
		f2.state = "FL";
		f2.zip = "33428";
		f2 = f2.save();

		List<FredoCorleone> list = FredoCorleone.findByProximity(f.latitude, f.longitude, 1000, 0);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		Assert.assertTrue(list.contains(f));
	}

}
