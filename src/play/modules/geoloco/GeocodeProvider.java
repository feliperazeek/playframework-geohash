package play.modules.geoloco;

import play.modules.geoloco.Geocoder.GoogleGeocoder;
import play.Play;
import play.libs.F.Matcher;

/**
 * The Enum GeocodeProvider.
 */
public enum GeocodeProvider {

	/** The google. */
	google(GoogleGeocoder.class);

	/** The geocoder. */
	protected Class<?> geocoder;

	/**
	 * Instantiates a new geocode provider.
	 * 
	 * @param geocoder
	 *            the geocoder
	 */
	private GeocodeProvider(Class<?> geocoder) {
		this.geocoder = geocoder;
	}

	/**
	 * Gets the.
	 * 
	 * @return the geocoder
	 */
	public static Geocoder get() {
		String provider = Play.configuration.getProperty("geoloco.geocoder", google.name());
		for (GeocodeProvider gp : GeocodeProvider.values()) {
			for (@SuppressWarnings("unused")
			String s : Matcher.String.and(Matcher.Equals(provider)).match(gp.name())) {
				try {
					return (Geocoder) gp.geocoder.newInstance();
				} catch (Throwable t) {
					throw new RuntimeException(t.fillInStackTrace());
				}
			}
		}
		return null;
	}

}