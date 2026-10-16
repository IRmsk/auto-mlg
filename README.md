# Auto MLG

自动落地水模组 — 坠落时自动在脚下放水，免伤、不消耗物品、不需要作弊权限。

## 功能

- 玩家坠落超过 3 格时，自动在脚下生成水方块并取消坠落伤害
- 不消耗水桶或任何物品，生存模式直接生效
- 不需要开启作弊（/gamemode creative 或 OP 权限）
- 水方块在 0.6 秒（12 tick）后自动清除，不会到处流

## 环境要求

- Minecraft 1.20.1
- Forge 47.2.0+

## 安装

1. 安装 [Forge 1.20.1](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
2. 将 `auto_mlg-1.0.0.jar` 放入 `.minecraft/mods/` 文件夹
3. 启动游戏

## 构建

```bash
# JDK 17
./gradlew build
# 产物：build/libs/auto_mlg-1.0.0.jar
```

## 作者

IRmsk

## License

MIT
