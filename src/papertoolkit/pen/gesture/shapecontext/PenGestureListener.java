package papertoolkit.pen.gesture.shapecontext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;

import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author Avi Robinson-Mosher
 */
public class PenGestureListener implements PenListener {

	private ArrayList<Gesture> gestures = new ArrayList<Gesture>();

	private ArrayList<ShapeContext> contexts = new ArrayList<ShapeContext>();

	private ArrayList<PenSample> samples = null;

	private int gestureThreshold = 4;

	private int remainingContexts = 0;

	private GestureDatabase database;

	private String author;

	private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	public void penDown(PenSample sample) {
		samples = new ArrayList<PenSample>();
	}

	public void setContexts(ArrayList<ShapeContext> contexts, int remainingContexts) {
		this.contexts = contexts;
		this.remainingContexts = remainingContexts;
	}

	public void penUp(PenSample sample) {
		final double categoryThreshold = 20;
		if (samples != null && samples.size() > gestureThreshold && remainingContexts > 0) {
			// normalize samples by first in space, maybe time
			double min_x = Double.MAX_VALUE, min_y = Double.MAX_VALUE;
			long min_t = Long.MAX_VALUE;
			for (PenSample inksample : samples) {
				min_x = Math.min(min_x, inksample.x);
				min_y = Math.min(min_y, inksample.y);
				min_t = Math.min(min_t, inksample.timestamp);
			}
			for (PenSample inksample : samples) {
				inksample.x -= min_x;
				inksample.y -= min_y;
				inksample.timestamp -= min_t;
			}
			System.out.println("Gesture accepted.");
			remainingContexts--;
			int index = -1;
			double distance = Double.MAX_VALUE;
			ShapeContext context = new ShapeContext(samples, author);
			contexts.add(context);
			if (remainingContexts == 0)
				System.out.println("Done reading gestures.");
		} else if (samples != null && samples.size() > gestureThreshold && database != null) {
			ShapeContext context = new ShapeContext(samples, "");
			database.test(context, true);
		}
		samples = null;
	}

	public void sample(PenSample sample) {
		PenSample inkSample = new PenSample(sample.x, sample.y, sample.force, sample.timestamp);
		samples.add(inkSample);
	}

	public void quillWrite(Writer writer) throws IOException {
		final String VERSION = "gdt 2.0";

		writer.write(VERSION + "\n");
		// gesture package
		String name = "bob";
		writer.write("name\t" + name + "\n");
		writer.write("training\n");
		// gesture set
		writer.write("name\t" + name + "\n");
		for (Gesture gesture : gestures) {
			writer.write("category\n");
			gesture.quillWrite(writer);
		}
		// end category
		writer.write("endset\n");
		// end set
		writer.write("test\n");
		writer.write("endpackage\n");
		writer.close();
	}

	public void setDatabase(GestureDatabase database) {
		// TODO Auto-generated method stub
		this.database = database;
	}

	public void setAuthor(String author) {
		// TODO Auto-generated method stub
		this.author = author;
	}
}
