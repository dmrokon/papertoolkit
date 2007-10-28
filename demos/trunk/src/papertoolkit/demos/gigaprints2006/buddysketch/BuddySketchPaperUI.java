package papertoolkit.demos.gigaprints2006.buddysketch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import papertoolkit.events.PenEvent;
import papertoolkit.events.handlers.InkHandler;
import papertoolkit.events.handlers.ClickHandler.ClickAdapter;
import papertoolkit.paper.Region;
import papertoolkit.paper.Sheet;
import papertoolkit.paper.regions.ImageRegion;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.units.Inches;
import papertoolkit.units.Pixels;
import papertoolkit.util.DebugUtils;


/**
 * <p>
 * The Sheet is 44" wide by 24" tall (within arms reach while seated). One the right, there are one
 * or two columns of cool photos that people can share by tapping the pen. The main area is just a
 * huge patterned area where we can draw...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BuddySketchPaperUI extends Sheet {

	private InkHandler inkWell;

	private BuddySketch sketchApp;

	/**
	 * @param sketch
	 * 
	 */
	public BuddySketchPaperUI(BuddySketch sketch) {
		super(44, 24); // inches
		sketchApp = sketch;
		addDrawingRegion();
		addPhotoRegions();
	}

	/**
	 * 
	 */
	private void addDrawingRegion() {
		Region drawingRegion = new Region("Drawing Region", 0.5, 0.5, 39.5, 23);
		inkWell = new InkHandler() {
			public void handleInkStroke(PenEvent event, InkStroke mostRecentStroke) {
				DebugUtils.println("Content Arrived");
				DebugUtils.println("Num Strokes Total: " + inkWell.getNumStrokesCollected());

				// display this ink in our local GUI...
				sketchApp.sendInkToGUI(inkWell.getNewInkOnly());
			}
		};
		drawingRegion.addEventHandler(inkWell);

		drawingRegion.addEventHandler(new ClickAdapter() {
			public void clicked(PenEvent e) {
				DebugUtils.println("Drawing Region CLICK " + e.getPercentageLocation());
				DebugUtils.println("Drawing Region CLICK "
						+ e.getPercentageLocation().getX().getValueIn(Pixels.ONE) + ", "
						+ e.getPercentageLocation().getY().getValueIn(Pixels.ONE));
			}
		});

		addRegion(drawingRegion);

	}

	/**
	 * 
	 */
	private void addPhotoRegions() {
		Map<String, String> files = new HashMap<String, String>();
		files.put("YellowLady", "211214686_3f38bdd9fe.jpg");
		files.put("Snowboarder", "128831488_cf9a83d4d3.jpg");
		files.put("Pouty Girl", "177501140_f64c1c2049.jpg");
		files.put("In Flight", "137429670_6832a419f9.jpg");
		files.put("Horses", "114424725_42d946fc01.jpg");
		files.put("Hand", "247630809_32ef00a555.jpg");
		files.put("Rainbow", "211786854_38da366538.jpg");

		final File parentDir = new File("data/Flickr/Twistr/normal/");

		Inches xInches = new Inches(40.25);
		double yInches = 0.5;
		double yPaddingToNextPhoto = 0.12;

		for (String desc : files.keySet()) {
			final String fileName = files.get(desc);
			final File imgFile = new File(parentDir, fileName);
			final ImageRegion imgRegion = new ImageRegion(desc, imgFile, //
					xInches, new Inches(yInches));
			imgRegion.setScale(0.5, 0.5);

			addRegion(imgRegion);

			yInches += imgRegion.getHeightVal();

			final Region retrieveOrHide = new Region(desc, xInches, new Inches(yInches), imgRegion
					.getWidth(), new Inches(0.6));
			retrieveOrHide.addEventHandler(new ClickAdapter() {
				public void clicked(PenEvent e) {
					DebugUtils.println("Clicked " + fileName + " at " + e.getPercentageLocation());
					sketchApp.displayImage(imgFile);
				}
			});
			addRegion(retrieveOrHide);

			// all in inches!
			yInches += retrieveOrHide.getHeight().getValue() + yPaddingToNextPhoto;
		}

	}
}