package net.perkowitz.issho.hachi;

import java.util.List;

/**
 * Created by mperkowi on 3/18/17.
 */
public class MemoryUtil {

    public static String countRender(MemoryObject memoryObject, String suffix) {

        String string = memoryObject.toString();

        List<MemoryObject> list = memoryObject.list();
        if (list.size() > 0) {
            int nonEmpty = 0;
            for (MemoryObject child : list) {
                if (child.nonEmpty()) {
                    nonEmpty++;
                }
            }
            string += String.format(":%02d/%02d Children", nonEmpty, list.size());
        }

        if (suffix != null) {
            string += ":" + suffix;
        }

        return string;
    }

    public static String countRender(MemoryObject memoryObject) {
        return countRender(memoryObject, null);
    }

}
