# ModpackComprehensiveOptimization

`mco` 在同一个 JAR 中提供 GCT-Core 的跨模组优化和 Echoes of Yore 的五项启动优化。目标环境为 Minecraft 1.12.2、Cleanroom 0.6.12-alpha 和 Java 25。将 `build/libs/mco-1.0.0.jar` 放入实例的 `mods`。构建生成的 `-sources.jar` 用于阅读源码，`-dev.jar` 用于开发环境。

## 构建

在项目根目录用 JDK 25 执行以下 PowerShell 命令。将参数指向包含目标版本 `ChanceCubes-1.12.2-5.0.2.385-patched.jar` 和 `farmers-future-delight-1.6.1.jar` 的目录。其余模组的编译依赖由 Gradle 从 CurseMaven 解析。已缓存依赖时可以加 `--offline`。

```powershell
.\gradlew.bat clean build "-PechoesMods=G:/MC/.minecraft/versions/Echoes of Yore/mods" --console=plain
```

编译依赖见 [dependencies.gradle](gradle/scripts/dependencies.gradle)。安装时按实际存在的目标组合启用 Mixin。HEI 专用补丁还检查 HEI 的标志类。四个启动优化目标模组在生产环境核对目标类的 SHA-256，匹配失败时记录原因。原版图集补丁仅在客户端应用。

## 启动优化

下表中的 JVM 参数在下一次启动时生效，默认启用。日志分类为 `mco`。

| 目标与实现 | 算法及作用范围 | 单独关闭 |
|---|---|---|
| [GeckoLib](src/main/java/dev/mco/mixin/geckolib/BoneLookup.java) | 为当前模型已接入的骨骼建立索引。遇到重复或空名称时沿用树遍历。 | `-Dmco.geckolib3=false` |
| [Chance Cubes](src/main/java/dev/mco/mixin/chancecubes/SoundPackRefresh.java) | 首次预初始化时检查已生成的 ZIP。确认空包后复用星辉已上传且仍会预载的纹理。目标整合包保留 Forge 资源挂载和监听器通知，供 GeckoLib 首次加载模型与动画。后续装配照常刷新。 | `-Dmco.chancecubes=false` |
| [Mantle](src/main/java/dev/mco/mixin/mantle/SingleInputMatch.java) | 单输入匹配直接检查当前模板槽、物品和 NBT，成功时返回原类型的物品副本列表。 | `-Dmco.mantle=false` |
| [Farmer’s Future Delight](src/main/java/dev/mco/mixin/farmersfuturedelight) | 在一次烘焙内按命名空间和路径查询含水模型，登记变更使索引失效。 | `-Dmco.farmers_future_delight=false` |
| [纹理图集](src/main/java/dev/mco/mixin/atlas/AtlasSlot.java) | 每次尝试插入时读取一次纹理宽高，递归复用尺寸，并缓存子树上一次插入失败的尺寸。优先保留内部槽的精确匹配。 | `-Dmco.atlas=false` |

## GCT-Core 移植范围

核对默认分支自 `58b4866` 至 `23acf23`（含两端）的提交，选取以下 14 个提交中针对其他模组的改动。StellarCore 自身补丁、单独针对原版或 Forge 的优化、地牢与战利品内容均已排除。移植的 GeckoLib 功能与上表共用一处实现。[上游提交](https://github.com/Greediest-Studio/GCT-Core/commits/23acf23)

| 提交 | 选取内容 |
|---|---|
| `58b4866` | HEI 物品和流体查询缓存、匠魂熔炼燃料缓存、林业和 Smeltery IO 配方展示、Mekanism QIO 配方扫描 |
| `42972db` | Avaritia、Raids、The Spice of Life 和 Thaumic Concilium 清理世界或玩家引用 |
| `1a252f0` | 星辉魔法技能树渲染缓存、BetterNether 城市生成缓存、Compact Machines 3 扫描数据释放 |
| `8d80f3e` | HEI 启动、Tinkers’ Complement 熔炼覆盖索引、热力转置器容器查询 |
| `4ffea63` | GeckoLib 层级查找、NuclearCraft 脚本扩展资源合并 |
| `07fe7fc` | Additions 进度资源归档复用、ProjectE 知识物品索引 |
| `6f38636` | JAOPCA 资源存在性缓存和重载失效 |
| `7ada31f` | InGame Info XML 物品键保存独立 NBT 快照 |
| `841eddd` | Extra Trees 锯木配方索引、InGame Info XML 重复行修正、缓存维护 |
| `5c10dc9` | HEI 配方查询、RecipeStages 输出索引、匠魂材料熔炼配方索引 |
| `99626f5` | Raids 随维度卸载释放世界数据引用 |
| `dce4a9d` | ProjectE EMC 映射 |
| `75be9d0` | EnderStorage 储罐可见性同步 |
| `23acf23` | 热力转置器配方预览及焦点选择 |

林业装瓶机、Smeltery IO 铸造机、热力转置器抽取配方页会从配方浏览器隐藏。转置器填充页默认轮播最多四个示例，按流体输入或物品输出查询时显示匹配项。这些是所选上游提交带来的可见改动。测试覆盖与实测数据见[验收报告](验收报告.md)。
