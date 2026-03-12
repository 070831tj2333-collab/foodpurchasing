# 在 Mac 上安装 Java 17

你的电脑是 **Apple Silicon (arm64)**，任选下面一种方式即可。

---

## 方式一：官网下载安装包（推荐，最简单）

1. 打开浏览器访问：**https://adoptium.net/zh-CN/temurin/releases/?version=17&os=mac&arch=aarch64**
2. 下载 **.pkg** 安装包（例如 `OpenJDK17U-jdk_aarch64_mac_hotspot_17.0.x_x.pkg`）。
3. 双击运行安装包，按提示完成安装。
4. 打开**终端**，执行：
   ```bash
   java -version
   ```
   若显示 `openjdk version "17.x.x"` 即表示安装成功。

---

## 方式二：用 Homebrew 安装（需先安装 Homebrew）

若已安装 Homebrew，在终端执行：

```bash
brew install openjdk@17
```

然后根据提示把 JDK 加入 PATH，例如（按你终端类型二选一）：

```bash
# 若使用 zsh（默认）
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

还没装 Homebrew 的话，可先安装：https://brew.sh/  
（在终端粘贴其首页提供的安装命令即可。）

---

## 方式三：把 JDK 下载到项目里使用（无需管理员权限）

1. 在浏览器打开：**https://adoptium.net/zh-CN/temurin/releases/?version=17&os=mac&arch=aarch64**
2. 下载 **.tar.gz** 压缩包（不要选 .pkg）。
3. 解压后得到类似 `jdk-17.0.18+8` 的文件夹。
4. 把该文件夹**改名为 `jdk`**，并移动到项目根目录下，使路径为：
   ```
   食品交易网站/jdk/Contents/Home/bin/java
   ```
   即：解压后的目录里要有 `Contents/Home/bin/java`。
5. 在项目根目录执行（或先 `cd` 到项目根目录）：
   ```bash
   export JAVA_HOME="$(pwd)/jdk/Contents/Home"
   export PATH="$JAVA_HOME/bin:$PATH"
   ./mvnw -v
   ```
   若能看到 Maven 和 Java 版本，说明配置正确。  
   每次新开终端要用项目内 JDK 时，需要再执行上面两句 `export`，或把这两句加到 `~/.zshrc`。

---

安装好 Java 17 后，在项目根目录执行：

```bash
./mvnw -v
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

即可运行校园食品网站。
