# BrutalCatFixes

Универсальный мод для исправлений производительности и багов в модах для Minecraft 1.7.10.

## 📋 Описание

BrutalCatFixes — это модульный мод, использующий Mixin для исправления проблем производительности и багов в различных модах. Каждый фикс организован в отдельную папку по моду, что упрощает добавление новых исправлений.

## 🔧 Текущие исправления

### Thaumcraft - Utils.getFirstUncoveredBlockHeight()

**Проблема:** Критическое падение TPS при генерации Deep Dark (dimension 100) из Extra Utilities.

**Причина:**
- **БАГ:** Условие цикла `|| var3 > 250` вместо `&& var3 <= 250` — цикл может выйти за границы мира (Y > 255)
- **НЕЭФФЕКТИВНОСТЬ:** До 240 итераций × 8 вызовов = **1920 вызовов `isAirBlock()` на каждый чанк**

**Решение:** Заменили цикл (O(240)) на `world.getHeightValue(x, z)` (O(1))

```java
@Overwrite
public static int getFirstUncoveredBlockHeight(World world, int x, int z) {
    // Используем heightmap для O(1) вместо O(240) цикла
    return world.getHeightValue(x, z);
}
```

**Результат:** Значительное улучшение TPS при генерации мира.

## 📁 Структура проекта

```
src/main/java/com/github/brutalcatfixes/
├── BrutalCatFixes.java
└── mixins/
    ├── thaumcraft/           # Миксины для Thaumcraft
    │   └── MixinUtils.java
    ├── extrautilities/       # Готово для добавления
    ├── cofhcore/             # Готово для добавления
    └── ...                   # Другие моды
```

## 🚀 Установка

1. Скачайте собранный jar из [releases](../../releases) или соберите самостоятельно
2. Скопируйте в папку `mods/` сервера/клиента
3. Убедитесь что установлен **UniMixins** (обычно уже есть в GTNH)

### Зависимости

- Minecraft 1.7.10
- Minecraft Forge 10.13.4.1614+
- UniMixins (автоматически)

## 🛠️ Сборка

```bash
./gradlew.bat build
```

Собранный jar будет в `build/libs/brutalcatfixes-*.jar`

## ➕ Как добавить новый миксин

### Быстрый старт

1. **Добавьте зависимость на мод** в `dependencies.gradle`:

```gradle
dependencies {
    // Для CurseForge модов:
    transformedModCompileOnly("curse.maven:extra-utilities-225561:2264384")

    // Для GTNH модов:
    transformedModCompileOnly("com.github.GTNewHorizons:SomeMod:version:dev")

    // Для известных модов:
    transformedModCompileOnly("thaumcraft:Thaumcraft:1.7.10-4.2.3.5:dev")
}
```

**Примеры зависимостей:**
- CurseForge: `"curse.maven:extra-utilities-225561:2264384"`
- GTNH: `"com.github.GTNewHorizons:GT5-Unofficial:5.09.52.190:dev"`
- IC2: `"net.industrial-craft:industrialcraft-2:2.2.828-experimental:dev"`
- Thaumcraft: `"thaumcraft:Thaumcraft:1.7.10-4.2.3.5:dev"`

2. **Создайте подпапку** для миксинов мода:

```bash
mkdir -p src/main/java/com/github/brutalcatfixes/mixins/extrautilities
```

3. **Создайте миксин класс**:

```java
package com.github.brutalcatfixes.mixins.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rwtema.extrautils.SomeClass;

@Mixin(value = SomeClass.class, remap = false)
public class MixinSomeClass {

    @Inject(method = "problematicMethod", at = @At("HEAD"), cancellable = true)
    private void fixProblem(CallbackInfo ci) {
        // Ваш фикс здесь
    }
}
```

**Важно:**
- Используйте `remap = false` для модов (не vanilla)
- Используйте `remap = true` только для vanilla классов Minecraft

4. **Добавьте миксин в конфигурацию** `src/main/resources/mixins.brutalcatfixes.json`:

```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "com.github.brutalcatfixes.mixins",
  "refmap": "mixins.brutalcatfixes.refmap.json",
  "target": "@env(DEFAULT)",
  "compatibilityLevel": "JAVA_8",
  "mixins": [
    "thaumcraft.MixinUtils",
    "extrautilities.MixinSomeClass"
  ],
  "client": [],
  "server": []
}
```

5. **Соберите мод**:

```bash
./gradlew.bat build
```

### Примеры из других модов

Hodgepodge использует такую же структуру:
- `mixins/early/cofhcore/` - миксины для CoFH Core
- `mixins/early/thaumcraft/` - миксины для Thaumcraft
- `mixins/early/minecraft/` - миксины для vanilla Minecraft

## 📝 Технические детали

### Конфигурация

**gradle.properties:**
```properties
modName = BrutalCatFixes
modId = brutalcatfixes
modGroup = com.github.brutalcatfixes
mixinsPackage = mixins.thaumcraft
```

**dependencies.gradle:**
```gradle
configurations {
    transformedModCompileOnly  # Как в Hodgepodge
}

dependencies {
    transformedModCompileOnly("thaumcraft:Thaumcraft:1.7.10-4.2.3.5:dev")
}
```

### Почему Maven зависимости, а не локальные jar?

**Было (неправильно):**
```gradle
devOnlyNonPublishable(rfg.deobf(project.files("libs/Thaumcraft.jar")))
```
- ❌ Локальный jar файл
- ❌ Нужно вручную копировать
- ❌ Не обновляется автоматически

**Стало (правильно, как в Hodgepodge):**
```gradle
transformedModCompileOnly("thaumcraft:Thaumcraft:1.7.10-4.2.3.5:dev")
```
- ✅ Maven зависимость
- ✅ Загружается автоматически
- ✅ Используется только для компиляции
- ✅ Не включается в итоговый jar

## ✨ Преимущества архитектуры

1. **Модульность:** Каждый мод в отдельной папке
2. **Легко расширять:** Просто добавь папку и зависимость
3. **Нет локальных jar:** Всё через Maven
4. **Проверенный подход:** Следует архитектуре GTNH Hodgepodge
5. **Универсальность:** Один мод для фиксов любых модов

## 🔍 Полезные ссылки

- [Mixin Documentation](https://github.com/SpongePowered/Mixin/wiki)
- [MixinExtras Wiki](https://github.com/LlamaLad7/MixinExtras/wiki)
- [Hodgepodge](https://github.com/GTNewHorizons/Hodgepodge) - примеры миксинов
- [GTNH Maven Repository](https://nexus.gtnewhorizons.com)

## 📜 Лицензия

MIT License — см. [LICENSE](LICENSE)

## 👥 Авторы

- BrutalCatFixes mod
- Оригинальные моды принадлежат их авторам
