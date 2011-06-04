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

import java.util.Map;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.db.jpa.GenericModel;
import play.db.jpa.JPABase;

/**
 * The Class GeoLocoPlugin.
 */
public class GeoLocoPlugin extends PlayPlugin {

	/**
	 * Use this enhancer to define the GeoSpatial find methods on the model
	 * classes
	 * 
	 * @see play.PlayPlugin#enhance(play.classloading.ApplicationClasses.ApplicationClass)
	 */
	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {
		try {
			new GeoLocoEnhancer().enhanceThisClass(applicationClass);
		} catch (Throwable t) {
			Exceptions.errorAndThrow(t);
		}
	}

}
