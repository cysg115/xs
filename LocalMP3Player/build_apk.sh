#!/bin/bash

# ============================================
# 本地MP3播放器 APK 自动构建脚本
# 适用于 Linux/macOS 系统
# ============================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 开始构建本地MP3播放器APK...${NC}"
echo "========================================"

# 检查Java
check_java() {
    echo -n "检查Java环境... "
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
        echo -e "${GREEN}✓ Java $JAVA_VERSION${NC}"
        
        # 检查是否是JDK
        if command -v javac &> /dev/null; then
            echo -e "  ${GREEN}✓ 检测到JDK（包含javac）${NC}"
            return 0
        else
            echo -e "  ${YELLOW}⚠ 只检测到JRE，可能需要JDK${NC}"
            return 1
        fi
    else
        echo -e "${RED}✗ Java未安装${NC}"
        return 2
    fi
}

# 检查Android SDK
check_android_sdk() {
    echo -n "检查Android SDK... "
    
    # 检查环境变量
    if [[ -n "$ANDROID_HOME" ]]; then
        echo -e "${GREEN}✓ ANDROID_HOME=$ANDROID_HOME${NC}"
        
        # 检查build-tools
        if ls "$ANDROID_HOME/build-tools/"* &> /dev/null; then
            BUILD_TOOLS=$(ls "$ANDROID_HOME/build-tools/" | tail -1)
            echo -e "  ${GREEN}✓ Build-tools: $BUILD_TOOLS${NC}"
            return 0
        else
            echo -e "  ${YELLOW}⚠ 未找到build-tools${NC}"
            return 1
        fi
    elif [[ -n "$ANDROID_SDK_ROOT" ]]; then
        echo -e "${GREEN}✓ ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠ 未设置ANDROID_HOME环境变量${NC}"
        return 2
    fi
}

# 检查Gradle
check_gradle() {
    echo -n "检查Gradle... "
    if command -v gradle &> /dev/null; then
        GRADLE_VERSION=$(gradle --version | grep Gradle | head -1 | awk '{print $2}')
        echo -e "${GREEN}✓ Gradle $GRADLE_VERSION${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠ 未安装Gradle，将使用项目自带的gradlew${NC}"
        return 1
    fi
}

# 显示环境检查结果
show_environment() {
    echo ""
    echo "📋 环境检查结果："
    echo "----------------------------------------"
    
    check_java
    JAVA_STATUS=$?
    
    check_android_sdk
    SDK_STATUS=$?
    
    check_gradle
    GRADLE_STATUS=$?
    
    echo "----------------------------------------"
    
    # 总结
    if [[ $JAVA_STATUS -eq 0 ]] && [[ $SDK_STATUS -eq 0 ]]; then
        echo -e "${GREEN}✅ 环境检查通过，可以开始构建${NC}"
        return 0
    else
        echo -e "${YELLOW}⚠ 环境不完整，可能影响构建${NC}"
        
        if [[ $JAVA_STATUS -eq 2 ]]; then
            echo -e "${RED}  需要安装Java JDK 17+${NC}"
        fi
        
        if [[ $SDK_STATUS -eq 2 ]]; then
            echo -e "${RED}  需要设置ANDROID_HOME环境变量${NC}"
        fi
        
        echo ""
        echo "📚 环境配置指南："
        echo "1. 安装Java JDK 17: https://adoptium.net/"
        echo "2. 安装Android SDK: https://developer.android.com/studio"
        echo "3. 设置环境变量："
        echo "   export ANDROID_HOME=\$HOME/Android/Sdk"
        echo "   export PATH=\$PATH:\$ANDROID_HOME/tools:\$ANDROID_HOME/platform-tools"
        
        read -p "是否继续尝试构建？(y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
        return 1
    fi
}

# 清理之前的构建
clean_previous_build() {
    echo ""
    echo "🧹 清理之前的构建..."
    if [[ -f "gradlew" ]]; then
        ./gradlew clean 2>&1 | grep -E "(BUILD|FAILED|ERROR)" || true
    else
        echo -e "${YELLOW}⚠ 未找到gradlew，跳过清理${NC}"
    fi
}

# 构建APK
build_apk() {
    echo ""
    echo "🔨 开始构建APK..."
    echo "----------------------------------------"
    
    # 确保gradlew有执行权限
    chmod +x gradlew 2>/dev/null || true
    
    # 使用gradlew构建
    echo "执行: ./gradlew assembleDebug"
    echo ""
    
    if ./gradlew assembleDebug; then
        echo -e "${GREEN}✅ APK构建成功！${NC}"
        return 0
    else
        echo -e "${RED}❌ APK构建失败${NC}"
        return 1
    fi
}

# 显示构建结果
show_build_result() {
    echo ""
    echo "📦 构建结果："
    echo "----------------------------------------"
    
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    
    if [[ -f "$APK_PATH" ]]; then
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
        echo -e "${GREEN}✅ APK生成成功！${NC}"
        echo ""
        echo "📱 APK信息："
        echo "  文件: $APK_PATH"
        echo "  大小: $APK_SIZE"
        echo "  版本: 1.0 (debug)"
        echo "  Min SDK: 24 (Android 7.0+)"
        echo "  Target SDK: 35 (Android 15+)"
        echo ""
        echo "🚀 安装到手机："
        echo "  1. 连接手机并开启USB调试"
        echo "  2. 使用ADB安装: adb install $APK_PATH"
        echo "  3. 或复制APK到手机手动安装"
    else
        echo -e "${RED}❌ 未找到生成的APK文件${NC}"
        echo "可能的原因："
        echo "  1. 构建过程被中断"
        echo "  2. 缺少必要的依赖"
        echo "  3. 构建配置错误"
        echo ""
        echo "💡 建议："
        echo "  1. 检查上面的错误信息"
        echo "  2. 确保Android SDK已正确安装"
        echo "  3. 尝试使用Android Studio构建"
    fi
}

# 主函数
main() {
    echo "本地MP3播放器 APK构建工具"
    echo "设备目标: 三星S25U (Android 16)"
    echo "========================================"
    
    # 检查是否在项目目录
    if [[ ! -f "app/build.gradle.kts" ]]; then
        echo -e "${RED}错误：请在项目根目录运行此脚本${NC}"
        echo "项目结构应该包含：app/build.gradle.kts"
        exit 1
    fi
    
    # 显示环境检查
    show_environment
    
    # 清理并构建
    clean_previous_build
    build_apk
    
    # 显示结果
    show_build_result
    
    echo ""
    echo "========================================"
    echo -e "${GREEN}✨ 构建流程完成！${NC}"
    echo ""
    echo "📚 更多帮助请查看 BUILD_GUIDE_CN.md"
}

# 执行主函数
main "$@"