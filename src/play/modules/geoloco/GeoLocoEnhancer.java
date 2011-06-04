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

import java.lang.annotation.Annotation;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;
import play.Logger;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;
import play.db.jpa.JPABase;

/**
 * The Class GeoLocoEnhancer.
 */
public class GeoLocoEnhancer extends Enhancer {

	/**
	 * Use enhancer to define the implementation for the static methods defined
	 * on the model classes (base geo model class - GeoLocoModel).
	 * 
	 * @see play.classloading.enhancers.Enhancer#enhanceThisClass(play.classloading.ApplicationClasses.ApplicationClass)
	 */
	@Override
	public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
		// Get Class
		CtClass ctClass = this.makeClass(applicationClass);
        
		// Make sure it's a GeoLoco model class
		if (!this.hasAnnotation(ctClass, play.modules.geoloco.GeoModel.class.getCanonicalName())) {
			return;
		}

		// Get Entity Name
		String entityName = ctClass.getName();
		Logger.info("Class has been identified as a geo-enabled entity: %s", entityName);

		// Add method - findByProximity
		String radiusMethod = "public static java.util.List findByProximity(double latitude, double longitude, int results, double distance) { ";
		String radiusMethodImpl = "return play.modules.geoloco.GeoLocoAdapter.findByProximity(play.db.jpa.JPA.em(), \"" + entityName + "\", latitude, longitude, results, distance); ";
		radiusMethod = radiusMethod + radiusMethodImpl + " }";
		javassist.CtMethod m1 = javassist.CtMethod.make(radiusMethod, ctClass);
		ctClass.addMethod(m1);
		
		// Add method - findByBoundingBox
		String bboxMethod = "public static java.util.List findByBoundingBox(double latitudeSouth, double latitudeNorth, double longitudeEast, double longitudeWest) { ";
		String bboxMethodImpl = "return play.modules.geoloco.GeoLocoAdapter.findByBoundingBox(play.db.jpa.JPA.em(), \"" + entityName + "\", latitudeSouth, latitudeNorth, longitudeEast, longitudeWest); ";
		bboxMethod = bboxMethod + bboxMethodImpl + " }";
		javassist.CtMethod m2 = javassist.CtMethod.make(bboxMethod, ctClass);
		ctClass.addMethod(m2);
		
		// Done.
		applicationClass.enhancedByteCode = ctClass.toBytecode();
		ctClass.defrost();
	}
}
