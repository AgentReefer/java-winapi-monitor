
# Java System Monitor via WinAPI (JNA)

## 🔧 Описание проекта

Консольное Java-приложение, использующее **WinAPI** через **JNA** для сбора системной информации на Windows:

- 💻 CPU (модель, количество ядер, частота)
- 🧠 RAM (тип, всего, скорость)
- 💽 Диски (объём, свободное место, тип)
- 🖥️ ОС (версия, архитектура)
- 🌐 Сеть (имя адаптера, MAC, IP)

Результаты отображаются через `MessageBox` в виде красиво отформатированных блоков.

---

## 📦 Зависимости

- Java 17+
- Maven
- JNA (`com.sun.jna:jna:5.13.0`)
- SLF4J + Logback

---

## 🚀 Запуск

1. Клонируйте репозиторий:

```bash
git clone https://github.com/yourname/java-winapi-monitor.git
cd java-winapi-monitor
```

2. Соберите проект:

```bash
mvn clean package
```

3. Запустите:

```bash
mvn exec:java -Dexec.mainClass="com.monitor.App"
```

> ⚠️ Только для ОС **Windows**

---

## 🛠 Что под капотом

- JNA (Java Native Access) используется для вызова функций из:
  - `Kernel32.dll` (для `GetVersionExW`, `GlobalMemoryStatusEx`)
  - `Iphlpapi.dll` (для `GetAdaptersInfo`)
- Структуры JNA (`Structure`, `Pointer`, `Memory`) описывают соответствующие C-структуры Windows
- Вся информация логируется и форматируется блоками для читаемости

---

---

## 📁 Структура

- `com.monitor.App` — точка входа
- `com.monitor.system.*` — сервисы (CPU, RAM, Disk, OS, Network)
- `com.monitor.win.*` — обертки WinAPI
- `com.monitor.util.Formatter` — форматированный вывод

---

