package papertoolkit.pattern.coordinates.conversion;

import papertoolkit.pattern.TiledPattern;
import papertoolkit.units.PatternDots;
import papertoolkit.units.Percentage;
import papertoolkit.units.Units;
import papertoolkit.units.coordinates.Coordinates;
import papertoolkit.units.coordinates.PercentageCoordinates;
import papertoolkit.units.coordinates.StreamedPatternCoordinates;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Stores the bounds in physical (streaming) coordinates.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class TiledPatternCoordinateConverter implements PatternCoordinateConverter {

	private int bottomMostVoidSpaceInDots;

	private double clipH;

	private double clipW;

	private double clipX;

	private double clipY;

	/**
	 * The Width of a Tile, in dots.
	 */
	private int dotsPerTileHorizontal;

	/**
	 * The Height of a Tile, in dots.
	 */
	private int dotsPerTileVertical;

	/**
	 * Whether the region actually takes up only a small portion of this tiled pattern block.
	 */
	private boolean hasClippingBounds = false;

	/**
	 * Right Boundary in Physical Coordinates. Thus, even if your tiling looks like this: <blockquote><code>
	 *  [0][1][2]<br>
	 *  [3][4][5]
	 * </code></blockquote>
	 * 
	 * Your maxX could be huge because in reality your pattern space looks like this: <blockquote><code>
	 *  [0][1][2][3][4][5]
	 * </code></blockquote>
	 * 
	 * Similarly, maxY might be not so big after all...
	 */
	private double maxX;

	/**
	 * Bottom Boundary in Physical coordinates.
	 */
	private double maxY;

	/**
	 * Difference in X Values between subsequent tiles...
	 */
	private double numDotsHorizontalBetweenTiles;

	/**
	 * Difference in Y Values between subsequent tiles...
	 */
	private double numDotsVerticalBetweenTiles;

	/**
	 * numTilesAcross x numTilesDown. This is the total number of tiles in our configuration.
	 */
	private int numTiles;

	/**
	 * <blockquote><code>
	 *  [0][1][2]<br>
	 *  [3][4][5]
	 * </code></blockquote>
	 * 
	 * 3 tiles across in the above example.
	 */
	private int numTilesAcross;

	/**
	 * 2 tiles down in the above example.
	 */
	private int numTilesDown;

	/**
	 * The total width of this tiled pattern.
	 */
	private double numTotalDotsAcross;

	/**
	 * The total width of this tiled pattern.
	 */
	private PatternDots numTotalDotsAcrossObj;

	/**
	 * The total height of this tiled pattern.
	 */
	private double numTotalDotsDown;

	/**
	 * The total height of this tiled pattern.
	 */
	private PatternDots numTotalDotsDownObj;

	/**
	 * 
	 */
	private double originX;

	/**
	 * 
	 */
	private double originY;

	/**
	 * Which Region is this Converter tied to?
	 */
	private String regionName;

	/**
	 * 
	 */
	private int rightMostVoidSpaceInDots;

	/**
	 * Name the top-left tile anything you want.
	 */
	private int startingTile;

	/**
	 * 
	 */
	private double tileHeightIncludingPadding;

	/**
	 * 
	 */
	private double tileWidthIncludingPadding;

	/**
	 * A constructor for when you need to set the information later (The Lazy Approach).
	 * 
	 * @param regionName
	 *            the Region that this coordinate converter was created for. The reason we don't pass in the
	 *            whole region object is because this converter WILL be serialized and unserialized. Due to
	 *            XStream's limitations, we will have to resolve the region at runtime using the region's name
	 *            instead.
	 */
	public TiledPatternCoordinateConverter(String regionName) {
		this(regionName, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	/**
	 * Used when you are creating a converter that lives on a SINGLE PATTERNED TILE.
	 */
	public TiledPatternCoordinateConverter(String theRegionName, //
			double leftMostPatternXCoord, double topMostPatternYCoord, int dotsAcross, int dotsDown) {
		setRegionName(theRegionName);
		setStartingTile(0);
		setTileConfiguration(1, 1);
		setTileSizeInDots(dotsAcross, dotsDown);
		setOriginInDots(leftMostPatternXCoord, topMostPatternYCoord);
		setTotalSizeInDots(dotsAcross, dotsDown);
		setTileToTileOffsetInDots(0, 0);
	}

	/**
	 * <p>
	 * This object deals with physical coordinates (the type that you get when you stream coordinates from the
	 * Nokia SU-1B). They are all huge numbers, but we store them in PatternDots objects. Although we can
	 * convert the PatternDots objects into other Units, it doesn't really make sense, as the dots are
	 * specified in the world of Anoto's gargantuan pattern space. For example, if you converted the xOrigin
	 * to inches, you would get a beast of a number.
	 * </p>
	 * <p>
	 * We precompute the boundaries so that we can do contains(...) tests faster.
	 * </p>
	 * 
	 * @param theRegionName
	 *            The region that this converter was created for.
	 * @param startTile
	 *            the number that you assign to the top-left tile
	 * @param nTilesAcross
	 *            width of this tile configuration, in # of tiles
	 * @param nTilesDown
	 *            height of this tile configuration, in # of tiles
	 * @param dotsPerTileHoriz
	 *            width of each tile, in # dots
	 * @param dotsPerTileVert
	 *            height of each tile, in # dots
	 * @param leftMostPatternX
	 *            left boundary in pattern dots
	 * @param topMostPatternY
	 *            top boundary, in pattern dots
	 * @param numDotsAcross
	 *            width of entire region in dots, not including the between-tile padding
	 * @param numDotsDown
	 *            height of entire region in dots, not including the padding in between tiles
	 * @param numHorizDotsBetweenTiles
	 *            the horizontal padding between adjacent tiles
	 * @param numVertDotsBetweenTiles
	 *            the vertical padding between adjacent tiles (tends to be 0, in our experience)
	 */
	public TiledPatternCoordinateConverter(String theRegionName, int startTile, int nTilesAcross,
			int nTilesDown, int dotsPerTileHoriz, int dotsPerTileVert, double numHorizDotsBetweenTiles,
			double numVertDotsBetweenTiles, double leftMostPatternX, double topMostPatternY,
			double numDotsAcross, double numDotsDown) {
		setRegionName(theRegionName);
		setStartingTile(startTile);
		setTileConfiguration(nTilesAcross, nTilesDown);
		setTileSizeInDots(dotsPerTileHoriz, dotsPerTileVert);
		setOriginInDots(leftMostPatternX, topMostPatternY);
		setTotalSizeInDots(numDotsAcross, numDotsDown);
		setTileToTileOffsetInDots(numHorizDotsBetweenTiles, numVertDotsBetweenTiles);
	}

	/**
	 * To make it easier to use this class, this method will be called EVERY TIME one of the input values is
	 * updated, to keep internal state consistent.
	 * 
	 * Unfortunately, have this flexible design, the main constructor will call this method a total of four
	 * times. The values will be finally correct once the last call, to setTileToTileOffsetInDots(...) is
	 * called.
	 * 
	 * ASSUMPTION: Either the dots between tiles is larger than a single tile OR it is 0. Weird things can
	 * happen if the tiles are staggered, such that the dots between tiles is smaller than the width or height
	 * of a tile.
	 * 
	 * This is an OK assumption because in all the Anoto pattern we have seen, Y values don't seem to change
	 * from page to page.
	 */
	private void calculateCachedDimensions() {

		// set the maxX and tileHeight
		if (numTilesAcross == 1) {
			tileWidthIncludingPadding = numTotalDotsAcross;
			maxX = originX + numTotalDotsAcross;
		} else if (numDotsHorizontalBetweenTiles < dotsPerTileHorizontal) { // dX = 0
			tileWidthIncludingPadding = dotsPerTileHorizontal;
			maxX = originX + dotsPerTileHorizontal - rightMostVoidSpaceInDots; // only one
			// horizontal
			// tile
		} else { // bigger than the width
			tileWidthIncludingPadding = numDotsHorizontalBetweenTiles;
			maxX = originX + numDotsHorizontalBetweenTiles * numTiles - rightMostVoidSpaceInDots;
		}

		// set the maxY and tileHeight
		if (numTilesDown == 1) {
			tileHeightIncludingPadding = numTotalDotsDown;
			maxY = originY + numTotalDotsDown;
		} else if (numDotsVerticalBetweenTiles < dotsPerTileVertical) { // dY = 0
			// only one vertical tile, like bnet's pattern
			tileHeightIncludingPadding = dotsPerTileVertical;
			maxY = originY + dotsPerTileVertical - bottomMostVoidSpaceInDots;
		} else { // bigger than the height
			tileHeightIncludingPadding = numDotsVerticalBetweenTiles;
			maxY = originY + numDotsVerticalBetweenTiles * numTiles - bottomMostVoidSpaceInDots;
		}

		// DebugUtils.println(regionName);
		// DebugUtils.println("Void Space in X: " + rightMostVoidSpaceInDots);
		// DebugUtils.println("Void Space in Y: " + bottomMostVoidSpaceInDots);
	}

	/**
	 * For performance, we precompute the boundaries and store just those numbers. This method's likely faster
	 * than the other contains test, especially if you already have the x and y values and do not need to
	 * create a StreamedPatternLocation object.
	 * 
	 * @param xValPatternDots
	 *            x value of the location, in PatternDots (physical/streamed coordinates)
	 * @param yValPatternDots
	 *            y value of the location, in PatternDots (physical/streamed coordinates)
	 * @return
	 * 
	 * 
	 * WARNING: POSSIBLY BUGGY, Due to New way of iterating through Pattern
	 * 
	 * TODO: Fix Bug ~Here....
	 * 
	 * TODO: Implement a FASTER reject. Contains is called many many times. We want to reject as soon as is
	 * possible. Also, rejects happen a lot more than accepts.
	 */
	public boolean contains(final double xValPatternDots, final double yValPatternDots) {
		// has to be to the right of the leftmost border
		final boolean insideLeftBoundary = xValPatternDots >= originX;

		// has to be below the top border
		final boolean insideTopBoundary = yValPatternDots >= originY;

		// has to be to the left of the rightmost border
		final boolean insideRightBoundary = xValPatternDots < maxX;

		// has to be above the bottom border
		final boolean insideBottomBoundary = yValPatternDots < maxY;

		// has to NOT fall in between the gaps between pages...
		final boolean notInHorizontalGap = ((xValPatternDots - originX) % tileWidthIncludingPadding) < dotsPerTileHorizontal;
		final boolean notInVerticalGap = ((yValPatternDots - originY) % tileHeightIncludingPadding) < dotsPerTileVertical;

		// if there is a clipping bounds, it has to fall inside the clip
		boolean inClip = true;
		if (hasClippingBounds) {
			// DebugUtils.println("Origin: " + originX + ", " + originY);
			// DebugUtils.println("Incoming X,Y: " + xValPatternDots + ", " + yValPatternDots);

			// basically a call to get relative location

			final double xOffset = xValPatternDots - originX;
			final double yOffset = yValPatternDots - originY;

			// DebugUtils.println("Relative X & Y: " + xOffset + ", " + yOffset);

			final int tileNumHoriz = getTileNumHorizontal(xValPatternDots);
			final int tileNumVert = getTileNumVertical(yValPatternDots);

			// DebugUtils.println("Tile X & Y: " + tileNumHoriz + ", " + tileNumVert);

			final double xRelativeToTile = xOffset - (tileNumHoriz * numDotsHorizontalBetweenTiles);
			final double yRelativeToTile = yOffset - (tileNumVert * numDotsVerticalBetweenTiles);

			final int tileOffset = Math.max(tileNumHoriz, tileNumVert);

			// DebugUtils.println("Tile Offset: " + tileOffset);

			final int tileRow = tileOffset / numTilesAcross;
			final int tileCol = tileOffset % numTilesAcross;

			// how many dots are we in x & y
			// this is the coordinate in absolute dots, relative to the upper left corner of the region
			final double totalDotsX = tileCol * dotsPerTileHorizontal + xRelativeToTile;
			final double totalDotsY = tileRow * dotsPerTileVertical + yRelativeToTile;

			// DebugUtils.println(totalDotsX + " in X: [" + clipX + " " + (clipX + clipW) + "]");
			// DebugUtils.println(totalDotsY + " in Y: [" + clipY + " " + (clipY + clipH) + "]");

			inClip = (totalDotsX >= clipX) && (totalDotsX <= clipX + clipW) && (totalDotsY >= clipY)
					&& (totalDotsY <= clipY + clipH);
			// DebugUtils.println("In Clip? " + inClip);
		}

		return insideLeftBoundary && insideTopBoundary && insideRightBoundary && insideBottomBoundary
				&& notInHorizontalGap && notInVerticalGap && inClip;
	}

	/**
	 * If you have the values and do not need to create a StreamedPatternCoordinates object, use the other
	 * contains(...) method.
	 * 
	 * @param location
	 * @return whether the tile configuration contains this location
	 */
	public boolean contains(StreamedPatternCoordinates location) {
		final double xTestVal = location.getXVal();
		final double yTestVal = location.getYVal();

		return contains(xTestVal, yTestVal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.pattern.coordinates.conversion.PatternCoordinateConverter#getArea()
	 */
	public double getArea() {
		return numTotalDotsAcross * numTotalDotsDown;
	}

	/**
	 * The name of the SINGLE REGION that this coordinate converter handles.
	 * 
	 * @return
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * Convert the input coordinate into a percentage location relative to this tile configuration (a region
	 * that has tiled pattern)
	 * 
	 * @return
	 */
	public PercentageCoordinates getRelativeLocation(StreamedPatternCoordinates coord) {

		final double xOffset = coord.getXVal() - originX;
		final double yOffset = coord.getYVal() - originY;

		final double xRelativeToTile = xOffset
				- (getTileNumHorizontal(coord) * numDotsHorizontalBetweenTiles);
		final double yRelativeToTile = yOffset - (getTileNumVertical(coord) * numDotsVerticalBetweenTiles);

		final int tileOffset = getTileNumber(coord) - startingTile;

		final int tileRow = tileOffset / numTilesAcross;
		final int tileCol = tileOffset % numTilesAcross;

		// how many dots are we in x & y
		// this is the coordinate in absolute dots, relative to the upper left corner of the region
		final double totalDotsX = tileCol * dotsPerTileHorizontal + xRelativeToTile;
		final double totalDotsY = tileRow * dotsPerTileVertical + yRelativeToTile;

		if (hasClippingBounds) {
			// DebugUtils.println(totalDotsX + " in X: [" + clipX + " " + (clipX + clipW) + "]");
			// DebugUtils.println(totalDotsY + " in Y: [" + clipY + " " + (clipY + clipH) + "]");

			final double pctX = (totalDotsX - clipX) / clipW * 100;
			final double pctY = (totalDotsY - clipY) / clipH * 100;

			return new PercentageCoordinates( // 
					new Percentage(pctX, numTotalDotsAcrossObj), // fraction of width
					new Percentage(pctY, numTotalDotsDownObj)); // fraction of height
		} else {
			final double pctX = totalDotsX / numTotalDotsAcross * 100;
			final double pctY = totalDotsY / numTotalDotsDown * 100;

			return new PercentageCoordinates( // 
					new Percentage(pctX, numTotalDotsAcrossObj), // fraction of width
					new Percentage(pctY, numTotalDotsDownObj)); // fraction of height
		}
	}

	/**
	 * It tries to calculate the tile number given an input coordinate.
	 * 
	 * <blockquote><code>
	 *  [0][1][2]<br>
	 *  [3][4][5]
	 * </code></blockquote>
	 * 
	 * This should work if the pattern space is short and wide, or thin and tall...
	 * 
	 * @param coord
	 * @return the tile number of this coordinate. Returns -1 if this coordinate is not contained by this tile
	 *         configuration.
	 */
	public int getTileNumber(StreamedPatternCoordinates coord) {
		if (!contains(coord)) {
			return -1;
		}

		// using the x or y coordinate, determine the tile number
		// depending on the orientation of the pattern, these numbers may not be the same (i.e., one
		// of them may be zero while the other is not)
		final int tileNumHoriz = getTileNumHorizontal(coord);
		final int tileNumVert = getTileNumVertical(coord);

		return startingTile + Math.max(tileNumHoriz, tileNumVert);
	}

	/**
	 * Works on Raw Coordinates
	 * 
	 * @param coord
	 * @return
	 */
	private int getTileNumHorizontal(StreamedPatternCoordinates coord) {
		return (int) Math.floor((coord.getXVal() - originX) / tileWidthIncludingPadding);
	}

	private int getTileNumHorizontal(double xVal) {
		return (int) Math.floor((xVal - originX) / tileWidthIncludingPadding);
	}

	/**
	 * @param coord
	 * @return
	 */
	private int getTileNumVertical(StreamedPatternCoordinates coord) {
		return (int) Math.floor((coord.getYVal() - originY) / tileHeightIncludingPadding);
	}

	private int getTileNumVertical(double yVal) {
		return (int) Math.floor((yVal - originY) / tileHeightIncludingPadding);
	}

	/**
	 * @param origin
	 * @param width
	 * @param height
	 */
	private void setClippingBounds(Coordinates origin, Units width, Units height) {
		hasClippingBounds = true;
		clipX = origin.getX().getValueInPatternDots();
		clipY = origin.getY().getValueInPatternDots();
		clipW = width.getValueInPatternDots();
		clipH = height.getValueInPatternDots();
		DebugUtils.println("The Clipping Bounds is set to: x=" + clipX + ", y=" + clipY + " w=" + clipW
				+ " h=" + clipH);
	}

	/**
	 * @param numDots
	 */
	private void setNumDotsFromBottomMostTilesThatAreNotIncluded(int numDots) {
		bottomMostVoidSpaceInDots = numDots;
		calculateCachedDimensions();
	}

	/**
	 * @param numDots
	 */
	private void setNumDotsFromRightMostTilesThatAreNotIncluded(int numDots) {
		rightMostVoidSpaceInDots = numDots;
		calculateCachedDimensions();
	}

	/**
	 * What is the physical coordinate of the top-left corner of the top-left tile?
	 * 
	 * @param leftMostPatternX
	 * @param topMostPatternY
	 */
	public void setOriginInDots(double leftMostPatternX, double topMostPatternY) {
		originX = leftMostPatternX;
		originY = topMostPatternY;

		// must update some internal state
		calculateCachedDimensions();
	}

	/**
	 * Reads in the information to define this coordinate converter.
	 * 
	 * @param p
	 *            the tiled pattern object produced by sheet renderers.
	 */
	public void setPatternInformationByReadingItFrom(TiledPattern p) {
		final int voidX = p.getNumDotsXPerFullTile() - p.getNumDotsXFromRightMostTiles();
		final int voidY = p.getNumDotsYPerFullTile() - p.getNumDotsYFromBottomMostTiles();

		setNumDotsFromRightMostTilesThatAreNotIncluded(voidX);
		setNumDotsFromBottomMostTilesThatAreNotIncluded(voidY);

		setOriginInDots(p.getOriginXInDots(), p.getOriginYInDots());
		setStartingTile(p.getInitialPatternFileNumber());
		setTileConfiguration(p.getNumTilesX(), p.getNumTilesY());
		setTileSizeInDots(p.getNumDotsXPerFullTile(), p.getNumDotsYPerFullTile());
		setTileToTileOffsetInDots(p.getNumHorizDotsBetweenTiles(), p.getNumVertDotsBetweenTiles());
		setTotalSizeInDots(p.getNumTotalColumns(), p.getNumTotalRows());

		// DebugUtils.println("");
		// System.out.println("=====================================");
		// System.out.println(p);
		// System.out.println(this);
		// System.out.println("=====================================");
	}

	/**
	 * The region only occupies a portion of the large block of pattern p. The clipping bounds defines the
	 * area that the region occupies.
	 * 
	 * @param pattern
	 * @param regionLocation
	 */
	public void setPatternInformationByReadingItFrom(TiledPattern p, Coordinates regionOrigin,
			Units regionWidth, Units regionHeight) {
		setPatternInformationByReadingItFrom(p);
		setClippingBounds(regionOrigin, regionWidth, regionHeight);
	}

	/**
	 * @param rName
	 *            the name of the region that this converter manages. It's important to keep your regions
	 *            uniquely identifiable.
	 */
	public void setRegionName(String rName) {
		regionName = rName;
	}

	/**
	 * The number of the first (top-left) tile; this is largely arbitrary, but _may_ correlate with a pattern
	 * file number N.pattern --> N as a starting tile number. This makes calculations easier for certain
	 * operations, such as finding out which page of a notebook your user has written on.
	 * 
	 * @param startTile
	 */
	public void setStartingTile(int startTile) {
		startingTile = startTile;
	}

	/**
	 * The number of tiles owned by this converter. Usually, this converter will map to a region on a sheet.
	 * This means that the tiledPatternConverter will need to know how many tiles of pattern the region
	 * contains. It will then help us find out where on the region we are.
	 * 
	 * @param nTilesAcross
	 * @param nTilesDown
	 */
	public void setTileConfiguration(int nTilesAcross, int nTilesDown) {
		numTilesAcross = nTilesAcross;
		numTilesDown = nTilesDown;
		numTiles = numTilesAcross * numTilesDown;
	}

	/**
	 * How wide and tall are these tiles? We assume uniform tiles (except for the rightmost and bottommost
	 * tiles)
	 * 
	 * @param dotsPerTileHoriz
	 * @param dotsPerTileVert
	 */
	public void setTileSizeInDots(int dotsPerTileHoriz, int dotsPerTileVert) {
		dotsPerTileHorizontal = dotsPerTileHoriz;
		dotsPerTileVertical = dotsPerTileVert;

		// must update some internal state
		calculateCachedDimensions();
	}

	/**
	 * @param numHorizDotsBetweenTiles
	 * @param numVertDotsBetweenTiles
	 */
	public void setTileToTileOffsetInDots(double numHorizDotsBetweenTiles, double numVertDotsBetweenTiles) {
		// what is the x offset between the origins of two adjacent tiles?
		// this is the width of a tile, plus a padding that anoto creates when you use the FDK to
		// generate pattern
		numDotsHorizontalBetweenTiles = numHorizDotsBetweenTiles;
		// what is the y offset between the origins of two adjacent tiles?
		// note that in the BNet pattern space, the y offset is 0
		numDotsVerticalBetweenTiles = numVertDotsBetweenTiles;

		// must update some internal state
		calculateCachedDimensions();
	}

	/**
	 * Set the size of this tile configuration, irrespective of the tiling. This SHOULD be able to calculate
	 * the extra space on the last tile that is NOT a part of our configuration.
	 * 
	 * @param numDotsAcross
	 * @param numDotsDown
	 */
	public void setTotalSizeInDots(double numDotsAcross, double numDotsDown) {
		// how wide and tall is the whole region?
		numTotalDotsAcross = numDotsAcross;
		numTotalDotsDown = numDotsDown;

		// save this for later calculations
		numTotalDotsAcrossObj = new PatternDots(numTotalDotsAcross);
		numTotalDotsDownObj = new PatternDots(numTotalDotsDown);

		calculateCachedDimensions();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TiledPatternCoordinateConverter {" + "x=" + originX + ", y=" + originY + " w="
				+ numTotalDotsAcrossObj + ", h=" + numTotalDotsDownObj + "}";
	}
}
