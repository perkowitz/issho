package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.perkowitz.issho.controller.apps.hachi.modules.MockModule;
import net.perkowitz.issho.controller.apps.hachi.modules.Module;
import net.perkowitz.issho.controller.apps.hachi.modules.step.StepModule;
import net.perkowitz.issho.controller.midi.MidiOut;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class Config {

    private static final String MODULES = "modules";

    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonNode mainConfig;
    @Getter private List<Module> modules;
    @Getter private List<ModuleTranslator> moduleTranslators;

    public Config(JsonNode mainConfig) {
        this.mainConfig = mainConfig;
    }


    public void loadModules(HachiController controller, MidiOut midiOut) {

        modules = Lists.newArrayList();
        moduleTranslators = Lists.newArrayList();

        Palette []ps = new Palette[]{
                Palette.BLUE, Palette.MAGENTA, Palette.CYAN, Palette.PURPLE,
                Palette.ORANGE, Palette.YELLOW, Palette.PINK, Palette.RED
        };

        ArrayNode moduleConfigs = (ArrayNode) mainConfig.get(MODULES);
        Iterator<JsonNode> iter = moduleConfigs.getElements();
        while (iter.hasNext()) {
            JsonNode node = iter.next();
            String className = node.get("class").getTextValue();
            String paletteName = "";
            if (node.get("palette") != null) {
                paletteName = node.get("palette").getTextValue();
            }
            String filePrefix = node.get("filePrefix").getTextValue();
            if (filePrefix == null) {
                filePrefix = className.toLowerCase() + (modules.size() + 1);
            }

            Palette palette = Palette.fromName(paletteName);
            if (palette == null) {
                palette = ps[modules.size() % ps.length];
            }

            if (className.equals("StepModule")) {
                ModuleTranslator moduleTranslator = new ModuleTranslator(controller);
                moduleTranslator.setEnabled(false);
                Module module = new StepModule(moduleTranslator, midiOut, palette, filePrefix);
                module.setPalette(palette);
                modules.add(module);
                moduleTranslators.add(moduleTranslator);

            } else if (className.equals("MockModule")) {
                ModuleTranslator moduleTranslator = new ModuleTranslator(controller);
                moduleTranslator.setEnabled(false);
                Module module = new MockModule(moduleTranslator, palette, false);
                modules.add(module);
                moduleTranslators.add(moduleTranslator);

            }
        }

    }


    /***** static methods *****/

    public static Config fromJsonFile(String filename) throws IOException {

        InputStream inputStream = null;
        try {

            File file = new File(filename);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = Config.class.getClassLoader().getResourceAsStream(filename);
            }

            if (inputStream != null) {
                return new Config(objectMapper.readTree(inputStream));
            }
            throw new FileNotFoundException("Config file '" + filename + "' not found in the classpath or path");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return null;
    }

}
