package net.square.intect.processor.manager;

import net.square.intect.Intect;
import net.square.intect.checks.checks.fastbow.FastbowTypeA;
import net.square.intect.checks.checks.killaura.*;
import net.square.intect.checks.objectable.Check;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ModuleManager {

    public static final Class<?>[] CHECKS = new Class[]{
        FastbowTypeA.class,

        KillauraTypeA.class,
        KillauraTypeB.class,
        KillauraTypeC.class,
        KillauraTypeD.class,
        KillauraTypeE.class,
        KillauraTypeF.class,
        KillauraTypeG.class,
        KillauraTypeH.class,
        KillauraTypeI.class,
        KillauraTypeJ.class,
        KillauraTypeK.class,
        KillauraTypeL.class,
        KillauraTypeM.class,
        KillauraTypeN.class,
        KillauraTypeO.class,
        KillauraTypeP.class,
        KillauraTypeR.class,
        KillauraTypeS.class,
        KillauraTypeT.class
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static List<Check> loadChecks(final PlayerStorage data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                Check e = (Check) constructor.newInstance(data);
                if(e.getCheckInfo().bukkit()) {
                    Bukkit.getPluginManager().registerEvents(e, Intect.getIntect());
                }
                checkList.add(e);
            } catch (Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }

    public static void setup() {
        for (Class<?> clazz : CHECKS) {
            //if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
            try {
                CONSTRUCTORS.add(clazz.getConstructor(PlayerStorage.class));
                //Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
            //} else {
            //    Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
            //}
        }

        Intect.getIntect().getLogger().log(Level.INFO, "Setup " + CONSTRUCTORS.size() + " modules");
    }
}
