package net.perkowitz.issho.controller.elements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Created by optic on 10/27/16.
 */
public class ElementSet {

    @Getter private List<Element> elements;
    private Map<Element, Element> elementMap;


    /***** constructors ****************************************/

    public ElementSet(Collection<Element> elements) {
        this.elements = new ArrayList<Element>(elements);
        computeMap();
    }

    public ElementSet(ElementSet controlSet) {
        this.elements = controlSet.getElements();
        computeMap();
    }


    /***** public methods ****************************************/

    public boolean contains(Element element) {
        return elements.contains(element);
    }

    public Element get(Element element) {
        return elementMap.get(element);
    }

    public Element get(int index) {
        return elements.get(index);
    }

    public Integer getIndex(Element control) {
        Element c = get(control);
        if (c != null) {
            return c.getIndex();
        } else {
            return null;
        }
    }

    public int size() {
        return elements.size();
    }


    /***** private methods ****************************************/

    public void computeMap() {
        elementMap = Maps.newHashMap();
        for (Element element : elements) {
            elementMap.put(element, element);
        }
    }




    /***** static factories ****************************************/

    public static ElementSet buttons(int group, int startIndex, int endIndex, boolean invertIndex) {
        List<Element> controls = Lists.newArrayList();
        for (int index = startIndex; index <= endIndex; index ++) {
            int buttonIndex = index;
            if (invertIndex) {
                buttonIndex = 7 - index;
            }
            controls.add(Button.at(group, index));
        }
        return new ElementSet(controls);
    }

    public static ElementSet buttons(int group, int startIndex, int endIndex) {
        return buttons(group, startIndex, endIndex, false);
    }

    public static ElementSet pads(int group, int startRow, int endRow, int startColumn, int endColumn) {
        List<Element> controls = Lists.newArrayList();
        int index = 0;
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startColumn; c <= endColumn; c++) {
                controls.add(Pad.at(group, r, c));
                index++;
            }
        }
        return new ElementSet(controls);
    }

    public static ElementSet fromMultiple(ElementSet... sets) {
        Set<Element> controls = Sets.newHashSet();
        for (ElementSet set : sets) {
            controls.addAll(set.elements);
        }
        return new ElementSet(controls);
    }

}
