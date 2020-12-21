/**
 * SessionModule
 *
 * Interface for a module that organizes data into "sessions".
 */
package net.perkowitz.issho.controller.apps.hachi.modules;

public interface SessionModule {

    int getSessionIndex();
    void setSessionIndex(int index);
}
