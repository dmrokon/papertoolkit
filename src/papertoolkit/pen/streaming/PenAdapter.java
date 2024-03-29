package papertoolkit.pen.streaming;

import papertoolkit.pen.PenSample;
import papertoolkit.pen.streaming.listeners.PenListener;

/**
 * <p>
 * Convenience Class
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenAdapter implements PenListener {

	public void penDown(PenSample sample) {
		// empty: Override yourself when you subclass this class!
	}

	public void penUp(PenSample sample) {
		// empty: Override yourself when you subclass this class!
	}

	public void sample(PenSample sample) {
		// empty: Override yourself when you subclass this class!
	}

}
