package com.hacktoolkit.android.maps;

import com.hacktoolkit.android.constants.GeoConstants;

public class MapUtils {
	/**
	 * Calculates the Cartesian distance of two points represented as lat-long pairs
	 *
	 * It's much harder to calculate geographical distances with precision on the geographic coordinate system,
	 * because the Earth is spherical and not flat.
	 * Assuming a 2D model of the earth would produce inaccurate results over large distances or areas
	 * 
	 * http://en.wikipedia.org/wiki/Geographical_distance
	 * http://en.wikipedia.org/wiki/Euclidean_distance
	 * 
	 * For some simpler purposes, and within smaller areas, a Cartesian model can be used
	 * Good old middle school math and Pythagorean theorem will do:
	 *   (a^2 + b^2 = c^2)
	 *   or
	 *   distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
	 * 
	 * Since the sqrt operation is relatively expensive, we'll just keep the squares
	 * 
	 * @param previousLatitude
	 * @param previousLongitude
	 * @param newLatitude
	 * @param newLongitude
	 * @return the sum of the squares, which we will normalize into a score (?)
	 */
	public static double geographicalCartesianDistance(
			double previousLatitude,
			double previousLongitude,
			double newLatitude,
			double newLongitude) {
		double deltaX = Math.abs(newLatitude - previousLatitude);
		double deltaY = Math.abs(newLongitude - previousLongitude);
		double sumOfSquares = deltaX * deltaX + deltaY * deltaY;
		return sumOfSquares;
	}
	
	/**
	 * 
	 * @return
	 */
	/**
	 * Calculates approximate geographical distance using great circle formula
	 * 
	 * http://en.wikipedia.org/wiki/Great-circle_distance
	 *   The Earth is nearly spherical (see Earth radius) so great-circle distance formulas give the distance between points on
	 *   the surface of the Earth (as the crow flies) correct to within 0.5% or so.

	 *   Earth radius is the distance from Earth's center to its surface, about 6,371 kilometers (3,959 mi).
	 *   This length is also used as a unit of distance, especially in astronomy and geology, where it is usually denoted by R_\oplus.
	 * 
	 * Let \phi_1,\lambda_1 and \phi_2,\lambda_2 be the geographical latitude and longitude of two points 1 and 2,
	 *   and \Delta\phi,\Delta\lambda their absolute differences;
	 * then \Delta\sigma, the central angle between them, is given by the spherical law of cosines:
	 *   \Delta\sigma=\arccos\bigl(\sin\phi_1\sin\phi_2+\cos\phi_1\cos\phi_2\cos\Delta\lambda\bigr).
	 *   The distance d, i.e. the arc length, for a sphere of radius r and \Delta\sigma given in
	 *   d = r \, \Delta\sigma.
	 * 
	 * @param previousLatitude
	 * @param previousLongitude
	 * @param newLatitude
	 * @param newLongitude
	 * @return distance in kilometers
	 */
	public static double geographicalGreatCircleDistance(
			double previousLatitude,
			double previousLongitude,
			double newLatitude,
			double newLongitude) {
		// absolute difference in latitude
		//double deltaPhi = Math.abs(newLatitude - previousLatitude);
		// absolute difference in longitude
		double deltaLambda = Math.abs(newLongitude - previousLongitude);
		// central angle
		double deltaSigma = Math.acos((Math.sin(previousLatitude) * Math.sin(newLatitude)) +
				(Math.cos(previousLatitude) * Math.cos(newLatitude) * Math.cos(deltaLambda)));
		// arc length
		double distanceKm = deltaSigma * GeoConstants.EARTH_RADIUS_MEAN_KM;
		return distanceKm;
	}
}
