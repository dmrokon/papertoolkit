package papertoolkit.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.attribute.Attribute;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ArrayUtils {

	/**
	 * @param <T>
	 *            type of the objects...
	 * @param arrayOfObjects
	 *            the array we want to convert into the List<T>
	 * @return an arraylist of <T>s
	 */
	public static <T> List<T> convertArrayToList(T[] arrayOfObjects) {
		final ArrayList<T> list = new ArrayList<T>();
		Collections.addAll(list, arrayOfObjects);
		return list;
	}

	/**
	 * @param array
	 */
	public static void printArray(double[] array) {
		System.out.print("Double Array: [");
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i]);
			if (i != array.length - 1) {
				System.out.print(", ");
			}
		}
		System.out.println("]");
	}

	/**
	 * Prints an array of Objects to console.
	 * 
	 * @param array
	 */
	public static void printArray(Object[] array) {
		String className = array[0].getClass().toString();
		System.out.print(className.substring(className.lastIndexOf(".") + 1, className.length())
				+ " Array: [");
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i]);
			if (i != array.length - 1) {
				System.out.print(", ");
			}
		}
		System.out.println("]");
	}

	/**
	 * Object can be of mixed type. Print them out with the class names.
	 * 
	 * @param array
	 * @author ronyeh
	 */
	public static void printArrayOfUnknownObjects(Object[] array) {

		final StringBuffer sb = new StringBuffer();
		sb.append("Object Array: [");
		if (array.length == 0) {
			System.out.println(sb.toString() + "]");
		} else {
			for (Object o : array) {
				final String fullyQualifiedName = o.getClass().toString();
				final String shortClassName = fullyQualifiedName.substring(fullyQualifiedName
						.lastIndexOf(".") + 1, fullyQualifiedName.length());

				sb.append("(" + shortClassName + ": " + o.toString() + ") ");
			}
			System.out.println(sb.substring(0, sb.length() - 1) + "]");
		}
	}

	/**
	 * @created Feb 7, 2006
	 * @author Ron Yeh
	 */
	public static void printArrayOfUnknownObjectsOnePerLine(Object[] array) {
		final StringBuffer sb = new StringBuffer();
		sb.append("Object Array: [\n");
		for (Object o : array) {
			final String fullyQualifiedName = o.getClass().toString();
			final String shortClassName = fullyQualifiedName.substring(fullyQualifiedName
					.lastIndexOf(".") + 1, fullyQualifiedName.length());

			sb.append("\t(" + shortClassName + ": " + o.toString() + ")\n");
		}
		System.out.println(sb.substring(0, sb.length() - 1) + "]");
	}

	/**
	 * Displays a matrix of Objects.
	 * 
	 * @param matrix
	 */
	public static void printMatrix(Object[][] matrix) {
		int numRows = matrix[0].length;
		int numCols = matrix.length;
		System.out.println("Matrix of " + matrix[0][0].getClass().getSimpleName() + ": [");
		for (int y = 0; y < numRows; y++) {
			System.out.print("    row " + y + ": [");
			for (int x = 0; x < numCols; x++) {
				System.out.print(matrix[x][y]);
				if (x != numCols - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("]");
		}
		System.out.println("]");
	}

	/**
	 * @param array
	 *            turns an array of ints into a String
	 */
	public static String toString(int[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i : array) {
			sb.append(i + ", ");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @param array
	 * @return
	 */
	public static String toString(Object[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object o : array) {
			sb.append(o + ", ");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}
}
