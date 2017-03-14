package net.perkowitz.issho.util;

import com.google.common.io.Files;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.modules.mono.MonoMemory;
import net.perkowitz.issho.hachi.modules.mono.MonoPattern;
import net.perkowitz.issho.hachi.modules.mono.MonoSession;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;

/**
 * Created by optic on 3/13/17.
 */
public class MemoryApp {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String type;
    private String filename;
    private MemoryObject memory;


    public static void main(String args[]) throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: MemoryApp <type> <filename>");
            System.exit(0);
        }

        MemoryApp memoryApp = new MemoryApp(args[0], args[1]);
        memoryApp.run();
    }


    public MemoryApp(String type, String filename) {
        this.type = type.toLowerCase();
        this.filename = filename;
    }

    public void run() {

        load(filename);
        print(memory, "");

    }


    public void print(MemoryObject object, String indent) {
        for (MemoryObject child : object.list()) {
            if (child.nonEmpty()) {
                System.out.println(indent + child);
                print(child, indent + "  ");
            }
        }
    }


    private void save(String filename) {

        try {

            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }

            objectMapper.writeValue(file, memory);
//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(memory);
//            System.out.println(json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void load(String filename) {

        File file = new File(filename);

        try {
            if (type.equals("mono")) {
                memory = objectMapper.readValue(file, MonoMemory.class);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
