package papertoolkit.units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * The Meters unit is useful for scientists and people who are not American. =)
 */
public class Meters extends Units {

	/**
	 * Identity Element, representing one meter.
	 */
	public static final Units ONE = new Meters(1.0);

	/**
	 * One Meter.
	 */
	public Meters() {
		super(1);
	}

	/**
	 * @param m
	 */
	public Meters(double m) {
		super(m);
	}

	/**
	 * @see papertoolkit.units.Units#getNumberOfUnitsInOneInch()
	 */
	protected double getNumberOfUnitsInOneInch() {
		return .0254;
	}

}
