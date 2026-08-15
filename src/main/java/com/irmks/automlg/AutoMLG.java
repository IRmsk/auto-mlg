package com.irmks.automlg;

import com.irmks.automlg.event.MLGEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AutoMLG.MOD_ID)
public class AutoMLG {
    public static final String MOD_ID = "auto_mlg";
    public static final String AUTHOR = "IRmks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public AutoMLG() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册游戏事件到 Forge 总线
        MinecraftForge.EVENT_BUS.register(new MLGEventHandler());

        LOGGER.info("{} loaded - Author: {}", MOD_ID, AUTHOR);
    }
}
