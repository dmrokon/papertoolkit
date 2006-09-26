package edu.stanford.hci.r3.paper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A Collection of Sheets (see Sheet.java). A collection of pages/sheets of interactive paper. A
 * bundle contains multiple Sheets. Bundles are one of the data types that can be added to an
 * Application.
 * </p>
 * <p>
 * One benefit of bundles is that they can manage page numbers... If you want to create a notebook,
 * or multiple data sheets, you will want a bundle. In the future, we will allow adding regions to a
 * Bundle. This means that you can have one region duplicated across sheets. Whenever a person
 * interacts with ANY sheet in the area occupied by that region, the region's event handler will be
 * notified.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Bundle {

	/**
	 * A list of all the global regions contained in this bundle, that applies to EVERY sheet. This
	 * is the master list, although we duplicate it elsewhere for convenience.
	 */
	private List<Region> globalRegions = new ArrayList<Region>();

	/**
	 * For debugging or displaying in the user interface.
	 */
	private String name;

	/**
	 * The sheets contained in this bundle.
	 */
	private List<Sheet> sheets = new ArrayList<Sheet>();

	/**
	 * 
	 */
	public Bundle(String bundleName) {
		name = bundleName;
	}

	/**
	 * These regions will be placed in a master list that applies to ALL sheets. Thus, whenever a
	 * person writes on Sheet S within the region specified by R, it will call R's event handler.
	 * 
	 * This would act like a button that is placed on every sheet (e.g., a share with group button
	 * on notebook pages).
	 * 
	 * Plus, for rendering purposes, the global region will ensure that pattern will exist on every
	 * sheet in the coordinates specified by the region.
	 * 
	 * @param r
	 *            the global region to add
	 */
	public void addGlobalRegion(Region gr) {
		globalRegions.add(gr);
	}

	/**
	 * @param sheetsToAdd
	 */
	public void addSheets(Sheet... sheetsToAdd) {
		for (Sheet s : sheetsToAdd) {
			sheets.add(s);
		}
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param s
	 * @return a sheet number (starting from 0). Depends on the order in which you added the sheets.
	 *         Currently, there is no facility to reorganize sheets within a bundle.
	 */
	public int getSheetNumber(Sheet s) {
		return sheets.indexOf(s);
	}

	/**
	 * @param sheetsToRemove
	 */
	public void removeSheets(Sheet... sheetsToRemove) {
		for (Sheet s : sheetsToRemove) {
			sheets.remove(s);
		}
	}
}