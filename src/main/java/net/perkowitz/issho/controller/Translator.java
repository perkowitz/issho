/**
 * A translator makes transformations between two controller implementations,
 * for example between an application that wants to use certain elements for
 * input and output, and a hardware implementation that has certain elements
 * available.
 */
package net.perkowitz.issho.controller;

public interface Translator extends Controller, ControllerListener {
}
