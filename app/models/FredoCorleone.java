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
package models;

import javax.persistence.Entity;

import play.modules.geoloco.GeoLocoModel;
import play.modules.geoloco.GeoModel;

/**
 * The Class FredoCorleone.
 */
@Entity
@GeoModel
public class FredoCorleone extends GeoLocoModel {

	/** The address. */
	public String address;

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address
	 *            the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the city.
	 * 
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the zip.
	 * 
	 * @return the zip
	 */
	public String getZip() {
		return this.zip;
	}

	/**
	 * Sets the zip.
	 * 
	 * @param zip
	 *            the new zip
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/** The city. */
	public String city;

	/** The state. */
	public String state;

	/** The zip. */
	public String zip;

	/**
	 * Full Address
	 * 
	 * @see geoloco.GeoLocoModel#getLocationAddress()
	 */
	@Override
	public String getLocationAddress() {
		return String.format("%s %s, %s %s", this.address, this.city, this.state, this.zip);
	}

}
