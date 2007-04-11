package edu.stanford.hci.r3.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.render.ink.InkRenderer;
import edu.stanford.hci.r3.util.graphics.GraphicsUtils;

/**
 * <p>
 * Renders Ink in a JPanel using catmull-rom splines. You may swap in simpler renderers if this becomes slow.
 * Also, this provides some customization that allows you to zoom in and out, pan, and align the ink with
 * imagery. =)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkPanel extends JPanel {

	private Ink currentInk;

	/**
	 * Zoom in (> 1.0) or out (< 1.0) to the ink canvas.
	 */
	private double inkScale = 1.0;

	/**
	 * A collection of ink objects that have been added to this JPanel. Feel free to access it, if you are a
	 * subclass. Make sure to call repaint though!
	 */
	protected List<Ink> inkWell = Collections.synchronizedList(new LinkedList<Ink>());

	private boolean invertInkColors = false;

	/**
	 * 
	 */
	private InkRenderer renderer;

	/**
	 * Default Catmull-Rom method.
	 */
	public InkPanel() {
		this(new InkRenderer(), Color.WHITE);
	}

	/**
	 * Choose your own renderer.
	 * 
	 * @param inkRenderer
	 * @param bgColor
	 */
	public InkPanel(InkRenderer inkRenderer, Color bgColor) {
		setBackground(bgColor);
		renderer = inkRenderer;
	}

	/**
	 * @param ink
	 */
	public void addInk(Ink ink) {
		currentInk = ink;
		inkWell.add(ink);
		repaint();
	}

	/**
	 * @return the newly added Ink object.
	 */
	public Ink addNewInk() {
		currentInk = new Ink();
		inkWell.add(currentInk);
		repaint();
		return currentInk;
	}

	/**
	 * Remove all the ink from the ink panel.
	 */
	public void clear() {
		inkWell.clear();
		repaint();
	}

	/**
	 * 
	 */
	public void displayInvertedInkColor() {
		invertInkColors = true;
	}

	/**
	 * @return the reference to the ink. This is NOT a copy!
	 */
	public List<Ink> getAllInk() {
		return inkWell;
	}

	public double getScale() {
		return inkScale;
	}

	/**
	 * Redraw the ink strokes.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHints(GraphicsUtils.getBestRenderingHints());

		final AffineTransform transform = g2d.getTransform();
		g2d.scale(inkScale, inkScale);

		// the drawing of ink is "atomic" with respect to
		// adding and removing from the inkWell
		synchronized (inkWell) {
			for (Ink ink : inkWell) {
				renderer.setInk(ink);
				renderer.useInvertedInkColors();
				renderer.renderToG2D(g2d);
			}
		}
		g2d.setTransform(transform);
	}

	/**
	 * Used to remove the most-recently added ink object.
	 */
	public void removeLastBatchOfInk() {
		synchronized (inkWell) {
			if (inkWell.size() > 0) {
				inkWell.remove(inkWell.size() - 1);
				repaint();
			}
		}
	}

	/**
	 * @param theInk
	 */
	public void setAllInk(List<Ink> theInk) {
		inkWell = theInk;
		repaint();
	}

	/**
	 * @param r
	 */
	public void setRenderer(InkRenderer r) {
		renderer = r;
	}

	/**
	 * @param theScale
	 */
	public void setScale(double theScale) {
		inkScale = theScale;
	}
}
