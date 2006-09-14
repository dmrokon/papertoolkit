package edu.stanford.hci.r3.designer.acrobat;

import java.io.File;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Test_RegionConfigurationWriter {

	public static void main(String[] args) {
		RegionConfigurationWriter writer = new RegionConfigurationWriter(new File("data/designer/ExampleSubmit.xml"));
		writer.processXML();
	}
}
