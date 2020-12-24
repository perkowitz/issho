/**
 * SaveableModule
 *
 * Interface for a module that can save to and load from files.
 */
package net.perkowitz.issho.controller.apps.hachi.modules;

public interface SaveableModule {

    int getFileIndex();
    void setFileIndex(int index);
    void load();
    void save();
}
