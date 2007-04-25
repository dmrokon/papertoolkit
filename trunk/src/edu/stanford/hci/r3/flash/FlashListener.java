package edu.stanford.hci.r3.flash;

/**
 * <p>
 * Listens for commands coming from flash.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public interface FlashListener {
	
	/**
	 * @param command
	 * @return if the event was "consumed" If so, do not process it anymore...
	 */
	public boolean messageReceived(String command);
}